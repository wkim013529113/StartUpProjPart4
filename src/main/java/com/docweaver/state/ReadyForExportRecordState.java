// src/main/java/com/docweaver/state/ReadyForExportRecordState.java
package com.docweaver.state;

import com.docweaver.domain.DocumentRecord;
import com.docweaver.domain.RecordApprovalStatus;

public class ReadyForExportRecordState implements RecordState {

    private final DocumentRecord record;

    public ReadyForExportRecordState(DocumentRecord record) {
        this.record = record;
    }

    @Override
    public void startReview() {
        throw new IllegalStateException("Record is already approved and ready for export.");
    }

    @Override
    public void approve() {
        // already approved; no-op
    }

    @Override
    public void markExported() {
        record.setApprovalStatus(RecordApprovalStatus.EXPORTED);
        record.setState(new ExportedRecordState(record));
    }

    @Override
    public void editField(String key, String newValue) {
        // Business choice: disallow edits after approval
        throw new IllegalStateException("Cannot edit fields after record is ready for export.");
    }
}
