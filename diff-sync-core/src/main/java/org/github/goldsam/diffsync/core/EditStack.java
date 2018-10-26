package org.github.goldsam.diffsync.core;

import java.util.Collection;

public interface EditStack<P> {
  
  /**
   * Pushes an edit onto the stack of edits.
   * @param patch Patch describing how to edit a document.
   * @param version Edit version number.
   */
  void pushEdit(P patch, long version);
  
  /**
   * Purges all edit with a version less-than-or-equal to a given maximum (inclusive) version.
   * @param version maximum version (inclusive) of edits to purge.
   */
  void purgeEdits(long version);

  /**
   * Returns an ordered collection of edits on this stack.
   * @return ordered collection of edits on this stack.
   */
  Collection<P> getPatches();
  
  /**
   * Returns the version number of the edit on the top of the stack.
   * @return version number of the edit on the top of the stack.
   */
  long getCurrentVersion();
  
  /**
   * Returns the number of edits on this stack.
   * @return number of edits on this stack.
   */
  int getSize();
  
  /**
   * Removes all edits from this stack.
   */
  void clear();
}
