// src/main/java/com/docweaver/service/DocumentRecordRepository.java
package com.docweaver.service;

import com.docweaver.domain.DocumentRecord;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentRecordRepository {

    private final Map<String, DocumentRecord> store = new ConcurrentHashMap<>();

    public void save(DocumentRecord record) {
        store.put(record.getId(), record);
    }

    public DocumentRecord findById(String id) {
        return store.get(id);
    }

    public Collection<DocumentRecord> findAll() {
        return store.values();
    }

    public DocumentRecord findByDocumentId(String documentId) {
        return store.values()
                .stream()
                .filter(r -> r.getDocumentId().equals(documentId))
                .findFirst()
                .orElse(null);
    }
}
