// src/main/java/com/docweaver/strategy/ContractExtractionStrategy.java
package com.docweaver.strategy;

import com.docweaver.domain.*;

public class ContractExtractionStrategy implements ExtractionStrategy {

    @Override
    public DocumentRecord extract(Document document) {
        DocumentRecord record = new DocumentRecord(document.getId(), DocumentType.CONTRACT);
        record.addField(new Field("party_a", "Company A", 0.88, "page1:partyA"));
        record.addField(new Field("party_b", "Company B", 0.88, "page1:partyB"));
        return record;
    }
}
