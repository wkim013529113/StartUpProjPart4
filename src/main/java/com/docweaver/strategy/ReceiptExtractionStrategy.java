// src/main/java/com/docweaver/strategy/ReceiptExtractionStrategy.java
package com.docweaver.strategy;

import com.docweaver.domain.*;

public class ReceiptExtractionStrategy implements ExtractionStrategy {

    @Override
    public DocumentRecord extract(Document document) {
        DocumentRecord record = new DocumentRecord(document.getId(), DocumentType.RECEIPT);

        record.addField(new Field("merchant_name", "Coffee Shop", 0.92, "page1:merchant"));
        record.addField(new Field("purchase_date", "2025-02-01", 0.90, "page1:date"));
        record.addField(new Field("total_amount", "7.50", 0.97, "page1:total"));

        record.addLineItem(new LineItem("Latte", 1, 5.00, 5.00, "USD"));
        record.addLineItem(new LineItem("Tax", 1, 2.50, 2.50, "USD"));

        return record;
    }
}
