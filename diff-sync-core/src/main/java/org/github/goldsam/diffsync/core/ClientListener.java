package org.github.goldsam.diffsync.core;

import java.util.List;
import org.github.goldsam.diffsync.core.edit.Edit;

public interface ClientListener<D, P> {
  public void onDocumentUpdated(D document, long localVersion);
  
  public void onEditsProcessed(D document, List<Edit<P>> edits, long remoteVersion);
}
