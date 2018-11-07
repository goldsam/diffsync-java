package org.github.goldsam.diffsync.text;

import java.util.LinkedList;
import name.fraser.neil.plaintext.diff_match_patch;
import org.github.goldsam.diffsync.core.Differencer;
import org.github.goldsam.diffsync.core.PatchFailedException;

public class TextDifferencer implements Differencer<String, String> { 
  public final diff_match_patch diffMatchPatch;
  
  public TextDifferencer(diff_match_patch diffMatchPatch) {
    this.diffMatchPatch = diffMatchPatch;
  }
  
  public TextDifferencer() {
    this(new diff_match_patch());
  }
  
  @Override
  public String difference(String sourceDocument, String targetDocument) {
    LinkedList<diff_match_patch.Patch> patches = diffMatchPatch.patch_make(sourceDocument, targetDocument);
    return diffMatchPatch.patch_toText(patches);
  }

  @Override
  public String patch(String sourceDocument, String patch, boolean fuzzy) throws PatchFailedException {
    LinkedList<diff_match_patch.Patch> patches = diffMatchPatch.patch_fromText(patch);
   
    Object[] appliedPatch = diffMatchPatch.patch_apply(patches, sourceDocument);
    if (!fuzzy && anyFalse((boolean[])appliedPatch[1])) {
      throw new PatchFailedException("Unable to apply strict text patch.");
    }
    
    return appliedPatch[0].toString();
  }
  
  private static boolean anyFalse(boolean[] values) {
    for (int i = 0, l = values.length; i < l; i++) {
      if(!values[i]) {
        return true;
      }
    }
    
    return false;
  }
}














