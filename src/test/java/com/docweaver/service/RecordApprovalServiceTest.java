// src/test/java/com/docweaver/service/RecordApprovalServiceTest.java
package com.docweaver.service;

import com.docweaver.domain.*;
import com.docweaver.strategy.ExtractionEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordApprovalServiceTest {

    @Test
    void approvalLifecycle_changesStatusesAndLocksEdits() {
        DocumentRepository documentRepository = new DocumentRepository();
        DocumentRecordRepository recordRepository = new DocumentRecordRepository();
        ExtractionEngine extractionEngine = new ExtractionEngine();
        ExtractionJobProcessor extractionJobProcessor = new ExtractionJobProcessor(
                documentRepository, recordRepository, extractionEngine);
        RecordApprovalService approvalService = new RecordApprovalService(
                documentRepository, recordRepository);

        // Prepare extracted record via Module 2
        Workspace ws = new Workspace("ws-1", "Test WS");
        Document doc = new Document(ws.getId(), "invoice1.pdf");
        doc.markProcessing();
        doc.setDocumentType(DocumentType.INVOICE);
        documentRepository.save(doc);

        ProcessingJob job = new ProcessingJob(
                doc.getId(),
                ProcessingJob.JobType.CLASSIFY_AND_EXTRACT
        );
        extractionJobProcessor.process(job);

        Document updatedDoc = documentRepository.findById(doc.getId());
        DocumentRecord record = recordRepository.findById(updatedDoc.getRecordId());

        assertEquals(RecordApprovalStatus.DRAFT, record.getApprovalStatus());

        // Capture id once so it’s effectively final
        String recordId = record.getId();

        // startReview
        approvalService.startReview(recordId);
        record = recordRepository.findById(recordId);
        updatedDoc = documentRepository.findById(doc.getId());

        assertEquals(RecordApprovalStatus.IN_REVIEW, record.getApprovalStatus());
        assertEquals(DocumentStatus.IN_REVIEW, updatedDoc.getStatus());

        // edit during review
        approvalService.editField(recordId, "total_amount", "1500.00");
        record = recordRepository.findById(recordId);
        String total = record.getFields().stream()
                .filter(f -> f.getKey().equals("total_amount"))
                .findFirst()
                .map(Field::getValue)
                .orElse("<missing>");
        assertEquals("1500.00", total);

        // approve -> Ready for Export
        approvalService.approveRecord(recordId);
        record = recordRepository.findById(recordId);
        updatedDoc = documentRepository.findById(doc.getId());

        assertEquals(RecordApprovalStatus.READY_FOR_EXPORT, record.getApprovalStatus());
        assertEquals(DocumentStatus.READY_FOR_EXPORT, updatedDoc.getStatus());

        // editing now should fail
        assertThrows(IllegalStateException.class,
                () -> approvalService.editField(recordId, "total_amount", "1600.00"));

        // mark exported
        approvalService.markExported(recordId);
        record = recordRepository.findById(recordId);
        updatedDoc = documentRepository.findById(doc.getId());

        assertEquals(RecordApprovalStatus.EXPORTED, record.getApprovalStatus());
        assertEquals(DocumentStatus.EXPORTED, updatedDoc.getStatus());
    }
}
