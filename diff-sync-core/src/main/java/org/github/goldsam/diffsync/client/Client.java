package org.github.goldsam.diffsync.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.github.goldsam.diffsync.ClientListener;
import org.github.goldsam.diffsync.Connectable;
import org.github.goldsam.diffsync.Connection;
import org.github.goldsam.diffsync.ConnectionListener;
import org.github.goldsam.diffsync.SendDocumentCause;
import org.github.goldsam.diffsync.SendEditsCause;
import org.github.goldsam.diffsync.SynchronizationContext;
import org.github.goldsam.diffsync.edit.EditStack;
import org.github.goldsam.diffsync.edit.EditStackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.github.goldsam.diffsync.diffpatch.DiffPatch;
import org.github.goldsam.diffsync.SynchronizationHandler;
import org.github.goldsam.diffsync.SynchronizedView;
import org.github.goldsam.diffsync.edit.Edit;
import org.github.goldsam.diffsync.edit.EditProcessingResult;

public class Client<D, P> implements SynchronizationHandler<D, P>, Connectable<D, P> {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  
  private final SynchronizationContext<D, P> sharedContext;
  private final EditStackFactory<P> editStackFactory;  
  private final List<ClientListener<D, P>> clientListeners = new ArrayList<>();
  private Connection<D, P> connection;
  
  public Client(DiffPatch<D, P> differencer, EditStackFactory<P> editStackFactory, boolean usingShadowBackups) {
    sharedContext = new SynchronizationContext<>(differencer, this, usingShadowBackups);
    this.editStackFactory = editStackFactory;
  }
  
  public void update(D document) { 
    getOpenConnection().getSynchronizedView().update(document);
  }
  
  public void update(Function<D, D> updater) { 
    getOpenConnection().getSynchronizedView().update(updater);
  }
  
  public void reset() {
    SynchronizedView<D, P> view = getOpenConnection().getSynchronizedView();
    view.getConnectionListener().onSendResetRequest(view);
  }

  private void onConnectionClosed(Connection<D, P> connection) {
    this.connection = null;
    logger.debug("Client connection closed.");
  }
  
  private Connection<D, P> getOpenConnection() {
    if (connection == null) {
      throw new IllegalStateException("Connection not open.");
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
  public void shadowRollback(SynchronizedView<D, P> view, long oldLocalShadowVersion, long ackedLocalVersion) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void editProcessed(SynchronizedView<D, P> view, Edit<P> edit, long ackedLocalVersion, EditProcessingResult editProcessingResult, D updatedDocument) {
    view.getConnectionListener().onSendEdits(view, SendEditsCause.EDITS_ACKED);
    D document = view.getSynchronizationContext().getDocument();
    for (ClientListener<D, P> clientListener : new ArrayList<>(clientListeners)) {
      clientListener.editProcessed(document, edit, view.getRemoteShadowVersion());
    }
  }

  @Override
  public void allEditsApplied(SynchronizedView<D, P> view, long ackedLocalVersion, D updatedDocument) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void documentUpdated(SynchronizedView<D, P> view) {
    view.getConnectionListener()
      .onSendEdits(view, SendEditsCause.DOCUMENT_UPDATED);
    logger.debug(
      "Document updated: local version = {}, remote version = {}",
      view.getLocalShadowVersion(),
      view.getRemoteShadowVersion());
    
    D document = view.getSynchronizationContext().getDocument();
    for (ClientListener<D, P> clientListener : new ArrayList<>(clientListeners)) {
      clientListener.documentUpdated(
        document, 
        view.getLocalShadowVersion());
    }
  }

  @Override
  public void documentReset(SynchronizedView<D, P> view) {
    view.getConnectionListener().onSendDocument(view, SendDocumentCause.DOCUMENT_RESET);
    logger.debug(
      "Client reset: local version = {}, remote version = {}",
      view.getLocalShadowVersion(),
      view.getRemoteShadowVersion());
  }

  @Override
  public Connection<D, P> connect(ConnectionListener<D, P> connectionListener) throws Exception {
    if (connection != null) {
      throw new IllegalStateException("Client already connected.");
    }
    
    EditStack<P> editStack = editStackFactory.createEditStack();
    SynchronizedView<D, P> localContext = new SynchronizedView<>(sharedContext, editStack, connectionListener);
    connection = new Connection<>(localContext, this::onConnectionClosed);
    return connection;
  }
}
