package org.github.goldsam.diffsync.diffpatch;

/**
 * Exception throw to indicate failure in applying a patch to document.
 * 
 * @see DiffPatch#patch(java.lang.Object, java.lang.Object, org.github.goldsam.diffsync.diffpatch.PatchOptions)
 */
public class PatchFailedException extends RuntimeException {
  public PatchFailedException() { }

  public PatchFailedException(String message) {
    super(message);
  }

  public PatchFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
