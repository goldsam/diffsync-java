package org.github.goldsam.diffsync.edit;

/**
 * Represents a modification to a document at a specific point in time.
 *
 * @param <P> Patch type.
 */
public interface Edit<P> {
  
  /**
   * Returns the patch used to apply this edit to a document.
   * @return Document patch.
   */
  P getPatch();
  
  /**
   * Returns the version of document this patch was created from.
   * @return Source document version number.
   */
  long getVersion();
}
