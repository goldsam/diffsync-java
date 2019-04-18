package org.github.goldsam.diffsync.core;

import java.util.List;
import org.github.goldsam.diffsync.core.edit.Edit;

public interface ClientListener<D, P> {
  public void onDocumentUpdated(D document, long localVersion);
  
  public void onEditProcessed(D document, Edit<P> edit, long remoteVersion);
}
