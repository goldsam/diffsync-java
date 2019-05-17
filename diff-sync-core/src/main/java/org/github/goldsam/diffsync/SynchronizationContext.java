package org.github.goldsam.diffsync;

import java.util.concurrent.atomic.AtomicReference;
import org.github.goldsam.diffsync.diffpatch.DiffPatch;
import org.github.goldsam.diffsync.diffpatch.PatchOptions;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class SynchronizationContext<D, P> {
  private final DiffPatch<D, P> diffPatch;
  private final SynchronizationHandler<D, P> synchronizationHandler;
  private final boolean usingShadowBackups;
 private final AtomicReference<D> document;
  
  public SynchronizationContext(DiffPatch<D, P> diffPatch, SynchronizationHandler<D, P> synchronizationHandler, boolean usingShadowBackups, D document) {
    this.diffPatch = diffPatch;
    this.synchronizationHandler = synchronizationHandler;
    this.usingShadowBackups = usingShadowBackups;
    this.document = new AtomicReference<>(document);  
  }
  
  public SynchronizationContext(DiffPatch<D, P> diffPatch, SynchronizationHandler<D, P> synchronizationHandler, boolean usingShadowBackups) {
    this(diffPatch, synchronizationHandler, usingShadowBackups, null);
  }
  
  D patchAndGetDocument(P patch, PatchOptions options) {
    return document.updateAndGet(oldDocument -> diffPatch.patch(oldDocument, patch, options));
  }
  
  /**
   * Returns document diff and patch patch operations.
   * @return document diff and patch patch operations..
   */
  public DiffPatch<D, P> getDiffPatch() {
    return diffPatch;
  }
  
  /**
   * Returns {@code true} to indicate that bound {@link SynchronizedView} 
   * should make and use backups of shadow document to support rollback
   * when transmitted edits are lost.
   * @return {@code true} to indicate the use of shadow document backups.
   */
  public boolean isUsingShadowBackups() {
    return usingShadowBackups;
  }  
  
  /**
   * Returns the authoritative (i.e. "true") document to which peers are synchronized.
   * @return authoritative shared document.
   */
  public D getDocument() {
    return document.get();
  }
  
  /**
   * Sets the authoritative (i.e. "true") document to which peers are synchronized.
   * @param document authoritative shared document.
   */
  public void setDocument(D document) {
    this.document.set(document);
  }

  public SynchronizationHandler<D, P> getSynchronizationHandler() {
    return synchronizationHandler;
  }
}
