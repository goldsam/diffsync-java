package org.github.goldsam.diffsync.core;

import java.util.function.Function;
import org.github.goldsam.diffsync.core.edit.EditStack;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class LocalContext<D, P> {
  private final EditStack<P> editStack;
  private final LocalContextListener<D, P> listener;
  
  private SharedContext<D,P> sharedContext;
  
  private D shadowDocument;
  long shadowSourceVersion = -1L;
  
  private D backupDocument;
  long backupSourceVersion = -1L;
  
  /** Last acknowledged version received by the remote end. */
  long lastReceivedVersion; 
  
  
  public LocalContext(EditStack<P> editStack, LocalContextListener<D, P> listener) {
    this.editStack = editStack;
    this.listener = listener;
  }
  
  public void setSharedContext(SharedContext<D, P> sharedContext) {
    if (sharedContext != null && this.sharedContext != null) {
      throw new IllegalStateException("sharedContext already set");
    }
    this.sharedContext = sharedContext;
  }
  
  protected EditStack<P> getEditStack() {
    return editStack;
  }
  
  protected SharedContext<D,P> getSharedContext() {
    if (sharedContext == null) {
      throw new IllegalStateException("sharedContext is not set.");
    }
    return sharedContext;
  }
  
  public void initialize(D document) {
    getSharedContext().setDocument(document);
    backupDocument = document;
    backupSourceVersion = 0;
    
    shadowDocument = document;
    shadowSourceVersion = 0;
  }
 
  public void update() {
    updateImpl(getSharedContext().getDocument());
  }
  
  public void update(D newCurrentDocument) {
    validateDocument(newCurrentDocument);
    updateImpl(newCurrentDocument);
  }
  
  public void update(Function<D, D> updater) {
    updateImpl(updater.apply(getSharedContext().getDocument()));
  }
  
  private void updateImpl(D document) {
    if (getSharedContext().getDocument() == null) {
      throw new RuntimeException("Document must first be initialized.");
    }    
    
    P patch = getDifferencer().difference(shadowDocument, document);    
    EditStack<P> es = getEditStack();
    es.pushEdit(patch, shadowSourceVersion);

    shadowDocument = document;
    shadowSourceVersion++;
    
    SharedContext<D, P> sc = getSharedContext();
    if (sc.isUsingShadowBackups()) {
      backupDocument = shadowDocument;
      backupSourceVersion = shadowSourceVersion; 
    }
    
    sc.setDocument(document);
    listener.onDocumentUpdated(this, es.getPatches(), es.getNewestPatchSourceVersion(), lastReceivedVersion);
  }
  
  protected Differencer<D, P> getDifferencer() {
    return getSharedContext().getDifferencer();
  }
  
  void validateDocument(D document) {
    getSharedContext().validateDocument(document);
  }
  
  public void processEdit(P patch, long patchSourceVersion, long lastReceivedLocalVersion) throws PatchFailedException {
   
    boolean editApplied = tryPatchShadow(patch, patchSourceVersion, lastReceivedLocalVersion);
    if (!editApplied &&
        getSharedContext().isUsingShadowBackups() &&
        (lastReceivedLocalVersion == backupSourceVersion) && 
        (patchSourceVersion == backupSourceVersion)) 
    {
       shadowDocument = backupDocument;
       shadowSourceVersion= backupSourceVersion;
       getEditStack().clear();
       editApplied = tryPatchShadow(patch, patchSourceVersion, lastReceivedLocalVersion);
    }
    
    getSharedContext().editProcessed(this, editApplied, patch, patchSourceVersion, lastReceivedLocalVersion);
  }
  
  private boolean tryPatchShadow(P patch, long patchSourceVersion, long lastReceivedVersion) throws PatchFailedException {
    if((lastReceivedVersion == shadowSourceVersion) && 
       (patchSourceVersion == shadowSourceVersion)) 
    {
      Differencer<D, P> differencer = getDifferencer();
      D newShadowDocument = differencer.patch(shadowDocument, patch, false);
      validateDocument(newShadowDocument);
      
      SharedContext<D, P> sc = getSharedContext();

      D newCurrentDocument;
      try
      {
        newCurrentDocument = differencer.patch(sc.getDocument(), patch, true);
      } catch (PatchFailedException e) {
        handleCollision(patch, patchSourceVersion, lastReceivedVersion);
        return false;
      }
      
      validateDocument(newCurrentDocument);
      
      shadowDocument = newShadowDocument;
      sc.setDocument(newCurrentDocument);

      getEditStack().purgeEdits(lastReceivedVersion);
      this.lastReceivedVersion = lastReceivedVersion;
      return true;
    }
    
    return false;
  }
  
  private void handleCollision(P patch, long patchSourceVersion, long lastReceivedLocalVersion) {
    
  }
  
}
