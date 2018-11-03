package org.github.goldsam.diffsync.core;

public enum SendDocumentCause {
  
  /**
   * A remote connection was established.
   */
  CONNECTION_ESTABLISHED,
  
  /**
   * The shared document was reset.
   */
  DOCUMENT_RESET
}
