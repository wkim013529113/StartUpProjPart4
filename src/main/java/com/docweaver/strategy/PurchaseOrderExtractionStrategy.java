// src/main/java/com/docweaver/strategy/PurchaseOrderExtractionStrategy.java
package com.docweaver.strategy;

import com.docweaver.domain.*;

public class PurchaseOrderExtractionStrategy implements ExtractionStrategy {

    @Override
    public DocumentRecord extract(Document document) {
        DocumentRecord record = new DocumentRecord(document.getId(), DocumentType.PURCHASE_ORDER);
        record.addField(new Field("po_number", "PO-12345", 0.9, "page1:po_number"));
        record.addField(new Field("supplier_name", "Supplier Inc", 0.9, "page1:supplier"));
        return record;
    }
}
