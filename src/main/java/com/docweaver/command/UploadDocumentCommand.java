// src/main/java/com/docweaver/command/UploadDocumentCommand.java
package com.docweaver.command;

import com.docweaver.domain.Document;
import com.docweaver.domain.DocumentStatus;
import com.docweaver.domain.ProcessingJob;
import com.docweaver.domain.Workspace;
import com.docweaver.service.DocumentRepository;
import com.docweaver.service.ProcessingJobQueue;

import java.util.List;

public class UploadDocumentCommand implements Command {

    private final Workspace workspace;
    private final List<String> filenames;

    private final DocumentRepository documentRepository;
    private final ProcessingJobQueue jobQueue;

    public UploadDocumentCommand(
            Workspace workspace,
            List<String> filenames,
            DocumentRepository documentRepository,
            ProcessingJobQueue jobQueue) {

        this.workspace = workspace;
        this.filenames = filenames;
        this.documentRepository = documentRepository;
        this.jobQueue = jobQueue;
    }

    @Override
    public void execute() {
        if (filenames == null || filenames.isEmpty()) {
            throw new IllegalArgumentException("No files to upload");
        }

        for (String filename : filenames) {
            validate(filename);

            // store document
            Document document = new Document(workspace.getId(), filename);
            document.markProcessing(); // immediately mark as PROCESSING
            documentRepository.save(document);

            // create processing job
            ProcessingJob job = new ProcessingJob(
                    document.getId(),
                    ProcessingJob.JobType.CLASSIFY_AND_EXTRACT
            );
            jobQueue.enqueue(job);

            // In real system: write AuditEvent here
        }
    }

    private void validate(String filename) {
        if (!filename.toLowerCase().endsWith(".pdf")
                && !filename.toLowerCase().endsWith(".png")
                && !filename.toLowerCase().endsWith(".jpg")
                && !filename.toLowerCase().endsWith(".jpeg")) {
            throw new IllegalArgumentException("Unsupported file type: " + filename);
        }
    }
}
