// src/main/java/com/docweaver/state/DraftRecordState.java
package com.docweaver.state;

import com.docweaver.domain.DocumentRecord;
import com.docweaver.domain.RecordApprovalStatus;

public class DraftRecordState implements RecordState {

    private final DocumentRecord record;

    public DraftRecordState(DocumentRecord record) {
        this.record = record;
    }

    @Override
    public void startReview() {
        record.setApprovalStatus(RecordApprovalStatus.IN_REVIEW);
        record.setState(new InReviewRecordState(record));
    }

    @Override
    public void approve() {
        throw new IllegalStateException("Cannot approve a record that has not been reviewed.");
    }

    @Override
    public void markExported() {
        throw new IllegalStateException("Cannot export a draft record.");
    }

    @Override
    public void editField(String key, String newValue) {
        // Fully allowed in draft
        record.updateFieldValue(key, newValue);
    }
}
