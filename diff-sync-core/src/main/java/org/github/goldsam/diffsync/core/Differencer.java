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
   * @param targetDocument Target document.
   * @return Patch representing difference between reference and current document.
   */
  P Difference(D sourceDocument, D targetDocument);

  /**
   * Applies a patch to a document to compute a new document.
   * The original document is not modified.
   * 
   * @param soureDocument Document to patch.
   * @param patch Patch encapsulating document changes.
   * @return Target document.
   */
  D Patch(D soureDocument, P patch);
}
