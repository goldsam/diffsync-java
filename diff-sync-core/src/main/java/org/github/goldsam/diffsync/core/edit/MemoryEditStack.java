package org.github.goldsam.diffsync.core.edit;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import org.github.goldsam.diffsync.core.LocalContext;
import org.github.goldsam.diffsync.core.edit.EditStack;

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
  public long getNewestPatchSourceVersion() {
    return currentVersion;
  }

  @Override
  public int getPatchCount() {
    return patches.size();
  }
  
  @Override
  public void clear() {
    patches.clear();
    currentVersion = -1;
  }
  
  public static class Factory<P1> implements EditStackFactory<P1> {
    private static final Factory instance = new Factory();
    
    @Override
    public EditStack<P1> createEditStack(LocalContext<?, P1> localContext) {
      return new MemoryEditStack<>();
    } 
    
    public static <T> Factory<T> getInstance() {
      return instance;
    }
  }
 }
