// src/test/java/com/docweaver/strategy/InvoiceExtractionStrategyTest.java
package com.docweaver.strategy;

import com.docweaver.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceExtractionStrategyTest {

    @Test
    void extract_populatesInvoiceFieldsAndLineItems() {
        InvoiceExtractionStrategy strategy = new InvoiceExtractionStrategy();
        Workspace ws = new Workspace("ws-1", "Test WS");
        Document doc = new Document(ws.getId(), "invoice1.pdf");
        doc.setDocumentType(DocumentType.INVOICE);

        DocumentRecord record = strategy.extract(doc);

        assertEquals(DocumentType.INVOICE, record.getDocumentType());
        assertEquals(doc.getId(), record.getDocumentId());
        assertFalse(record.getFields().isEmpty(), "Fields should not be empty");
        assertFalse(record.getLineItems().isEmpty(), "Line items should not be empty");

        assertTrue(record.getFields().stream().anyMatch(f -> f.getKey().equals("vendor_name")));
        assertTrue(record.getFields().stream().anyMatch(f -> f.getKey().equals("total_amount")));
    }
}
