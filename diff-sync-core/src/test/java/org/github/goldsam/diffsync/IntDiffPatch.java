package org.github.goldsam.diffsync;

import org.github.goldsam.diffsync.diffpatch.DiffPatch;
import org.github.goldsam.diffsync.diffpatch.PatchFailedException;
import org.github.goldsam.diffsync.diffpatch.PatchOptions;

public class IntDiffPatch implements DiffPatch<Integer, Integer>{

  private IntDiffPatch() {}
  
  private static final IntDiffPatch instance = new IntDiffPatch();
  
  public static IntDiffPatch getInstance() {
    return instance;
  }

  @Override
  public Integer diff(Integer source, Integer target) {
    return target - source;
  }

  @Override
  public Integer patch(Integer source, Integer patch, PatchOptions options) throws PatchFailedException {
    return source + patch;
  }
}
