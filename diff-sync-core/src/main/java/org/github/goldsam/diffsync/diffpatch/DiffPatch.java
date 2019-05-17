package org.github.goldsam.diffsync.diffpatch;

/**
 * Document diff and patch operations.
 * 
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface DiffPatch<D, P> {
  /**
   * Computes a patch representing the diff between a {@code source} and 
   * {@code target} document. The resulting patch can be applied to the 
   * {@code source} document to reconstruct the {@code target} document.
   * @param source Source document.
   * @param target Target document.
   * @return Patch representing diff between {@code source} and {@code target} document.
   */
  P diff(D source, D target);
  
  /**
   * Applies a patch to a {@code source} document to compute a new document.
   * This operation must not modify the original source document.
   * 
   * @param source Document to patch.
   * @param patch Patch encapsulating document changes.
   * @param options Options specifying how the patch operation is applied.
   * @return New document representing patched {@code source}
   */
  D patch(D source, P patch, PatchOptions options) throws PatchFailedException;
}
