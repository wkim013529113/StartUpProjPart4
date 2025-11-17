// src/main/java/com/docweaver/strategy/ExtractionStrategy.java
package com.docweaver.strategy;

import com.docweaver.domain.Document;
import com.docweaver.domain.DocumentRecord;

public interface ExtractionStrategy {

    DocumentRecord extract(Document document);
}
