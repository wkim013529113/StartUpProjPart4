// src/main/java/com/docweaver/demo/ExtractionDemoMain.java
package com.docweaver.demo;

import com.docweaver.domain.*;
import com.docweaver.service.*;
import com.docweaver.strategy.ExtractionEngine;

public class ExtractionDemoMain {

    public static void main(String[] args) {

        // Shared infrastructure
        DocumentRepository documentRepository = new DocumentRepository();
        DocumentRecordRepository recordRepository = new DocumentRecordRepository();
        ProcessingJobQueue jobQueue = new ProcessingJobQueue();
        ExtractionEngine extractionEngine = new ExtractionEngine();

        // Simulate uploaded document
        Workspace ws = new Workspace("ws-1", "Demo Workspace");
        Document doc = new Document(ws.getId(), "invoice1.pdf");
        doc.markProcessing();
        doc.setDocumentType(DocumentType.INVOICE);  // classification result
        documentRepository.save(doc);

        // Job created from Upload module
        ProcessingJob job = new ProcessingJob(
                doc.getId(),
                ProcessingJob.JobType.CLASSIFY_AND_EXTRACT
        );
        jobQueue.enqueue(job);

        // Processor (Module 2)
        ExtractionJobProcessor processor = new ExtractionJobProcessor(
                documentRepository,
                recordRepository,
                extractionEngine
        );

        // Process queue
        while (jobQueue.size() > 0) {
            ProcessingJob next = jobQueue.dequeue();
            processor.process(next);
        }

        // Show result
        Document updated = documentRepository.findById(doc.getId());
        System.out.println("Document status: " + updated.getStatus());
        System.out.println("Linked recordId: " + updated.getRecordId());

        DocumentRecord record = recordRepository.findById(updated.getRecordId());
        System.out.println("Record docType: " + record.getDocumentType());

        System.out.println("Fields:");
        record.getFields().forEach(f ->
                System.out.printf("  %s = %s (%.2f)%n",
                        f.getKey(), f.getValue(), f.getConfidence())
        );

        System.out.println("Line Items:");
        record.getLineItems().forEach(li ->
                System.out.printf("  %s x%d = %.2f %s%n",
                        li.getDescription(),
                        li.getQuantity(),
                        li.getAmount(),
                        li.getCurrency())
        );
    }
}
