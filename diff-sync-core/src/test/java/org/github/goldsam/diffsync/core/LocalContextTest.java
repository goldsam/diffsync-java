package org.github.goldsam.diffsync.core;

import org.github.goldsam.diffsync.core.edit.MemoryEditStack;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocalContextTest {
  
  private MemoryEditStack<Integer> es = new MemoryEditStack<>();
  private MockLocalContextListener<Integer, Integer> lcl = new MockLocalContextListener<>();
  private LocalContext<Integer, Integer> lc = new LocalContext<>(es, lcl);
  
  private SharedContextListener<Integer, Integer> scl = new MockSharedContextListener<>();
  private SharedContext<Integer, Integer> sc = new SharedContext<>(
    IntDifferencer.getInstance(), 
    MemoryEditStack.Factory.getInstance(),
    scl,
    true);
  
  public LocalContextTest() {
    lc.setSharedContext(sc);
  }

  @Test
  public void testSetSharedContext() {
    lc.initialize(0);
    lc.update(7);
    lc.update(3);
    lc.update(13);
  }
}
