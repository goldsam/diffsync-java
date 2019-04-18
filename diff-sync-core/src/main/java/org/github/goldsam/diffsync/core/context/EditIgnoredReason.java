package org.github.goldsam.diffsync.core.context;

/**
 * Indicates a reason for ignoring an edit.
 */
public enum EditIgnoredReason {
  /**
   * The edit was previously processed and has already been applied.
   * This flag likely indicates loss of packets transmitted by the remote.
   */
  ALREADY_APPLIED,
  
  /**
   * An edit was received out of order (i.e. "from the future").
   */
  OUT_OF_ORDER,
  
  /**
   * The acknowledged local version number does not match the current local shadow version.
   */
  ACK_VERSION_MISMATCH
}
