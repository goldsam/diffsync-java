package org.github.goldsam.diffsync.core;

import org.github.goldsam.diffsync.core.edit.EditStack;

/**
 * @param <P> Patch type.
 */
public interface EditSender<P> {

  public void sendEdits(EditStack<P> localEdits, long lastReceivedRemoteVersion);
  
  public void sendAck(long lastReceivedRemoteVersion);
}
