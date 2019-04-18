package org.github.goldsam.diffsync.core.context;

import org.github.goldsam.diffsync.core.edit.Edit;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface ContextListener<D, P> {
  
  void onDocumentReset(LocalContext<D, P> localContext);
  
  /**
   * Invoked when a {@link LocalContext}'s shadow document is rolled back to a 
   * previous version. Invocations of this method indicate loss of packets 
   * transmitted to the remote.
   *
   * @param localContext Local context which was rolled back.
   * @param ackedLocalVersion Acknowledged local version receive from remote.
   */
  void onShadowRollback(LocalContext<D, P> localContext, long ackedLocalVersion); 

  /**
   * Invoked when a processed edit is successfully applied to a {@link LocalContext}'s 
   * shadow document and possibly the {@link SharedContext}'s document.
   * 
   * @param localContext Originating (updated) local context.
   * @param edit Applied edit.
   * @param ackedLocalVersion Acknowledged local version receive from remote.
   * @param collision {@literal true} if a collision occurred indicating that 
   *        the {@link SharedContext}'s document was not updated.
   * 
   * @see SharedContext#getDocument() 
   * @see LocalContext#getShadowDocument()
   */
  void onEditApplied(LocalContext<D, P> localContext, Edit<P> edit, long ackedLocalVersion, boolean collision);

  /**
   * Invoked when a processed edit is ignored for one of various reasons.
   * 
   * @param localContext Originating local context.
   * @param edit Ignored edit.
   * @param ackedLocalVersion Acknowledged local version receive from remote.
   * @param reason Indicates the reason the edit was ignored.
   */
  void onEditIgnored(LocalContext<D, P> localContext, Edit<P> edit, long ackedLocalVersion, EditIgnoredReason reason);
  
  /**
   * Invoked when a {@link LocalContext's shadow document has been updated 
   * by a locally-origniating process resulting in an outgoing edit being 
   * pushed on the {@link LocalContext}'s edit stack.
   * 
   * @param localContext Originating (updated) local context.
   */
  void onDocumentUpdated(LocalContext<D, P> localContext);
}
