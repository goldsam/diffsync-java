package org.github.goldsam.diffsync;

public interface ConnectionListener<D, P> {
  
  void onSendEdits(SynchronizedView<D, P> localContext, SendEditsCause cause);
  
  void onSendDocument(SynchronizedView<D, P> localContext, SendDocumentCause cause);
  
  void onSendResetRequest(SynchronizedView<D, P> localContext);
}
