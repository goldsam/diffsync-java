package org.github.goldsam.diffsync.concurrent;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class SequentialExecutor implements Executor {
  private final Executor executor;
  private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
  private final AtomicInteger taskCount = new AtomicInteger(0);
  private final long taskExecutionTimeoutNanos = Duration.ofSeconds(10).toNanos();
  private final Runnable processQueueTask = new ProcessQueueTask();
  private final int maximumTasksPerContinuation = 10;
 
  public SequentialExecutor(Executor executor) {
    this.executor = Objects.requireNonNull(executor, "executor is null"); 
  }
  
  @Override
  public void execute(Runnable command) {
    tasks.add(command);
    if (taskCount.getAndIncrement() == 0) {
      executeTasks(1);
    }
  }
  
  private class ProcessQueueTask implements Runnable {
    @Override
    public void run() {
      executeTasks(maximumTasksPerContinuation);
    }
  }
  
  private void executeTasks(int maximumTasks) {
    boolean hasMoreTasks = true;
    long startNanos = System.nanoTime();
    int tasksExecuted = 0;
    do {
      Runnable task = tasks.poll();
      if(task != null) {
        try {
          task.run();
          tasksExecuted++;
        } 
        finally {
          hasMoreTasks = taskCount.decrementAndGet() > 0;
        }
      }
    } while (hasMoreTasks && !timeoutExpired(startNanos) && tasksExecuted < maximumTasks);

    if (hasMoreTasks) {
      executor.execute(processQueueTask);
    }
  }
  
  private boolean timeoutExpired(long startNanos) {
    return (System.nanoTime() - startNanos) > taskExecutionTimeoutNanos;
  }
}
