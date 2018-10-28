package org.github.goldsam.diffsync.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockSharedContextListener<D, P> implements SharedContextListener<D, P> {
  
  public static class Edit<T> {
    private final boolean editApplied; 
    private final T patch;
    private final long patchSourceVersion;
    private final long lastReceivedVersion; 

    public Edit(boolean editApplied, T patch, long patchSourceVersion, long lastReceivedVersion) {
      this.editApplied = editApplied;
      this.patch = patch;
      this.patchSourceVersion = patchSourceVersion;
      this.lastReceivedVersion = lastReceivedVersion;
    }

    public boolean isEditApplied() {
      return editApplied;
    }

    public T getPatch() {
      return patch;
    }

    public long getPatchSourceVersion() {
      return patchSourceVersion;
    }

    public long getLastReceivedVersion() {
      return lastReceivedVersion;
    }
  }
  
  private final List<Edit<P>> edits = new ArrayList<>();

  @Override
  public void onEditProcessed(LocalContext<D, P> processedContext, boolean editApplied, P patch, long patchSourceVersion, long lastReceivedVersion) {
    edits.add(new Edit<>(editApplied, patch, patchSourceVersion, lastReceivedVersion));
  }
  
  public List<Edit<P>> getEdits() {
    return Collections.unmodifiableList(edits);
  }
}
