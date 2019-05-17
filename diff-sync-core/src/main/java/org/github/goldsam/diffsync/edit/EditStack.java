package org.github.goldsam.diffsync.edit;

import java.util.List;

public interface EditStack<P> {
  
  /**
   * Pushes an edit onto the stack of edits.
   * @param patch Patch describing how to edit a document.
   * @param version Edit version number.
   */
  void pushEdit(P patch, long version);
  
  /**
   * Removes all edits with a version less-than-or-equal to a given maximum 
   * (inclusive) version number.
   * @param version maximum version (inclusive) of edits to purge.
   */
  void popEdits(long version);

  /**
   * Returns a list of all edits on this stack ordered oldest 
   * (lowest version) to newest (highest version).
   * @return list of all edits on this stack ordered oldest to newest.
   */
  List<Edit<P>> getEdits();
  
  /**
   * Returns {@literal true} if this edit stack is empty.
   * @return {@literal true} if this edit stack is empty.
   */
  boolean isEmpty();
  
  /**
   * Removes all edits from this stack.
   */
  void clear();
}
