package org.github.goldsam.diffsync.core.edit;

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
   * Returns a collection of edits on this stack ordered newest to oldest.
   * The source version of the newest edit (i.e. the first element) is returned 
   * returned by {@link #getNewestPatchSourceVersion()}.
   * Versions of edits are contiguous; that is, gaps in version numbers are not allowed..
   * @return collection of edits on this stack ordered newest to oldest.
   */
  Collection<P> getPatches();
  
  /**
   * Returns the version number of the edit on the top of the stack.
   * @return version number of the edit on the top of the stack.
   */
  long getNewestPatchSourceVersion();
  
  /**
   * Returns the number of edits on this stack.
   * @return number of edits on this stack.
   */
  int getPatchCount();
  
  /**
   * Removes all edits from this stack.
   */
  void clear();
}
