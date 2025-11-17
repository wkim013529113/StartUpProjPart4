// src/main/java/com/docweaver/domain/ProcessingJob.java
package com.docweaver.domain;

import java.time.Instant;
import java.util.UUID;

public class ProcessingJob {

    public enum JobType {
        CLASSIFY_AND_EXTRACT
    }

    private final String id;
    private final String documentId;
    private final JobType jobType;
    private final Instant createdAt;

    public ProcessingJob(String documentId, JobType jobType) {
        this.id = UUID.randomUUID().toString();
        this.documentId = documentId;
        this.jobType = jobType;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public JobType getJobType() {
        return jobType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
