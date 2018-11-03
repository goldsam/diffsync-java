package org.github.goldsam.diffsync.core.context;

import java.util.List;
import org.github.goldsam.diffsync.core.Differencer;
import org.github.goldsam.diffsync.core.edit.Edit;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class SharedContext<D, P> {
  private final Differencer<D, P> differencer;
  private final ContextListener<D, P> listener;
  private final boolean usingShadowBackups;
  private D document;
  
  public SharedContext(Differencer<D, P> differencer, ContextListener<D, P> listener, boolean usingShadowBackups) {
    this.differencer = differencer;
    this.listener = listener;
    this.usingShadowBackups = usingShadowBackups;
  }
  
  public SharedContext(Differencer<D, P> differencer, ContextListener<D, P> listener, boolean usingShadowBackups, D document) {
    this(differencer, listener, usingShadowBackups);
    this.document = document;  
  }
  
  public Differencer<D, P> getDifferencer() {
    return differencer;
  }
  
  public boolean isUsingShadowBackups() {
    return usingShadowBackups;
  }  
  public D getDocument() {
    return document;
  }
  
  public void setDocument(D document) {
    this.document = document;
  }
 
  public void onDocumentReset(LocalContext<D, P> localContext) {
    listener.onDocumentReset(localContext);
  }
    
  public void onEditsProcessed(LocalContext<D, P> localContext, List<Edit<P>> processedEdits) {
    listener.onEditsProcessed(localContext, processedEdits);
  }
 
  public void onDocumentUpdated(LocalContext<D, P> localContext) {
    listener.onDocumentUpdated(localContext);
  }
  
  public void handleCollision(LocalContext<D, P> localContext, Edit<P> collidingEdit) {
    listener.onCollision(localContext, collidingEdit);
  }
  
  public void validateDocument(LocalContext<D, P> localContext, D document) {
    // TODO: implement me!
  }
}
