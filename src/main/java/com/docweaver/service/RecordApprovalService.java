// src/main/java/com/docweaver/service/RecordApprovalService.java
package com.docweaver.service;

import com.docweaver.domain.*;

public class RecordApprovalService {

    private final DocumentRepository documentRepository;
    private final DocumentRecordRepository recordRepository;

    public RecordApprovalService(DocumentRepository documentRepository,
                                 DocumentRecordRepository recordRepository) {
        this.documentRepository = documentRepository;
        this.recordRepository = recordRepository;
    }

    public DocumentRecord startReview(String recordId) {
        DocumentRecord record = getRecordOrThrow(recordId);
        record.startReview();
        syncDocumentStatus(record);
        recordRepository.save(record);
        return record;
    }

    public DocumentRecord approveRecord(String recordId) {
        DocumentRecord record = getRecordOrThrow(recordId);
        record.approve();
        syncDocumentStatus(record);
        recordRepository.save(record);
        return record;
    }

    public DocumentRecord markExported(String recordId) {
        DocumentRecord record = getRecordOrThrow(recordId);
        record.markExported();
        syncDocumentStatus(record);
        recordRepository.save(record);
        return record;
    }

    public DocumentRecord editField(String recordId, String key, String newValue) {
        DocumentRecord record = getRecordOrThrow(recordId);
        record.editField(key, newValue);
        recordRepository.save(record);
        return record;
    }

    private DocumentRecord getRecordOrThrow(String recordId) {
        DocumentRecord record = recordRepository.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("No record found with id: " + recordId);
        }
        return record;
    }

    private void syncDocumentStatus(DocumentRecord record) {
        Document doc = documentRepository.findById(record.getDocumentId());
        if (doc == null) {
            // Inconsistent state; in a real system you'd log/error
            return;
        }

        switch (record.getApprovalStatus()) {
            case DRAFT:
                // probably already EXTRACTED; leave as-is
                break;
            case IN_REVIEW:
                doc.setRecordId(record.getId());
                doc.markProcessing(); // or a dedicated IN_REVIEW status
                docRepositorySetStatus(doc, DocumentStatus.IN_REVIEW);
                break;
            case READY_FOR_EXPORT:
                docRepositorySetStatus(doc, DocumentStatus.READY_FOR_EXPORT);
                break;
            case EXPORTED:
                docRepositorySetStatus(doc, DocumentStatus.EXPORTED);
                break;
        }
        documentRepository.save(doc);
    }

    private void docRepositorySetStatus(Document doc, DocumentStatus status) {
        // Document has specific setters for some statuses; we add a helper or
        // just set via existing methods. To keep it simple, add a generic setter:
        try {
            var field = Document.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(doc, status);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to update document status reflectively.", e);
        }
    }
}
