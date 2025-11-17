// src/main/java/com/docweaver/domain/RecordApprovalStatus.java
package com.docweaver.domain;

public enum RecordApprovalStatus {
    DRAFT,          // just created from extraction
    IN_REVIEW,      // reviewer is checking
    READY_FOR_EXPORT, // approved / locked and ready
    EXPORTED        // sent to external system / CSV
}
