// src/main/java/com/docweaver/domain/DocumentRecord.java
package com.docweaver.domain;

import com.docweaver.state.RecordState;
import com.docweaver.state.DraftRecordState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentRecord {

    private final String id;
    private final String documentId;
    private final DocumentType documentType;
    private final Instant createdAt;

    private final List<Field> fields = new ArrayList<>();
    private final List<LineItem> lineItems = new ArrayList<>();

    // Approval lifecycle
    private RecordApprovalStatus approvalStatus;
    private RecordState state;

    public DocumentRecord(String documentId, DocumentType documentType) {
        this.id = UUID.randomUUID().toString();
        this.documentId = documentId;
        this.documentType = documentType;
        this.createdAt = Instant.now();
        this.approvalStatus = RecordApprovalStatus.DRAFT;
        this.state = new DraftRecordState(this);
    }

    public String getId() {
        return id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public void addLineItem(LineItem lineItem) {
        this.lineItems.add(lineItem);
    }

    public RecordApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(RecordApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public RecordState getState() {
        return state;
    }

    public void setState(RecordState state) {
        this.state = state;
    }

    // ---- State-delegated behavior ----

    public void startReview() {
        state.startReview();
    }

    public void approve() {
        state.approve();
    }

    public void markExported() {
        state.markExported();
    }

    public void editField(String key, String newValue) {
        state.editField(key, newValue);
    }

    // Helper used by states
    public void updateFieldValue(String key, String newValue) {
        fields.stream()
                .filter(f -> f.getKey().equals(key))
                .findFirst()
                .ifPresent(f -> f.setValue(newValue));
    }
}
