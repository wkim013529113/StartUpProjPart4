// src/main/java/com/docweaver/domain/Field.java
package com.docweaver.domain;

public class Field {

    private final String key;
    private String value;
    private final double confidence;   // 0.0–1.0
    private final String sourceAnchor; // e.g. page/coords, simplified as string

    public Field(String key, String value, double confidence, String sourceAnchor) {
        this.key = key;
        this.value = value;
        this.confidence = confidence;
        this.sourceAnchor = sourceAnchor;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getSourceAnchor() {
        return sourceAnchor;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
