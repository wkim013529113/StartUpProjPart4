// src/main/java/com/docweaver/strategy/InvoiceExtractionStrategy.java
package com.docweaver.strategy;

import com.docweaver.domain.*;

public class InvoiceExtractionStrategy implements ExtractionStrategy {

    @Override
    public DocumentRecord extract(Document document) {
        DocumentRecord record = new DocumentRecord(document.getId(), DocumentType.INVOICE);

        record.addField(new Field("vendor_name", "ACME Corp", 0.95, "page1:vendor"));
        record.addField(new Field("invoice_date", "2025-01-10", 0.93, "page1:date"));
        record.addField(new Field("total_amount", "1234.56", 0.98, "page1:total"));

        record.addLineItem(new LineItem("Consulting Services", 10, 100.0, 1000.0, "USD"));
        record.addLineItem(new LineItem("Sales Tax", 1, 234.56, 234.56, "USD"));

        return record;
    }
}
