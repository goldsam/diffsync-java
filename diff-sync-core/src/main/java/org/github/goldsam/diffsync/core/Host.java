package org.github.goldsam.diffsync.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.github.goldsam.diffsync.core.context.ContextListener;
import org.github.goldsam.diffsync.core.context.LocalContext;
import org.github.goldsam.diffsync.core.context.SharedContext;
import org.github.goldsam.diffsync.core.edit.Edit;
import org.github.goldsam.diffsync.core.edit.EditStack;
import org.github.goldsam.diffsync.core.edit.EditStackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class Host<D, P> implements ContextListener<D, P>, Connectable<D, P> {
  private static final Logger logger = LoggerFactory.getLogger(Host.class);
  
  private final SharedContext<D, P> sharedContext;
  private final EditStackFactory<P> editStackFactory;
  private final List<Connection<D, P>> connections = new ArrayList<>();
  
  public Host(Differencer<D, P> differencer, EditStackFactory<P> editStackFactory) {
    sharedContext = new SharedContext(differencer, this, true);
    this.editStackFactory = editStackFactory;
  }    
  
  @Override
  public Connection<D, P> connect(ConnectionListener<D, P> connectionListener) {
    EditStack<P> editStack = editStackFactory.createEditStack();
    LocalContext<D, P> localContext = new LocalContext<>(sharedContext, editStack, connectionListener);
    Connection<D, P> connection = new Connection<>(localContext, this::onConnectionClosed);
    connections.add(connection);
//    connectionListener.onSendDocument(localContext, SendDocumentCause.CONNECTION_ESTABLISHED);
    return connection;
  }
  
  private void onConnectionClosed(Connection<D, P> connection) {
    connections.remove(connection);
    logger.debug("Host connection closed.");
  }

  public void reset(D document) {
    sharedContext.setDocument(document);
  }

  @Override
  public void onDocumentReset(LocalContext<D, P> localContext) {
    localContext.getConnectionListener().onSendDocument(localContext, SendDocumentCause.DOCUMENT_RESET);
    logger.debug(
      "Document reset: local version = {}, remote version = {}",
      localContext.getLocalVersion(),
      localContext.getRemoteVersion());
  }

  @Override
  public void onEditsProcessed(LocalContext<D, P> localContext, List<Edit<P>> processedEdits) {
    for(Connection<D, P> connection : connections) {
      LocalContext<D, P> connectionLocalContext = connection.getLocalContext();
      if (connectionLocalContext != localContext) {
        connectionLocalContext.update();
      } else {
        ConnectionListener<D, P> connectionListener = connectionLocalContext.getConnectionListener();
        connectionListener.onSendEdits(connectionLocalContext, SendEditsCause.EDITS_ACKED);
      }
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug(
        "Edits processed: edit version = {}, local version = {}, remote version = {}",
        processedEdits.stream()
          .map(e -> String.valueOf(e.getVersion()))
          .collect(Collectors.joining(",", "{", "}")),
        localContext.getLocalVersion(),
        localContext.getRemoteVersion());
    }
  }

  @Override
  public void onDocumentUpdated(LocalContext<D, P> localContext) {
    localContext.getConnectionListener().onSendEdits(localContext, SendEditsCause.DOCUMENT_UPDATED);
    logger.debug(
      "Document updated: local version = {}, remote version = {}",
      localContext.getLocalVersion(),
      localContext.getRemoteVersion());
  }

  @Override
  public void onCollision(LocalContext<D, P> localContext, Edit<P> collidingEdit) {
    logger.warn(
      "Collision: local version = {}, remote version = {}",
      localContext.getLocalVersion(),
      localContext.getRemoteVersion());
  }
}
