package org.github.goldsam.diffsync.core;

/**
 *
 */
public class PatchFailedException extends Exception {

  public PatchFailedException() {
  }

  public PatchFailedException(String message) {
    super(message);
  }

  public PatchFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
