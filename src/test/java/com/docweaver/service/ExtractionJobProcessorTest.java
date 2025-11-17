// src/test/java/com/docweaver/service/ExtractionJobProcessorTest.java
package com.docweaver.service;

import com.docweaver.domain.*;
import com.docweaver.strategy.ExtractionEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtractionJobProcessorTest {

    @Test
    void process_createsRecordAndUpdatesDocument() {
        DocumentRepository documentRepository = new DocumentRepository();
        DocumentRecordRepository recordRepository = new DocumentRecordRepository();
        ExtractionEngine extractionEngine = new ExtractionEngine();

        Workspace ws = new Workspace("ws-1", "Test WS");
        Document doc = new Document(ws.getId(), "invoice1.pdf");
        doc.markProcessing();
        doc.setDocumentType(DocumentType.INVOICE);
        documentRepository.save(doc);

        ProcessingJob job = new ProcessingJob(doc.getId(), ProcessingJob.JobType.CLASSIFY_AND_EXTRACT);

        ExtractionJobProcessor processor = new ExtractionJobProcessor(
                documentRepository,
                recordRepository,
                extractionEngine
        );

        processor.process(job);

        Document updated = documentRepository.findById(doc.getId());
        assertEquals(DocumentStatus.EXTRACTED, updated.getStatus());
        assertNotNull(updated.getRecordId(), "RecordId should be set on document");

        DocumentRecord record = recordRepository.findById(updated.getRecordId());
        assertNotNull(record, "Record should be stored");
        assertEquals(doc.getId(), record.getDocumentId());
        assertEquals(DocumentType.INVOICE, record.getDocumentType());
    }

    @Test
    void process_throwsIfDocumentTypeMissing() {
        DocumentRepository documentRepository = new DocumentRepository();
        DocumentRecordRepository recordRepository = new DocumentRecordRepository();
        ExtractionEngine extractionEngine = new ExtractionEngine();

        Workspace ws = new Workspace("ws-1", "Test WS");
        Document doc = new Document(ws.getId(), "invoice1.pdf");
        doc.markProcessing();
        // no doc.setDocumentType(...)
        documentRepository.save(doc);

        ProcessingJob job = new ProcessingJob(doc.getId(), ProcessingJob.JobType.CLASSIFY_AND_EXTRACT);

        ExtractionJobProcessor processor = new ExtractionJobProcessor(
                documentRepository,
                recordRepository,
                extractionEngine
        );

        assertThrows(IllegalStateException.class, () -> processor.process(job));
    }
}
