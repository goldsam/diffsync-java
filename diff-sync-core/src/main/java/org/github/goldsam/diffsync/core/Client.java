package org.github.goldsam.diffsync.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.github.goldsam.diffsync.core.context.ContextListener;
import org.github.goldsam.diffsync.core.context.EditIgnoredReason;
import org.github.goldsam.diffsync.core.context.LocalContext;
import org.github.goldsam.diffsync.core.context.SharedContext;
import org.github.goldsam.diffsync.core.edit.Edit;
import org.github.goldsam.diffsync.core.edit.EditStack;
import org.github.goldsam.diffsync.core.edit.EditStackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client<D, P> implements ContextListener<D, P>, Connectable<D, P> {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  
  private final SharedContext<D, P> sharedContext;
  private final EditStackFactory<P> editStackFactory;  
  private final List<ClientListener<D, P>> clientListeners = new ArrayList<>();
  private Connection<D, P> connection;
  
  public Client(Differencer<D, P> differencer, EditStackFactory<P> editStackFactory, boolean usingShadowBackups) {
    sharedContext = new SharedContext<>(differencer, this, usingShadowBackups);
    this.editStackFactory = editStackFactory;
  }
  
  public void update(D document) { 
    getOpenConnection().getLocalContext().update(document);
  }
  
  public void update(Function<D, D> updater) { 
    getOpenConnection().getLocalContext().update(updater);
  }
  
  public void reset() {
    LocalContext<D, P> localContext = getOpenConnection().getLocalContext();
    localContext.getConnectionListener().onSendResetRequest(localContext);
  }
   
  @Override
  public Connection<D, P> connect(ConnectionListener<D, P> connectionListener) {
    if (connection != null) {
      throw new IllegalStateException("Client already connected.");
    }
    
    EditStack<P> editStack = editStackFactory.createEditStack();
    LocalContext<D, P> localContext = new LocalContext<>(sharedContext, editStack, connectionListener);
    connection = new Connection<D, P>(localContext, this::onConnectionClosed);
    return connection;
  }

  @Override
  public void onDocumentReset(LocalContext<D, P> localContext) {
//    localContext.getConnectionListener()
//      .onSendDocument(localContext, SendDocumentCause.DOCUMENT_RESET);
    logger.debug(
      "Document reset: local version = {}, remote version = {}",
      localContext.getLocalShadowVersion(),
      localContext.getRemoteShadowVersion());
  }

  
  @Override
  public void onEditApplied(LocalContext<D, P> localContext, Edit<P> edit, long ackedLocalVersion, boolean collision) {
    localContext.getConnectionListener()
      .onSendEdits(localContext, SendEditsCause.EDITS_ACKED);
    
    D document = localContext.getSharedContext().getDocument();
    for (ClientListener<D, P> clientListener : new ArrayList<>(clientListeners)) {
      clientListener.onEditProcessed(
        document, 
        edit, 
        localContext.getRemoteShadowVersion());
    }
  }
  
  @Override
  public void onEditIgnored(LocalContext<D, P> localContext, Edit<P> edit, long ackedLocalVersion, EditIgnoredReason reason) {
  }
  
  @Override
  public void onDocumentUpdated(LocalContext<D, P> localContext) {
    localContext.getConnectionListener()
      .onSendEdits(localContext, SendEditsCause.DOCUMENT_UPDATED);
    logger.debug(
      "Document updated: local version = {}, remote version = {}",
      localContext.getLocalShadowVersion(),
      localContext.getRemoteShadowVersion());
    
    D document = localContext.getSharedContext().getDocument();
    for (ClientListener<D, P> clientListener : new ArrayList<>(clientListeners)) {
      clientListener.onDocumentUpdated(
        document, 
        localContext.getLocalShadowVersion());
    }
  }
  
  private void onConnectionClosed(Connection<D, P> connection) {
    this.connection = null;
    logger.debug("Client connection closed.");
  }
  
  private Connection<D, P> getOpenConnection() {
    if (connection == null) {
      throw new IllegalStateException("Connection not established.");
    }
    
    return connection;
  }
  
  public void addClientListener(ClientListener<D, P> clientListener) {
    clientListeners.add(clientListener);
  }
  
  public void removeClientListener(ClientListener<D, P> clientListener) {
    clientListeners.remove(clientListener);
  }

  @Override
  public void onShadowRollback(LocalContext<D, P> localContext, long ackedLocalVersion) {
  }
}
