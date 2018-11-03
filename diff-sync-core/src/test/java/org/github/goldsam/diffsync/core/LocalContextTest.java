package org.github.goldsam.diffsync.core;

import org.github.goldsam.diffsync.core.context.LocalContext;
import org.github.goldsam.diffsync.core.context.SharedContext;
import org.github.goldsam.diffsync.core.edit.MemoryEditStack;
import org.junit.Test;

public class LocalContextTest {
  
  private final MockContextListener<Integer, Integer> contextListener;
  private final MockConnectionListener<Integer,Integer> connectionListener;
  
  private final SharedContext<Integer, Integer> sharedContext;
  private final MemoryEditStack<Integer> editStack;
  private final LocalContext<Integer, Integer> localContext;
 
  public LocalContextTest() {
    contextListener = new MockContextListener<>();
    sharedContext = new SharedContext<>(IntDifferencer.getInstance(), contextListener, true);
    editStack = new MemoryEditStack<>();
    connectionListener = new MockConnectionListener<>();
    localContext = new LocalContext<>(sharedContext, editStack, connectionListener);
  }
  
  @Test
  public void testSetSharedContext() {
    localContext.reset(0, 0);
    localContext.update(7);
    localContext.update(3);
    localContext.update(13);
  }
}
