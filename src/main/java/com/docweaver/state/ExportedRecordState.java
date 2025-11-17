// src/main/java/com/docweaver/state/ExportedRecordState.java
package com.docweaver.state;

import com.docweaver.domain.DocumentRecord;
import com.docweaver.domain.RecordApprovalStatus;

public class ExportedRecordState implements RecordState {

    private final DocumentRecord record;

    public ExportedRecordState(DocumentRecord record) {
        this.record = record;
    }

    @Override
    public void startReview() {
        throw new IllegalStateException("Cannot review an already exported record.");
    }

    @Override
    public void approve() {
        throw new IllegalStateException("Record is already exported.");
    }

    @Override
    public void markExported() {
        // already exported; no-op
    }

    @Override
    public void editField(String key, String newValue) {
        throw new IllegalStateException("Cannot edit an exported record.");
    }
}
