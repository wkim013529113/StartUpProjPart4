// src/main/java/com/docweaver/service/DocumentRepository.java
package com.docweaver.service;

import com.docweaver.domain.Document;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentRepository {

    private final Map<String, Document> store = new ConcurrentHashMap<>();

    public void save(Document doc) {
        store.put(doc.getId(), doc);
    }

    public Document findById(String id) {
        return store.get(id);
    }

    public Collection<Document> findAll() {
        return store.values();
    }
}
