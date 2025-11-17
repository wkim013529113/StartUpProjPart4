// src/main/java/com/docweaver/domain/Document.java
package com.docweaver.domain;

import java.util.UUID;

public class Document {

    private final String id;
    private final String workspaceId;
    private final String filename;

    private DocumentStatus status;
    private DocumentType documentType;   // set by classification step
    private String recordId;             // link to extracted Record

    public Document(String workspaceId, String filename) {
        this.id = UUID.randomUUID().toString();
        this.workspaceId = workspaceId;
        this.filename = filename;
        this.status = DocumentStatus.UPLOADED;
    }

    public String getId() {
        return id;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getFilename() {
        return filename;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void markProcessing() {
        this.status = DocumentStatus.PROCESSING;
    }

    public void markExtracted() {
        this.status = DocumentStatus.EXTRACTED;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    //-- reflection helper
    public void markInReview() {
        this.status = DocumentStatus.IN_REVIEW;
    }

    public void markReadyForExport() {
        this.status = DocumentStatus.READY_FOR_EXPORT;
    }

    public void markExported() {
        this.status = DocumentStatus.EXPORTED;
    }

}
