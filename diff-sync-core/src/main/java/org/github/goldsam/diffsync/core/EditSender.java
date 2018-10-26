package org.github.goldsam.diffsync.core;

/**
 * @param <P> Patch type.
 */
public interface EditSender<P> {

  public void sendEdits(EditStack<P> localEdits, long lastReceivedRemoteVersion);
  
  public void sendAck(long lastReceivedRemoteVersion);
}
