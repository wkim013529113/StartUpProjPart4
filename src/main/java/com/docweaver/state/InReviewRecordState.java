// src/main/java/com/docweaver/state/InReviewRecordState.java
package com.docweaver.state;

import com.docweaver.domain.DocumentRecord;
import com.docweaver.domain.RecordApprovalStatus;

public class InReviewRecordState implements RecordState {

    private final DocumentRecord record;

    public InReviewRecordState(DocumentRecord record) {
        this.record = record;
    }

    @Override
    public void startReview() {
        // already in review; no-op or throw
        // we'll no-op for simplicity
    }

    @Override
    public void approve() {
        record.setApprovalStatus(RecordApprovalStatus.READY_FOR_EXPORT);
        record.setState(new ReadyForExportRecordState(record));
    }

    @Override
    public void markExported() {
        throw new IllegalStateException("Must approve record before export.");
    }

    @Override
    public void editField(String key, String newValue) {
        // Still allowed during review
        record.updateFieldValue(key, newValue);
    }
}
