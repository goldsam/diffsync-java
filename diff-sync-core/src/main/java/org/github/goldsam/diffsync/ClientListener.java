package org.github.goldsam.diffsync;

import org.github.goldsam.diffsync.edit.Edit;

public interface ClientListener<D, P> {

  public void documentUpdated(D document, long localVersion);
  
  public void editProcessed(D document, Edit<P> edit, long remoteVersion);
}
