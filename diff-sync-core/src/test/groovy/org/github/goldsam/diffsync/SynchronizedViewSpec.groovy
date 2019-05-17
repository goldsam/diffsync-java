package org.github.goldsam.diffsync.core.context

import org.github.goldsam.diffsync.*
import org.github.goldsam.diffsync.SynchronizationContext
import org.github.goldsam.diffsync.SynchronizationHandler
import org.github.goldsam.diffsync.edit.*
import spock.lang.*
import spock.mock.MockingApi.*;
import static org.junit.Assert.*;

class SynchronizedViewSpec extends Specification {
  
  private SynchronizedView<Integer, Integer> createSynchronizedView(SynchronizationHandler<Integer, Integer> handler, boolean usingShadowBackups) {
    return new SynchronizedView<>(
      new SynchronizationContext<>(IntDiffPatch.getInstance(), handler, usingShadowBackups),    
      new MemoryEditStack<>(), 
      null);
  }
  
  def "Receiving new inbound edits after a lost outbound ACK results in graceful rollback"() {
    
    SynchronizationHandler<Integer, Integer> handler = Mock()   
    SynchronizedView<Integer, Integer> view = createSynchronizedView(handler, true);
    
    given: "a remote edit is applied followed by an local update, but the remote never receives an ACK."
    view.reset(0, 0);
    view.processEdits([new ImmutableEdit<>(6, 0)], 0L);
    view.update(8);
    
    when: "the remote sends the orignal edit plus a newer one"
    view.processEdits([new ImmutableEdit<>(6, 0), new ImmutableEdit<>(-11, 1)], 0L);

    then: "a shadow rollback occurs, the first edit is ignored, and the seond edit is applied."
    1 * handler.shadowRollback(view, 1, 0);
    1 * handler.editProcessed(view, new ImmutableEdit<>(6, 0), 0, EditProcessingResult.DISCARDED_ALREADY_APPLIED, null);
    1 * handler.editProcessed(view, new ImmutableEdit<>(-11, 1), 0, EditProcessingResult.APPLIED_NO_COLLISION, -3);
    1 * handler.allEditsApplied(view, 0, -3);
  }
}
