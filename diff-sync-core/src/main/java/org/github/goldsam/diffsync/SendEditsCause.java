package org.github.goldsam.diffsync;

public enum SendEditsCause {
 
  /**
   * Previously received remote edits are being acknowledged.
   */
  EDITS_ACKED(0),
  
  /**
   * The document was updated locally.
   */
  DOCUMENT_UPDATED(1);
  
  private final int code;
    
  private SendEditsCause(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
