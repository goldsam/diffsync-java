package org.github.goldsam.diffsync.core.context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.github.goldsam.diffsync.core.ConnectionListener;
import org.github.goldsam.diffsync.core.Differencer;
import org.github.goldsam.diffsync.core.PatchFailedException;
import org.github.goldsam.diffsync.core.edit.Edit;
import org.github.goldsam.diffsync.core.edit.EditStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class LocalContext<D, P> {
  private static final Logger log = LoggerFactory.getLogger(LocalContext.class);
  
  private final SharedContext<D, P> sharedContext;
  private final EditStack<P> editStack;
  private final ConnectionListener<D, P> connectionListener;

  private D shadowDocument;
  private long remoteShadowVersion = -1L; // n (from server pov)
  private long localShadowVersion = -1L; // m (from server pov)
  
  private D shadowBackupDocument;
  private long localShadowBackupVersion = -1L; // m (from server pov)
  
  public LocalContext(SharedContext<D, P> sharedContext, EditStack<P> editStack, ConnectionListener<D, P> connectionListener) {
    this.sharedContext = sharedContext;
    this.editStack = editStack;
    this.connectionListener = connectionListener;
  }
  
  public List<Edit<P>> getEdits() {
    return editStack.getEdits();
  }

  public SharedContext<D, P> getSharedContext() {
    return sharedContext;
  }

  public ConnectionListener<D, P> getConnectionListener() {
    return connectionListener;
  }

  public long getRemoteShadowVersion() {
    return remoteShadowVersion;
  }

  public long getLocalShadowVersion() {
    return localShadowVersion;
  }
  
  public D getShadowDocument() {
    return shadowDocument;
  }
  
  public D getDocument() {
    return sharedContext.getDocument();
  }
  
  public void reset(D document, long version) {
    validateDocument(document);
    sharedContext.setDocument(document);
    resetImpl(document, version);
  }
  
  public void reset(long version) {
    resetImpl(sharedContext.getDocument(), version);
  }
  
  public void reset() {
    reset(localShadowVersion + 1);
  }
  
  private void resetImpl(D document, long version) {
    shadowDocument = document;
    localShadowVersion = version;
    remoteShadowVersion = version;

    shadowBackupDocument = null;
    localShadowBackupVersion = -1L;
    
    editStack.clear();
    sharedContext.getListener().onDocumentReset(this);
  }
  
  public void update() {
    updateImpl(sharedContext.getDocument());
  }
  
  public void update(D newCurrentDocument) {
    validateDocument(newCurrentDocument);
    updateImpl(newCurrentDocument);
  }
  
  public void update(Function<D, D> updater) {
    updateImpl(updater.apply(sharedContext.getDocument()));
  }
  
  private void updateImpl(D document) {
    if (sharedContext.getDocument() == null) {
      throw new IllegalStateException("Document not set on shared context.");
    }
    
    if (shadowDocument == null) {
      throw new IllegalStateException("Shadow document not set on locsl context.");
    }
    
    P patch = getDifferencer().difference(shadowDocument, document);    
    editStack.pushEdit(patch, localShadowVersion);

    shadowDocument = document;
    localShadowVersion++;
    
    sharedContext.setDocument(document);
    sharedContext.getListener().onDocumentUpdated(this);
  }
  
  protected Differencer<D, P> getDifferencer() {
    return sharedContext.getDifferencer();
  }
  
  void validateDocument(D document) {
    // TODO: Implement document validation support.
  }
  
  public void processEdits(List<Edit<P>> edits, long ackedLocalVersion) throws PatchFailedException {
    if (ackedLocalVersion != localShadowVersion && ackedLocalVersion == localShadowBackupVersion) {
      // Remote did not receive the last response - rollback shadow
      log.warn("Rollback from shadow {} to backup shadow {}", localShadowVersion, localShadowBackupVersion);
      shadowDocument = shadowBackupDocument;
      localShadowVersion = localShadowBackupVersion;
      editStack.clear();
      sharedContext.getListener().onShadowRollback(this, ackedLocalVersion);
    } else {
      editStack.popEdits(ackedLocalVersion);
    }
    
    for (Edit<P> edit : edits) {
      processEdit(edit, ackedLocalVersion);
    }
  }
  
  private void processEdit(Edit<P> edit, long ackedLocalVersion) throws PatchFailedException {
    if (ackedLocalVersion != localShadowVersion) {
      // Can't apply an edit on a mismatched shadow version.
      log.warn("Acknowledged version mismatch: local version = {}, acked version = {}", localShadowVersion, ackedLocalVersion);
      sharedContext.getListener().onEditIgnored(this, edit, ackedLocalVersion, EditIgnoredReason.ACK_VERSION_MISMATCH);
    } else if (edit.getVersion() > remoteShadowVersion) {
      // Client has a version in the future?
      log.warn("Cannot apply edit out of order: edit version = {}, expected version = {}", edit.getVersion(), remoteShadowVersion);
      sharedContext.getListener().onEditIgnored(this, edit, ackedLocalVersion, EditIgnoredReason.OUT_OF_ORDER); 
    } else if (edit.getVersion() < remoteShadowVersion) {
      // We've already seen this edit.
      log.debug("Ignoring previously applied edit: edit version = {}, expected version = {}", edit.getVersion(), remoteShadowVersion);
      sharedContext.getListener().onEditIgnored(this, edit, ackedLocalVersion, EditIgnoredReason.ALREADY_APPLIED);
    } else {
      boolean collision = false;
      Differencer<D, P> differencer = getDifferencer();
      D newShadowDocument = differencer.patch(shadowDocument, edit.getPatch(), false);
      validateDocument(newShadowDocument);
      try {
        D newCurrentDocument = differencer.patch(sharedContext.getDocument(), edit.getPatch(), true);
        sharedContext.setDocument(newCurrentDocument);
        log.debug("Successfully applied edit to shared document: edit version = {}, acked version = {}", edit.getVersion(), ackedLocalVersion);
      } catch (PatchFailedException e) {
        log.warn("Collision while applying edit to shared document: edit version = {}, acked version = {}", edit.getVersion(), ackedLocalVersion, e);
        collision = true;
      }

      remoteShadowVersion++;
      shadowDocument = newShadowDocument;
      shadowBackupDocument = newShadowDocument;
      localShadowBackupVersion = localShadowVersion;
      sharedContext.getListener().onEditApplied(this, edit, ackedLocalVersion, collision);
    }
  }
}
