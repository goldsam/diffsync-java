package org.github.goldsam.diffsync.core.context;

import java.util.Arrays;
import java.util.List;
import org.github.goldsam.diffsync.core.ConnectionListener;
import org.github.goldsam.diffsync.core.IntDifferencer;
import org.github.goldsam.diffsync.core.MockConnectionListener;
import org.github.goldsam.diffsync.core.MockContextListener;
import org.github.goldsam.diffsync.core.edit.Edit;
import org.github.goldsam.diffsync.core.edit.ImmutableEdit;
import org.github.goldsam.diffsync.core.edit.MemoryEditStack;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
 
//  
//  @Test
//  public void updateShouldSetTheCurrentDocuent() {
//    initialize(true);;
//    
//    localContext.update(6);
//    assertEquals(Long.valueOf(6), localContext.getDocument());
//
//    localContext.update(-5);
//    assertEquals(Long.valueOf(-5), localContext.getDocument());
//  }

//  @Test
//  public void testGetSharedContext() {
//    System.out.println("getSharedContext");
//    LocalContext instance = null;
//    SharedContext expResult = null;
//    SharedContext result = instance.getSharedContext();
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testGetConnectionListener() {
//    System.out.println("getConnectionListener");
//    LocalContext instance = null;
//    ConnectionListener expResult = null;
//    ConnectionListener result = instance.getConnectionListener();
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testGetRemoteVersion() {
//    System.out.println("getRemoteVersion");
//    LocalContext instance = null;
//    long expResult = 0L;
//    long result = instance.getRemoteVersion();
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testGetLocalVersion() {
//    System.out.println("getLocalVersion");
//    LocalContext instance = null;
//    long expResult = 0L;
//    long result = instance.getLocalVersion();
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReset_GenericType_long() {
//    System.out.println("reset");
//    Object document = null;
//    long version = 0L;
//    LocalContext instance = null;
//    instance.reset(document, version);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReset_long() {
//    System.out.println("reset");
//    long version = 0L;
//    LocalContext instance = null;
//    instance.reset(version);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testReset_0args() {
//    System.out.println("reset");
//    LocalContext instance = null;
//    instance.reset();
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testUpdate_0args() {
//    System.out.println("update");
//    LocalContext instance = null;
//    instance.update();
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testUpdate_GenericType() {
//    System.out.println("update");
//    Object newCurrentDocument = null;
//    LocalContext instance = null;
//    instance.update(newCurrentDocument);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testUpdate_Function() {
//    System.out.println("update");
//    LocalContext instance = null;
//    instance.update(null);
//    fail("The test case is a prototype.");
//  }
//
//  @Test
//  public void testProcessEdits() throws Exception {
//    System.out.println("processEdits");
//    LocalContext instance = null;
//    instance.processEdits(null);
//    fail("The test case is a prototype.");
//  }
  
}
