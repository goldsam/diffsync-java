package org.github.goldsam.diffsync.core;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

public class MemoryEditStack<P> implements EditStack<P>{
  
  private final Deque<P> patches = new ArrayDeque<>();
  private long currentVersion = -1;

  @Override
  public void pushEdit(P patch, long version) {
    if (version == (currentVersion + 1) || currentVersion < 0) {
      patches.push(patch);
      currentVersion = version;
    }
  }

  @Override
  public void purgeEdits(long version) {
    if (version < 0) {
      throw new IllegalArgumentException("Edit version number cannot be negative.");
    }
    
    if (currentVersion >= 0 && version <= currentVersion && !patches.isEmpty()) {
      for (long i = version - (currentVersion - patches.size()); i > 0; i--) {
        patches.removeLast();
      }
    }
  }

  @Override
  public Collection<P> getPatches() {
    return Collections.unmodifiableCollection(patches);
  }

  @Override
  public long getCurrentVersion() {
    return currentVersion;
  }

  @Override
  public int getSize() {
    return patches.size();
  }
  
  @Override
  public void clear() {
    patches.clear();
    currentVersion = -1;
  }
}
