package org.github.goldsam.diffsync.concurrent

import org.github.goldsam.diffsync.concurrent.*
import spock.lang.*
import spock.mock.MockingApi.*
import static org.junit.Assert.*
import java.util.ArrayDeque
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

class SequentialExecutorSpec extends Specification {
  
  private static class FakeExecutor implements Executor {
    Queue<Runnable> tasks = new ArrayDeque<>();

    @Override
    public void execute(Runnable command) {
      tasks.add(command);
    }

    boolean hasNext() {
      return !tasks.isEmpty();
    }

    void runNext() {
      assertTrue("expected at least one task to run", hasNext());
      tasks.remove().run();
    }

    void runAll() {
      while (hasNext()) {
        runNext();
      }
    }
  }
  
  def FakeExecutor fakePool = new FakeExecutor()
  def SequentialExecutor e = new SequentialExecutor(fakePool)

  def "Basic contract"() {
    given: "a simple task"
      final AtomicInteger totalCalls = new AtomicInteger()
      Runnable intCounter = new Runnable() {
          @Override
          public void run() {
            totalCalls.incrementAndGet();
            // Make sure that no other tasks are scheduled to run while this is running.
            assertFalse(fakePool.hasNext());
          }
        };

    when: "the task is scheduled for execution while no pending tasks are outstanding"
      e.execute(intCounter);
    then: "the task is immediately executed in the caller's execution context"
      1 == totalCalls.get()
      !fakePool.hasNext()
    when: "another task is scheduled before the underlying executor makes progress"
//      e.execute(intCounter);
//    then: "neither task will have been executed"
//      0 == totalCalls.get()
//    when: "the underlying executor processes to completion"
//      fakePool.runAll();
//    then: "both tasks will have executed"
//      2 == totalCalls.get()
      
//    // Queue is empty so no runner should be scheduled.
//    assertFalse(fakePool.hasNext());
//
//    // Check that execute can be safely repeated
//    e.execute(intCounter);
//    e.execute(intCounter);
//    e.execute(intCounter);
//    // No change yet.
//    assertEquals(2, totalCalls.get());
//    fakePool.runAll();
//    assertEquals(5, totalCalls.get());
//    assertFalse(fakePool.hasNext());
  }

}

