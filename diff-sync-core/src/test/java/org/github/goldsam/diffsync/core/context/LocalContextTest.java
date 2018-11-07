package org.github.goldsam.diffsync.core.context;

import java.util.Arrays;
import org.github.goldsam.diffsync.core.IntDifferencer;
import org.github.goldsam.diffsync.core.MockConnectionListener;
import org.github.goldsam.diffsync.core.MockContextListener;
import org.github.goldsam.diffsync.core.PatchFailedException;
import org.github.goldsam.diffsync.core.edit.ImmutableEdit;
import org.github.goldsam.diffsync.core.edit.MemoryEditStack;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocalContextTest {
  
  private MockContextListener<Integer, Integer> contextListener;
  private SharedContext<Integer, Integer> sharedContext;
  private MemoryEditStack<Integer> editStack;
  private LocalContext<Integer, Integer> localContext;
  private MockConnectionListener<Integer, Integer> connectionContext;
  
  private void initialize(boolean usingShadowBackups) {
    contextListener = new MockContextListener<>();
    sharedContext = new SharedContext<>(
      IntDifferencer.getInstance(), contextListener, usingShadowBackups, 0);
    connectionContext = new MockConnectionListener<>();
    editStack = new MemoryEditStack<>();
    localContext = new LocalContext<>(sharedContext, editStack, connectionContext);
  }
  
  private void initialize() {
    initialize(true);
  }
  
  private void initializeAndReset(boolean usingShadowBackups) {
    initialize(usingShadowBackups);
    localContext.reset();
  }
  
  private void initializeAndReset() {
    initializeAndReset(true);
  }
  
  @Test(expected = IllegalStateException.class)
  public void updatingBeforeCallingResetThrowsIllegalState() {
    initialize();
    localContext.update(6);
  }
  
  @Test
  public void updateSetsTheCurrentDocuent() {
    initializeAndReset();
  
    localContext.update(6);
    assertEquals(Integer.valueOf(6), localContext.getDocument());

    localContext.update(-5);
    assertEquals(Integer.valueOf(-5), localContext.getDocument());
  }
  
  @Test 
  public void remoteVersionsIsInvalidBeforeInitialReset() {
    initialize();
    
    assertTrue("remote version negative", localContext.getRemoteVersion() < 0); 
  }
  
  @Test 
  public void localVersionsIsInvalidBeforeInitialReset() {
    initialize();
    
    assertTrue("local version negative", localContext.getLocalVersion() < 0); 
  }
 
  @Test 
  public void resetInializesRemoteVersion() {
    initializeAndReset();
    
    assertEquals(localContext.getRemoteVersion(), 0); 
  }
  
  @Test 
  public void resetInializesLocalVersion() {
    initializeAndReset();
    
    assertEquals(localContext.getLocalVersion(), 0); 
  }
  
  @Test
  public void updateAdvancesLocalVersion() {
    initializeAndReset();
  
    localContext.update(6);
    assertEquals(localContext.getLocalVersion(), 1);

    localContext.update(-5);
    assertEquals(localContext.getLocalVersion(), 2);
  }
 
  @Test
  public void updateDoesNotAdvancesRemoteVersion() {
    initializeAndReset();
  
    localContext.update(6);
    assertEquals(localContext.getRemoteVersion(), 0);

    localContext.update(-5);
    assertEquals(localContext.getRemoteVersion(), 0);
  }
  
  @Test
  public void resetClearsEditStack() {
    initialize();

    editStack.pushEdit(0, 5);
    
    localContext.reset();
    assertTrue("editStack is empty", editStack.isEmpty());
  }
  
  @Test
  public void updatePushesToEditStack() {
    initializeAndReset();
  
    localContext.update(6);
    assertEquals(
      Arrays.asList(new ImmutableEdit<>(6, 0)),
      localContext.getEdits());

    localContext.update(-5);
    assertEquals(
      Arrays.asList(new ImmutableEdit<>(6, 0), new ImmutableEdit<>(-11, 1)),
      localContext.getEdits());
  }
  
  @Test
  public void applyingEditsFromTheFutureDoesNothing() throws PatchFailedException {
    initializeAndReset();
    localContext.update(6);
    localContext.update(-5);
   
    localContext.processEdits(Arrays.asList(new ImmutableEdit<>(-3, 5L)), 0L);
    assertEquals(
      Arrays.asList(new ImmutableEdit<>(6, 0), new ImmutableEdit<>(-11, 1)),
      localContext.getEdits());
    assertEquals(localContext.getLocalVersion(), 2);
    assertEquals(localContext.getRemoteVersion(), 0);
  }
  
//  @Test
//  public void applyingEditMatchingRemoteAndLocalVersion_popsCorrespondingEditFromEditStack() throws PatchFailedException { 
//    initializeAndReset();
////    localContext.update(6);
//   
//    localContext.processEdits(Arrays.asList(new ImmutableEdit<>(6, 0)), 0L);
////    assertTrue("edit stack is empty", localContext.getEdits().isEmpty());
//  }
  
  @Test
  public void applyingEditsMatchingRemoteAndLocalVersion_updatesDocumentAndCurrentShadow() throws PatchFailedException { 
    initializeAndReset();
   
    localContext.processEdits(
      Arrays.asList(
        new ImmutableEdit<>(6, 0),
        new ImmutableEdit<>(-11, 1)), 
      0L);
    assertEquals(
      Integer.valueOf(-5),
      localContext.getDocument());    
  }
  
  @Test
  public void applyingEditMatchingRemoteAndLocalVersion_updatesRemoteVersion() throws PatchFailedException { 
    initializeAndReset();
   
    localContext.processEdits(
      Arrays.asList(
        new ImmutableEdit<>(6, 0),
        new ImmutableEdit<>(-11, 1)), 
      0L);
    assertEquals(2, localContext.getRemoteVersion());    
  }
  
  @Test
  public void applyingEditMatchingRemoteAndLocalVersion_firesEditsAppliedEvent() throws PatchFailedException { 
    initializeAndReset();

    localContext.processEdits(
      Arrays.asList(
        new ImmutableEdit<>(6, 0),
        new ImmutableEdit<>(-11, 1)), 
      0L);

    assertEquals(
      Arrays.asList(
        new MockContextListener.ProcessedEdit<>(
          localContext,
          Arrays.asList(
            new ImmutableEdit<>(6, 0),
            new ImmutableEdit<>(-11, 1)), 
          -5,
          2L)),
      contextListener.getProcessedEdits());
  }
  
  @Test
  public void applyingEditsAfterLostReturnAck() throws PatchFailedException { 
    initializeAndReset();
    
    localContext.processEdits(
      Arrays.asList(
        new ImmutableEdit<>(6, 0)), 
      0L);
    
    localContext.update(8);
    
    localContext.processEdits(
      Arrays.asList(
        new ImmutableEdit<>(6, 0),
        new ImmutableEdit<>(-11, 1)), 
      0L);
//
//    assertEquals(
//      Arrays.asList(
//        new MockContextListener.ProcessedEdit<>(
//          localContext,
//          Arrays.asList(
//            new ImmutableEdit<>(6, 0),
//            new ImmutableEdit<>(-11, 1)), 
//          -5,
//          2L)),
//      contextListener.getProcessedEdits());
  }
}
