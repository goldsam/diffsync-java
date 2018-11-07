package org.github.goldsam.diffsync.text;

import org.github.goldsam.diffsync.core.PatchFailedException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TextDifferencerTest {
  
  TextDifferencer differencer = new TextDifferencer();
  
  String INITIAL_TEXT = "This is a test.";
  String FINAL_TEXT = "This was a bad test!";
  
  @Test
  public void testRoundTripDiffPatchIsSuccessful() throws PatchFailedException { 
    String diff = differencer.difference(INITIAL_TEXT, FINAL_TEXT);
    String patched = differencer.patch(INITIAL_TEXT, diff, false);
    assertEquals(FINAL_TEXT, patched);
  }
  
  @Test(expected = PatchFailedException.class)
  public void testThrowsPathFailedExceptionOnConflictWith() throws PatchFailedException { 
    String diff = differencer.difference(INITIAL_TEXT, FINAL_TEXT);
    differencer.patch("garbage in", diff, false);
  }
}
