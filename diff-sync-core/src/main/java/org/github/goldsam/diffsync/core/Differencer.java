package org.github.goldsam.diffsync.core;

/**
 * Differencing engine which performs two-way differences merges.
 *
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface Differencer<D, P> {
  
  /**
   * Computes a patch representing the difference between a 
   * current document and a reference document.
   * @param sourceDocument Source document.
   * @param destDocument Target document.
   * @return Patch representing difference between reference and current document.
   */
  P difference(D sourceDocument, D destDocument);

  /**
   * Applies a patch to a document to compute a new document.
   * The original document is not modified.
   * 
   * @param sourceDocument Document to patch.
   * @param patch Patch encapsulating document changes.
   * @param fuzzy Allows fuzzy patching if {@literal true}; 
   *              enforces strict patching if {@literal false}.
   * @return Target document.
   */
  D patch(D sourceDocument, P patch, boolean fuzzy) throws PatchFailedException;
}
