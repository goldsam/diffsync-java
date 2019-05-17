package org.github.goldsam.diffsync.host;

import java.util.ArrayList;
import java.util.List;
import org.github.goldsam.diffsync.Connectable;
import org.github.goldsam.diffsync.Connection;
import org.github.goldsam.diffsync.ConnectionListener;
import org.github.goldsam.diffsync.SendDocumentCause;
import org.github.goldsam.diffsync.SendEditsCause;
import org.github.goldsam.diffsync.SynchronizationContext;
import org.github.goldsam.diffsync.SynchronizedView;
import org.github.goldsam.diffsync.edit.Edit;
import org.github.goldsam.diffsync.edit.EditStack;
import org.github.goldsam.diffsync.edit.EditStackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.github.goldsam.diffsync.diffpatch.DiffPatch;
import org.github.goldsam.diffsync.SynchronizationHandler;
import org.github.goldsam.diffsync.edit.EditProcessingResult;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class Host<D, P> implements SynchronizationHandler<D, P>, Connectable<D, P> {
  private static final Logger logger = LoggerFactory.getLogger(Host.class);
  
  private final SynchronizationContext<D, P> sharedContext;
  private final EditStackFactory<P> editStackFactory;
  private final List<Connection<D, P>> connections = new ArrayList<>();
  
  public Host(DiffPatch<D, P> differencer, EditStackFactory<P> editStackFactory, D document) {
    sharedContext = new SynchronizationContext(differencer, this, true, document);
    this.editStackFactory = editStackFactory;
  }    
  
  public Connection<D, P> connect(ConnectionListener<D, P> connectionListener) {
    EditStack<P> editStack = editStackFactory.createEditStack();
    SynchronizedView<D, P> view = new SynchronizedView<>(sharedContext, editStack, connectionListener);
    Connection<D, P> connection = new Connection<>(view, this::onConnectionClosed);
    connections.add(connection);
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
  public void shadowRollback(SynchronizedView<D, P> view, long oldLocalShadowVersion, long ackedLocalVersion) {
    
  }

  @Override
  public void editProcessed(SynchronizedView<D, P> view, Edit<P> edit, long ackedLocalVersion, EditProcessingResult editProcessingResult, D updatedDocument) {
    if (editProcessingResult.isApplied()) {
      for(Connection<D, P> connection : connections) {
        SynchronizedView<D, P> connectionLocalContext = connection.getSynchronizedView();
        if (connectionLocalContext != view) {
          connectionLocalContext.update();
        } else {
          ConnectionListener<D, P> connectionListener = connectionLocalContext.getConnectionListener();
          connectionListener.onSendEdits(connectionLocalContext, SendEditsCause.EDITS_ACKED);
        }
      }

      logger.debug(
        "Edits processed: edit version = {}, local version = {}, remote version = {}",
        edit.getVersion(),
        view.getLocalShadowVersion(),
        view.getRemoteShadowVersion());
      
    } else {
      logger.debug(
        "edit ignored: acked local version = {}, edit ingored reason = {}",
        view.getLocalShadowVersion(),
        editProcessingResult.toString());
    }
  }

  @Override
  public void allEditsApplied(SynchronizedView<D, P> view, long ackedLocalVersion, D updatedDocument) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void documentUpdated(SynchronizedView<D, P> view) {
    view.getConnectionListener().onSendEdits(view, SendEditsCause.DOCUMENT_UPDATED);
    logger.debug(
      "Document updated: local version = {}, remote version = {}",
      view.getLocalShadowVersion(),
      view.getRemoteShadowVersion());
  }

  @Override
  public void documentReset(SynchronizedView<D, P> view) {
    view.getConnectionListener().onSendDocument(view, SendDocumentCause.DOCUMENT_RESET);
    logger.debug(
      "Document reset: local version = {}, remote version = {}",
      view.getLocalShadowVersion(),
      view.getRemoteShadowVersion());
  }
}
