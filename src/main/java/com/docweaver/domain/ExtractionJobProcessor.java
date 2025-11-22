// src/main/java/com/docweaver/service/ExtractionJobProcessor.java
package com.docweaver.service;

import com.docweaver.domain.Document;
import com.docweaver.domain.DocumentRecord;
import com.docweaver.domain.DocumentStatus;
import com.docweaver.domain.ProcessingJob;
import com.docweaver.strategy.ExtractionEngine;

public class ExtractionJobProcessor {

    private final DocumentRepository documentRepository;
    private final DocumentRecordRepository recordRepository;
    private final ExtractionEngine extractionEngine;

    public ExtractionJobProcessor(DocumentRepository documentRepository,
                                  DocumentRecordRepository recordRepository,
                                  ExtractionEngine extractionEngine) {
        this.documentRepository = documentRepository;
        this.recordRepository = recordRepository;
        this.extractionEngine = extractionEngine;
    }

    public void process(ProcessingJob job) {
        Document document = documentRepository.findById(job.getDocumentId());
        if (document == null) {
            throw new IllegalArgumentException("No document found for job: " + job.getId());
        }

        DocumentRecord record = extractionEngine.extract(document);
        recordRepository.save(record);

        document.setRecordId(record.getId());
        document.markExtracted();
        documentRepository.save(document);
    }
}
