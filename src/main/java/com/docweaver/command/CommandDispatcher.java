// src/main/java/com/docweaver/command/CommandDispatcher.java
package com.docweaver.command;

import java.util.ArrayDeque;
import java.util.Queue;

public class CommandDispatcher {

    private final Queue<Command> queue = new ArrayDeque<>();

    public void submit(Command command) {
        queue.offer(command);
    }

    public void runAll() {
        Command cmd;
        while ((cmd = queue.poll()) != null) {
            cmd.execute();
        }
    }
}
