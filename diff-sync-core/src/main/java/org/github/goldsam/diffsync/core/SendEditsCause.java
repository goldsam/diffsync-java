package org.github.goldsam.diffsync.core;

public enum SendEditsCause {
  /**
   * Previously received remote edits are being acknowledged.
   */
  EDITS_ACKED,
  
  /**
   * The document was updated locally.
   */
  DOCUMENT_UPDATED  
}
