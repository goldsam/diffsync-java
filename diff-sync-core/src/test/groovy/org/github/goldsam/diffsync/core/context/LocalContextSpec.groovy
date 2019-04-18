package org.github.goldsam.diffsync.core.context

import org.github.goldsam.diffsync.core.*
import org.github.goldsam.diffsync.core.edit.*
import org.github.goldsam.diffsync.core.context.*
import spock.lang.*
import spock.mock.MockingApi.*;
import static org.junit.Assert.*;

class LocalContextSpec extends Specification {
  
  private LocalContext<Integer, Integer> createLocalContext(ContextListener<Integer, Integer> contextListener, boolean usingShadowBackups) {
    return new LocalContext<>(
      new SharedContext<>(IntDifferencer.getInstance(), contextListener, usingShadowBackups, 0), 
      new MemoryEditStack
      <>(), 
      null);
  }
  
  def "Receiving new inbound edits after a lost outbound ACK results in graceful rollback"() {
    ContextListener<Integer, Integer> contextListener = Mock()   
    LocalContext<Integer, Integer> localContext = createLocalContext(contextListener, true);
    
    given: "a remote edit is applied followed by an local update, but the remote never receives an ACK."
    localContext.reset();
    localContext.processEdits([new ImmutableEdit<>(6, 0)], 0L);
    localContext.update(8);
    
    when: "the remote sends the orignal edit plus a newer one"
    localContext.processEdits([new ImmutableEdit<>(6, 0), new ImmutableEdit<>(-11, 1)], 0L);

    then: "a shadow rollback occurs, the first edit is ignored, and the seond edit is applied."
    1 * contextListener.onShadowRollback(localContext, 0);
    1 * contextListener.onEditIgnored(localContext, new ImmutableEdit<>(6, 0), 0, EditIgnoredReason.ALREADY_APPLIED);
    1 * contextListener.onEditApplied(localContext, new ImmutableEdit<>(-11, 1), 0, false);
    localContext.getDocument() == -3
  }
}
