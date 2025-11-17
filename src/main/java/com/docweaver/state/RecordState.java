// src/main/java/com/docweaver/state/RecordState.java
package com.docweaver.state;

public interface RecordState {

    void startReview();

    void approve();

    void markExported();

    void editField(String key, String newValue);
}
