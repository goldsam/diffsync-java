package org.github.goldsam.diffsync.core;

import org.github.goldsam.diffsync.core.context.LocalContext;

public interface ConnectionListener<D, P> {
  void onSendEdits(LocalContext<D, P> localContext, SendEditsCause cause);
  
  void onSendDocument(LocalContext<D, P> localContext, SendDocumentCause cause);
  
  void onSendResetRequest(LocalContext<D, P> localContext);
}
