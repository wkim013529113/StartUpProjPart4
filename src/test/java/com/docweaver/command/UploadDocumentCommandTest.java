// src/test/java/com/docweaver/command/UploadDocumentCommandTest.java
package com.docweaver.command;

import com.docweaver.domain.Document;
import com.docweaver.domain.DocumentStatus;
import com.docweaver.domain.ProcessingJob;
import com.docweaver.domain.Workspace;
import com.docweaver.service.DocumentRepository;
import com.docweaver.service.ProcessingJobQueue;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UploadDocumentCommandTest {

    @Test
    void execute_createsDocumentAndProcessingJob() {
        DocumentRepository documentRepository = new DocumentRepository();
        ProcessingJobQueue jobQueue = new ProcessingJobQueue();
        Workspace workspace = new Workspace("ws-1", "Test WS");

        UploadDocumentCommand cmd = new UploadDocumentCommand(
                workspace,
                Collections.singletonList("invoice1.pdf"),
                documentRepository,
                jobQueue
        );

        cmd.execute();

        assertEquals(1, documentRepository.findAll().size(), "One document should be stored");
        Document doc = documentRepository.findAll().iterator().next();
        assertEquals(DocumentStatus.PROCESSING, doc.getStatus());

        assertEquals(1, jobQueue.size(), "One job should be enqueued");
        ProcessingJob job = jobQueue.dequeue();
        assertEquals(doc.getId(), job.getDocumentId());
        assertEquals(ProcessingJob.JobType.CLASSIFY_AND_EXTRACT, job.getJobType());
    }

    @Test
    void execute_throwsOnUnsupportedFileType() {
        DocumentRepository documentRepository = new DocumentRepository();
        ProcessingJobQueue jobQueue = new ProcessingJobQueue();
        Workspace workspace = new Workspace("ws-1", "Test WS");

        UploadDocumentCommand cmd = new UploadDocumentCommand(
                workspace,
                Collections.singletonList("data.txt"),
                documentRepository,
                jobQueue
        );

        assertThrows(IllegalArgumentException.class, cmd::execute);
    }
}
