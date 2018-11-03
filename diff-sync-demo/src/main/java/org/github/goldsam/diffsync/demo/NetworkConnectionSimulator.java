package org.github.goldsam.diffsync.demo;

import org.github.goldsam.diffsync.core.Connectable;
import org.github.goldsam.diffsync.core.Connection;
import org.github.goldsam.diffsync.core.ConnectionListener;
import org.github.goldsam.diffsync.core.PatchFailedException;
import org.github.goldsam.diffsync.core.SendDocumentCause;
import org.github.goldsam.diffsync.core.SendEditsCause;
import org.github.goldsam.diffsync.core.context.LocalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkConnectionSimulator<D, P> implements ConnectionListener<D, P> {
  private static final Logger logger = LoggerFactory.getLogger(NetworkConnectionSimulator.class);
    
  public final Connectable<D, P> endpoint1;
  public final Connectable<D, P> endpoint2;
  
  private Connection<D, P> connection1;
  private Connection<D, P> connection2;
  
  public NetworkConnectionSimulator(Connectable<D, P> endpoint1, Connectable<D, P> endpoint2) {
    this.endpoint1 = endpoint1;
    this.endpoint2 = endpoint2;
  }
  
  public void connect() throws Exception{
    try {
      connection1 = endpoint1.connect(this);
      connection2 = endpoint2.connect(this);
    } catch(Exception e) {
      disconnect();
      throw e; 
    }
  }
  
  public void disconnect() throws Exception {
    if (connection1 != null) {
      connection1.close();
      connection1 = null;
    }
    if (connection2 != null) {
      connection2.close();
      connection2 = null;
    }
  }

  @Override
  public void onSendEdits(LocalContext<D, P> localContext, SendEditsCause cause) {
    try {
      getDestConnection(localContext)
        .receiveEdits(
          localContext.getEdits(), 
          localContext.getLocalVersion());
    } catch (PatchFailedException e) {
      logger.error("Sending edits failed.", e);
    }
  }

  @Override
  public void onSendDocument(LocalContext<D, P> localContext, SendDocumentCause cause) {
    getDestConnection(localContext)
      .receiveDocument(
        localContext.getSharedContext().getDocument(), 
        localContext.getLocalVersion());
  }
  
  @Override
  public void onSendResetRequest(LocalContext<D, P> localContext) {
    getDestConnection(localContext)
      .receiveResetRequest();
  }
  
  private Connection<D, P> getDestConnection(LocalContext<D, P> sourceContext) {
    return (connection1.getLocalContext() == sourceContext) ? connection2 : connection1;    
  }
}
