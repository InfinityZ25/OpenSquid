package me.aleiv.core.paper.utilities.TCT;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quick util that allows developer to chain tasks together without having to
 * think much about it
 */
public class TaskChainTool {
    /**
     * Use concurrent linked queue to preserve order and ensure many threads can
     * access the same resources at once.
     */
    protected ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    protected int totalTasks;
    protected static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * A static method that creates an empty task chain object.
     */
    public static TaskChainTool create() {
        return new TaskChainTool();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Runnable poll() {
        return queue.poll();
    }

    public int getTasksLeft(){
        return totalTasks-queue.size();
    }

    public int getCurrentTask(){
        return totalTasks-getTasksLeft();
    }

    /**
     * Builder methods that appends runnable to the end of the queue.
     * 
     * @param runnable to append to the queue.
     * @return this instance of TaskChainTool.
     */
    public TaskChainTool add(Runnable runnable) {
        queue.add(runnable);

        return this;
    }

    /**
     * Builder method that appends delay to the end of the queue.
     * 
     * @param delay in milliseconds.
     * @return this instance of TaskChainTool instance.
     */
    public TaskChainTool delay(long delay) {
        final var actualDelay = Math.abs(delay);

        /** Ensure the delay is greater than 0ms */
        if (actualDelay > 0)
            queue.add(DelayTask.of(actualDelay));

        return this;
    }

    /**
     * Repeats the last task x amount of times.
     * 
     * @param times to repeat the last task.
     * @return this instance of TaskChainTool.
     */
    public TaskChainTool repeat(int times) {
        // Retrieve the head of queue without removing
        var head = queue.peek();

        if (head != null) {
            // If head is not null, then we can repeat it
            for (int i = 0; i < times; i++) {
                queue.add(head);
            }
        }

        return this;

    }

    /**
     * Builder method to add a task to the chain.
     * 
     * @param runnable The task to add.
     * @param delay    The delay in milliseconds after the task is executed.
     * @return The builder object.
     */
    public TaskChainTool addWithDelay(Runnable runnable, long delay) {
        queue.add(() -> {
            runnable.run();

            final var actualDelay = Math.abs(delay);

            /** Ensure the delay is greater than 0ms */
            if (actualDelay > 0)
                DelayTask.of(actualDelay).run();

        });
        return this;
    }

    /**
     * Function that executes all tasks in the queue.
     * 
     * @return CompletableFuture that completes when all tasks are completed.
     */
    public CompletableFuture<Boolean> execute() {
        this.totalTasks = queue.size();
        var future = new CompletableFuture<Boolean>();

        EXECUTOR_SERVICE.submit(() -> {
            while (!queue.isEmpty())
                handleRunnable(poll());

            future.complete(true);

        });
        return future;
    }

    /**
     * Function that executes all tasks in the queue.
     * 
     * @param nextRunnable
     */
    public void handleRunnable(Runnable nextRunnable) {

        try {
            EXECUTOR_SERVICE.submit(nextRunnable).get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * Function to clone the current taskchaintool onto a different object.
     * 
     */
    @Override
    public TaskChainTool clone() {
        var clone = new TaskChainTool();

        for (var runnable : queue) {
            clone.add(runnable);
        }

        return clone;
    }

}