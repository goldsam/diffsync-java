package org.github.goldsam.diffsync.edit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MemoryEditStack<P> implements EditStack<P>{
  
  private final Deque<Edit<P>> edits = new ArrayDeque<>();

  @Override
  public void pushEdit(P patch, long version) {
    if (!edits.isEmpty() && (edits.peekLast().getVersion() + 1) != version) {
      throw new IllegalArgumentException(String.format(
        "Version %d did not match expected version %d.",
        edits.peekLast().getVersion() + 1,
        version));
    }
    edits.addLast(new ImmutableEdit<>(patch, version));
  }
 
  @Override
  public void popEdits(long version) {
    while (!edits.isEmpty() && version <= edits.peekFirst().getVersion()) {
      edits.removeFirst();
    }
  }

  @Override
  public List<Edit<P>> getEdits() {
    return new ArrayList<>(edits);
  }

  @Override
  public boolean isEmpty() {
    return edits.isEmpty();
  }
  
  @Override
  public void clear() {
    edits.clear();
  }
  
  public static class Factory<T> implements EditStackFactory<T> {
    private static final Factory instance = new Factory();
    
    @Override
    public EditStack<T> createEditStack() {
      return new MemoryEditStack<>();
    } 
    
    public static <T> Factory<T> getInstance() {
      return instance;
    }
  }
 }
