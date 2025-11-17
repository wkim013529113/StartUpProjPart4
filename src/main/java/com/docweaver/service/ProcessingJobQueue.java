// src/main/java/com/docweaver/service/ProcessingJobQueue.java
package com.docweaver.service;

import com.docweaver.domain.ProcessingJob;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessingJobQueue {

    private final Queue<ProcessingJob> queue = new LinkedList<>();

    public void enqueue(ProcessingJob job) {
        queue.offer(job);
    }

    public ProcessingJob dequeue() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}
