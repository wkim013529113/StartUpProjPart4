// src/main/java/com/docweaver/demo/UploadDemoMain.java
package com.docweaver.demo;

import com.docweaver.command.CommandDispatcher;
import com.docweaver.command.UploadDocumentCommand;
import com.docweaver.domain.Document;
import com.docweaver.domain.Workspace;
import com.docweaver.service.DocumentRepository;
import com.docweaver.service.ProcessingJobQueue;

import java.util.Arrays;

public class UploadDemoMain {

    public static void main(String[] args) {
        // Setup
        DocumentRepository documentRepository = new DocumentRepository();
        ProcessingJobQueue jobQueue = new ProcessingJobQueue();
        CommandDispatcher dispatcher = new CommandDispatcher();

        Workspace workspace = new Workspace("ws-1", "Demo Workspace");

        // Simulate user selecting two PDFs
        UploadDocumentCommand uploadCmd = new UploadDocumentCommand(
                workspace,
                Arrays.asList("invoice1.pdf", "receipt1.pdf"),
                documentRepository,
                jobQueue
        );

        dispatcher.submit(uploadCmd);
        dispatcher.runAll();

        // Show results
        System.out.println("Documents in repository:");
        for (Document d : documentRepository.findAll()) {
            System.out.printf("  id=%s, file=%s, status=%s%n",
                    d.getId(), d.getFilename(), d.getStatus());
        }

        System.out.println("Jobs in queue after upload: " + jobQueue.size());
        // Dev B's extraction module will consume this queue.
    }
}
