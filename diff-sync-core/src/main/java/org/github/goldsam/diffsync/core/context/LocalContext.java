package org.github.goldsam.diffsync.core.context;

import java.util.List;
import java.util.function.Function;
import org.github.goldsam.diffsync.core.ConnectionListener;
import org.github.goldsam.diffsync.core.Differencer;
import org.github.goldsam.diffsync.core.PatchFailedException;
import org.github.goldsam.diffsync.core.edit.Edit;
import org.github.goldsam.diffsync.core.edit.EditStack;
import org.github.goldsam.diffsync.core.edit.EditUtils;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class LocalContext<D, P> {
  private final SharedContext<D, P> sharedContext;
  private final EditStack<P> editStack;
  private final ConnectionListener<D, P> connectionListener;

  private D shadow;
  private long remoteVersion = -1L; // n (from server pov)
  private long localVersion = -1L; // m (from server pov)
  
  private D backup;
  private long backupVersion = -1L; // m (from server pov)
  
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

  public long getRemoteVersion() {
    return remoteVersion;
  }

  public long getLocalVersion() {
    return localVersion;
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
    reset(localVersion + 1);
  }
  
  private void resetImpl(D document, long version) {
    shadow = document;
    localVersion = version;
    remoteVersion = version;

    backup = null;
    backupVersion = -1L;
    
    editStack.clear();
    sharedContext.onDocumentReset(this);
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
      throw new IllegalStateException("Document not initialized.");
    }
    
    if (shadow == null) {
      throw new IllegalStateException("Local context not initialized.");
    }
    
    P patch = getDifferencer().difference(shadow, document);    
    editStack.pushEdit(patch, localVersion);

    shadow = document;
    localVersion++;
    
    sharedContext.setDocument(document);
    sharedContext.onDocumentUpdated(this);
  }
  
  protected Differencer<D, P> getDifferencer() {
    return sharedContext.getDifferencer();
  }
  
  void validateDocument(D document) {
    sharedContext.validateDocument(this, document);
  }
  
  public void processEdits(List<Edit<P>> edits, long lastReceivedRemoteVersion) throws PatchFailedException {
    edits.sort(EditUtils.getVersionComparator());
    
    if (!edits.isEmpty()) { 
      for(Edit<P> edit : edits) {
        if (!processEdit(edit, lastReceivedRemoteVersion)) {
          edits = edits.subList(0, edits.indexOf(edit));
          break;
        }
      }
    
      sharedContext.onEditsProcessed(this, edits);
    } else {
      editStack.popEdits(lastReceivedRemoteVersion);
    }
  }
  
  private boolean processEdit(Edit<P> edit, long lastReceivedRemoteVersion) throws PatchFailedException {   
    boolean editApplied = false;
    if (lastReceivedRemoteVersion == localVersion && edit.getVersion() == remoteVersion) {
//    if (lastReceivedRemoteVersion == remoteVersion && edit.getVersion() == localVersion) {
      editApplied = tryApplyEdit(edit, lastReceivedRemoteVersion);
    } else if (lastReceivedRemoteVersion < remoteVersion) {
      // duplicate message received.
    } else if (lastReceivedRemoteVersion == backupVersion && edit.getVersion() == remoteVersion) {
      shadow = backup;
      localVersion = backupVersion;
      editStack.clear();
      editApplied = tryApplyEdit(edit, lastReceivedRemoteVersion);
    } 
  
    return editApplied;
  }
  
  /**
   * Attempts to apply a batch to shared and shadow documents. 
   * @param edit Edit to apply.
   * @param patchRemoteVersion Remote document version the patch was created from.
   * @param lastReceivedRemoteVersion
   * @return {@literal true} if the shadow was successfully patched; 
   *         {@literal false} if a conflict occurred while patch 
   * @throws PatchFailedException 
   */
  private boolean tryApplyEdit(Edit<P> edit, long lastReceivedRemoteVersion) throws PatchFailedException {
    Differencer<D, P> differencer = getDifferencer();
    D newShadowDocument = differencer.patch(shadow, edit.getPatch(), false);
    validateDocument(newShadowDocument);

    D newCurrentDocument;
    try {
      newCurrentDocument = differencer.patch(sharedContext.getDocument(), edit.getPatch(), true);
    } catch (PatchFailedException e) {
      sharedContext.handleCollision(this, edit);
      return false;
    }

    validateDocument(newCurrentDocument);
    sharedContext.setDocument(newCurrentDocument);

    shadow = newShadowDocument;
    remoteVersion++;

    backup = newShadowDocument;
    backupVersion = localVersion;

    editStack.popEdits(lastReceivedRemoteVersion);
    return true;
  }
}
