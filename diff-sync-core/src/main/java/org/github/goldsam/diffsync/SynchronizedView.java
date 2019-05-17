package org.github.goldsam.diffsync;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.github.goldsam.diffsync.diffpatch.PatchFailedException;
import org.github.goldsam.diffsync.diffpatch.DiffPatch;
import org.github.goldsam.diffsync.diffpatch.PatchOptions;
import org.github.goldsam.diffsync.edit.EditProcessingResult;
import org.github.goldsam.diffsync.edit.Edit;
import org.github.goldsam.diffsync.edit.EditStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local view of a remote peer's synchronized state.
 * This view includes shadow document copies, version counters, a stack of 
 * pending edits to be applied and acknowledged by the remote 
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class SynchronizedView<D, P> {
  private static final Logger log = LoggerFactory.getLogger(SynchronizedView.class);
  
  private final SynchronizationContext<D, P> context;
  
  /*
   * Stack of edits to be applied by remote.
   */
  private final EditStack<P> editStack;
  private final ConnectionListener<D, P> connectionListener;

  private D shadowDocument;
  private long remoteShadowVersion = 0; // n (from server pov)
  private long localShadowVersion = 0; // m (from server pov)
  
  private D shadowBackupDocument;
  private long localShadowBackupVersion = -1L; // m (from server pov)
  
  public SynchronizedView(SynchronizationContext<D, P> context, EditStack<P> editStack, ConnectionListener<D, P> connectionListener) {
    this.context = context;
    this.editStack = editStack;
    this.connectionListener = connectionListener;
    this.shadowDocument = context.getDocument();
//    if(context.isUsingShadowBackups()) {
//      this.shadowBackupDocument = this.shadowBackupDocument;
//    }
  }
  
  public List<Edit<P>> getEdits() {
    return editStack.getEdits();
  }

  public SynchronizationContext<D, P> getSynchronizationContext() {
    return context;
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
  
  public void reset(D document, long version) {
    validateDocument(document);
    context.setDocument(document);
    
    resetImpl(document, version);
  }
  
  public void reset(long version) {
    resetImpl(context.getDocument(), version);
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
    context.getSynchronizationHandler().documentReset(this);
  }
  
  public void update() {
    updateImpl(context.getDocument());
  }
  
  public void update(D newCurrentDocument) {
    validateDocument(newCurrentDocument);
    updateImpl(newCurrentDocument);
  }
  
  public void update(Function<D, D> updater) {
    updateImpl(updater.apply(context.getDocument()));
  }
  
  private void updateImpl(D document) {
    if (context.getDocument() == null) {
      throw new IllegalStateException("Document not set on shared context.");
    }
    
    if (shadowDocument == null) {
      throw new IllegalStateException("Shadow document not set on locsl context.");
    }
    
    P patch = context.getDiffPatch().diff(shadowDocument, document);
    editStack.pushEdit(patch, localShadowVersion);

    shadowDocument = document;
    localShadowVersion++;
    
    context.setDocument(document);
    context.getSynchronizationHandler().documentUpdated(this);
  }
  
  void validateDocument(D document) {
    // TODO: Implement document validation support.
  }
  
  public void processEdits(List<Edit<P>> edits, long ackedLocalVersion) throws PatchFailedException {    
    if (ackedLocalVersion != localShadowVersion && ackedLocalVersion == localShadowBackupVersion) {
      // Remote did not receive the last response - rollback shadow
      log.debug("Rollback from shadow {} to backup shadow {}", localShadowVersion, localShadowBackupVersion);
      long oldLocalShadowVersion = localShadowVersion;
      shadowDocument = shadowBackupDocument;
      localShadowVersion = localShadowBackupVersion;
      editStack.clear();
      context.getSynchronizationHandler().shadowRollback(this, oldLocalShadowVersion, ackedLocalVersion);
    } else {
      editStack.popEdits(ackedLocalVersion);
    }

    D updatedDocument = null;
    for (Edit<P> edit : edits) {
      updatedDocument = processEdit(edit, ackedLocalVersion).orElse(updatedDocument);
    }
    
    context.getSynchronizationHandler().allEditsApplied(this, ackedLocalVersion, updatedDocument);
  }
  
  private Optional<D> processEdit(Edit<P> edit, long ackedLocalVersion) throws PatchFailedException {
    D updatedDocument = null;
    EditProcessingResult result = EditProcessingResult.APPLIED_NO_COLLISION;
    if (ackedLocalVersion != localShadowVersion) {
      // Can't apply an edit on a mismatched shadow version.
      result = EditProcessingResult.DISCARDED_ACK_VERSION_MISMATCH;
      log.warn("Acknowledged version mismatch: local version = {}, acked version = {}", localShadowVersion, ackedLocalVersion);
    } else if (edit.getVersion() > remoteShadowVersion) {
      // Client has a version in the future?
      result = EditProcessingResult.DISCARDED_OUT_OF_ORDER;
      log.warn("Cannot apply edit out of order: edit version = {}, expected version = {}", edit.getVersion(), remoteShadowVersion);
    } else if (edit.getVersion() < remoteShadowVersion) {
      // We've already seen this edit.
      result = EditProcessingResult.DISCARDED_ALREADY_APPLIED;
      log.debug("Discarding previously applied edit: edit version = {}, expected version = {}", edit.getVersion(), remoteShadowVersion);
    } else {
      DiffPatch<D, P> diffPatch = context.getDiffPatch();
      D newShadowDocument = diffPatch.patch(shadowDocument, edit.getPatch(), PatchOptions.Builder.withStrict());
      validateDocument(newShadowDocument);
      try {
        updatedDocument = context.patchAndGetDocument(edit.getPatch(), PatchOptions.Builder.withFuzzy());
        log.debug("Successfully applied edit to shared document: edit version = {}, acked version = {}", edit.getVersion(), ackedLocalVersion);
      } catch (PatchFailedException e) {
        if (shouldDiscardCollidingEdit(edit, ackedLocalVersion)) {
          result = EditProcessingResult.DISCARDED_DUE_TO_COLLISION;
        } else {
          updatedDocument = context.getDocument();
          result = EditProcessingResult.APPLIED_COLLISION; 
        }
        
        log.debug("Collision while applying edit to shared document: edit version = {}, acked version = {}", edit.getVersion(), ackedLocalVersion);
      }
      
      if (result.isApplied()) {
        remoteShadowVersion++;
        shadowDocument = newShadowDocument;
        shadowBackupDocument = newShadowDocument;
        localShadowBackupVersion = localShadowVersion;
      }
    }
    
    context.getSynchronizationHandler().editProcessed(this, edit, ackedLocalVersion, result, updatedDocument);
    return Optional.ofNullable(updatedDocument);
  }
  
  private boolean shouldDiscardCollidingEdit(Edit<P> edit, long ackedLocalVersion) {
    return false;
  }
}
