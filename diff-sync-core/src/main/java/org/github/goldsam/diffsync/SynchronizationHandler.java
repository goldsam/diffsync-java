package org.github.goldsam.diffsync;

import org.github.goldsam.diffsync.edit.EditProcessingResult;
import org.github.goldsam.diffsync.edit.Edit;

/**
 * Handler responsible for coordinating document synchronization.
 * 
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface SynchronizationHandler<D, P> {
  
  /**
   * Invoked when a {@link SynchronizedView}'s shadow document is rolled back to a 
   * previous version. Invocations of this method indicate loss of packets 
   * transmitted to the remote.
   *
   * @param view View for which the shadow document was rolled back to a previous version.
   * @param oldLocalShadowVersion Version of local shadow document prior to rollback.
   * @param ackedLocalVersion Acknowledged local version receive from remote.
   */
  void shadowRollback(SynchronizedView<D, P> view, long oldLocalShadowVersion, long ackedLocalVersion); 

  /**
   * Invoked when a single edit received from a peer is processed. If the edit 
   * is {@link EditProcessingResult#isApplied() applied}, then the client's 
   * {@link SynchronizedView#getShadowDocument() shadow document} and possibly 
   * the {@link SynchronizedDocumentContext#getDocument() synchronized document} 
   * and shadow document version numbers will have been modified. 
   * 
   * @param view View to which edit was applied.
   * @param edit Applied edit.
   * @param ackedLocalVersion Acknowledged local version receive from remote.
   * @param editProcessingResult Outcome of processing the {@code edit}.
   * 
   * @see SynchronizedDocumentContext#getDocument() 
   * @see SynchronizedView#getShadowDocument()
   */
  void editProcessed(
    SynchronizedView<D, P> view, 
    Edit<P> edit, 
    long ackedLocalVersion, 
    EditProcessingResult editProcessingResult,
    D updatedDocument);

  /**
   * Invoked when all received edits for a given peer have been applied
   * to that peer's {@link SynchronizedView}.
   *
   * @param view view to which edits were applied.
   * @param ackedLocalVersion Acknowledged local version receive from remote.
   */
  void allEditsApplied(SynchronizedView<D, P> view, long ackedLocalVersion, D updatedDocument); 
  
  /**
   * Invoked when a {@link SynchronizedView} has been updated, typically after 
   * having incoming edits applied, and has one or more pending outgoing edits 
   * to transmit to the corresponding peer.
   * 
   * @param view updated view.
   */
  void documentUpdated(SynchronizedView<D, P> view);
  
  /**
   * Invoked when a {@link SynchronizedView} has been reset and the 
   * the remote peer needs to reinitialize its synchronization state.
   * 
   * @param view reset view.
   */
  void documentReset(SynchronizedView<D, P> view);
}
