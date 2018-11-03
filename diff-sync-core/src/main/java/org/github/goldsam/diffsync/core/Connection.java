package org.github.goldsam.diffsync.core;

import java.util.List;
import java.util.function.Consumer;
import org.github.goldsam.diffsync.core.context.LocalContext;
import org.github.goldsam.diffsync.core.edit.Edit;

public class Connection<D, P> implements AutoCloseable {
  
  private final LocalContext<D, P> localContext;
  private Consumer<Connection<D, P>> closeCallback;
  
  public Connection(LocalContext<D, P> localContext, Consumer<Connection<D, P>> closeCallback) {
    this.localContext = localContext;
    this.closeCallback = closeCallback;
  }
   
  @Override
  public void close() throws Exception {
    if (closeCallback != null) {
      closeCallback.accept(this);
      closeCallback = null;
    }
  }
  
  public void receiveEdits(List<Edit<P>> edits, long remoteVersion) throws PatchFailedException {
    localContext.processEdits(edits, remoteVersion);
  }
  
  public void receiveDocument(D document, long version) {
    localContext.reset(document, version);
  }
  
  public void receiveResetRequest() {
    localContext.reset();
  }

  public LocalContext<D, P> getLocalContext() {
    return localContext;
  }
  
  public boolean isClosed() {
    return closeCallback == null;
  }
}
