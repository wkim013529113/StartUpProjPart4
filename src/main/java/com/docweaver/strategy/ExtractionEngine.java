// src/main/java/com/docweaver/strategy/ExtractionEngine.java
package com.docweaver.strategy;

import com.docweaver.domain.Document;
import com.docweaver.domain.DocumentRecord;
import com.docweaver.domain.DocumentType;

import java.util.EnumMap;
import java.util.Map;

public class ExtractionEngine {

    private final Map<DocumentType, ExtractionStrategy> strategies = new EnumMap<>(DocumentType.class);

    public ExtractionEngine() {
        register(DocumentType.INVOICE, new InvoiceExtractionStrategy());
        register(DocumentType.RECEIPT, new ReceiptExtractionStrategy());
        register(DocumentType.PURCHASE_ORDER, new PurchaseOrderExtractionStrategy());
        register(DocumentType.CONTRACT, new ContractExtractionStrategy());
    }

    public void register(DocumentType type, ExtractionStrategy strategy) {
        strategies.put(type, strategy);
    }

    public DocumentRecord extract(Document document) {
        DocumentType type = document.getDocumentType();
        if (type == null) {
            throw new IllegalStateException("Document type not set for document: " + document.getId());
        }

        ExtractionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No extraction strategy registered for type: " + type);
        }

        return strategy.extract(document);
    }
}
