// src/main/java/com/docweaver/domain/Workspace.java
package com.docweaver.domain;

public class Workspace {

    private final String id;
    private final String name;

    public Workspace(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
