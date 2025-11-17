// src/main/java/com/docweaver/demo/ApprovalDemoMain.java
package com.docweaver.demo;

import com.docweaver.domain.*;
import com.docweaver.service.*;
import com.docweaver.strategy.ExtractionEngine;

public class ApprovalDemoMain {

    /***
     * Ties together Module 2 + Module 3 in one run
     * */
    public static void main(String[] args) {
        // Shared infra
        DocumentRepository documentRepository = new DocumentRepository();
        DocumentRecordRepository recordRepository = new DocumentRecordRepository();
        ProcessingJobQueue jobQueue = new ProcessingJobQueue();
        ExtractionEngine extractionEngine = new ExtractionEngine();
        ExtractionJobProcessor extractionJobProcessor = new ExtractionJobProcessor(
                documentRepository, recordRepository, extractionEngine);
        RecordApprovalService approvalService = new RecordApprovalService(
                documentRepository, recordRepository);

        // 1) Simulate upload + classification
        Workspace ws = new Workspace("ws-1", "Demo Workspace");
        Document doc = new Document(ws.getId(), "invoice1.pdf");
        doc.markProcessing();
        doc.setDocumentType(DocumentType.INVOICE);
        documentRepository.save(doc);

        // 2) Enqueue and process extraction job
        ProcessingJob job = new ProcessingJob(
                doc.getId(),
                ProcessingJob.JobType.CLASSIFY_AND_EXTRACT
        );
        jobQueue.enqueue(job);

        while (jobQueue.size() > 0) {
            ProcessingJob next = jobQueue.dequeue();
            extractionJobProcessor.process(next);
        }

        // 3) Fetch the record created by extraction
        Document updatedDoc = documentRepository.findById(doc.getId());
        DocumentRecord record = recordRepository.findById(updatedDoc.getRecordId());

        System.out.println("After extraction:");
        System.out.println("  Document status: " + updatedDoc.getStatus());
        System.out.println("  Record approval status: " + record.getApprovalStatus());

        // 4) Start review
        approvalService.startReview(record.getId());
        record = recordRepository.findById(record.getId());
        updatedDoc = documentRepository.findById(doc.getId());

        System.out.println("After startReview:");
        System.out.println("  Document status: " + updatedDoc.getStatus());
        System.out.println("  Record approval status: " + record.getApprovalStatus());

        // 5) Edit a field during review
        approvalService.editField(record.getId(), "total_amount", "1300.00");
        record = recordRepository.findById(record.getId());
        String total = record.getFields().stream()
                .filter(f -> f.getKey().equals("total_amount"))
                .findFirst()
                .map(Field::getValue)
                .orElse("<missing>");
        System.out.println("  Updated total_amount during review: " + total);

        // 6) Approve (Ready for Export)
        approvalService.approveRecord(record.getId());
        record = recordRepository.findById(record.getId());
        updatedDoc = documentRepository.findById(doc.getId());

        System.out.println("After approveRecord:");
        System.out.println("  Document status: " + updatedDoc.getStatus());
        System.out.println("  Record approval status: " + record.getApprovalStatus());

        // 7) Mark exported
        approvalService.markExported(record.getId());
        record = recordRepository.findById(record.getId());
        updatedDoc = documentRepository.findById(doc.getId());

        System.out.println("After markExported:");
        System.out.println("  Document status: " + updatedDoc.getStatus());
        System.out.println("  Record approval status: " + record.getApprovalStatus());
    }
}
