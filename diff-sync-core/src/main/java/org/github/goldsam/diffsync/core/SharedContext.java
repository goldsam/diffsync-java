package org.github.goldsam.diffsync.core;

import java.util.ArrayList;
import java.util.List;
import org.github.goldsam.diffsync.core.edit.EditStackFactory;
import org.github.goldsam.diffsync.core.edit.EditStack;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class SharedContext<D, P> {
  private final Differencer<D, P> differencer;
  private final EditStackFactory<P> editStackFactory;
  private final SharedContextListener<D, P> listener;
  private final boolean usingShadowBackups;
  
  private final List<LocalContext<D, P>> localContexts = new ArrayList<>();

  public boolean isUsingShadowBackups() {
    return usingShadowBackups;
  }
    
  private D document;
  
  public SharedContext(
    Differencer<D, P> differencer, 
    EditStackFactory<P> editStackFactory,
    SharedContextListener<D, P> listener, 
    boolean usingShadowBackups) 
  {
    this.differencer = differencer;
    this.editStackFactory = editStackFactory;
    this.listener = listener;
    this.usingShadowBackups = usingShadowBackups;
  }
  
  public Differencer<D, P> getDifferencer() {
    return differencer;
  }
    
  public D getDocument() {
    return document;
  }
  
  public void setDocument(D document) {
    this.document = document;
  }
  
  public void editProcessed(
    LocalContext<D, P> processedContext, 
    boolean editApplied,
    P patch, 
    long patchSourceVersion, 
    long lastReceivedVersion) 
  {
    if (editApplied) {
      for(LocalContext<D, P> localContext : localContexts) {
        localContext.update();
      }
    } else {
      processedContext.update();
    }
    
    listener.onEditProcessed(processedContext, editApplied, patch, patchSourceVersion, lastReceivedVersion);
  }
  
  public EditStack<P> createEditStack(LocalContext<D, P> localContext) {
    return editStackFactory.createEditStack(localContext);
  }
  
  public void validateDocument(D document) {
    // TODO: implement me!
  }
  
  void addLocalContet(LocalContext<D, P> localContext) {
    localContext.setSharedContext(this);
    this.localContexts.add(localContext);
  }
  
  void removeLocalContet(LocalContext<D, P> localContext) {
    if (this.localContexts.remove(localContext)) {
      localContext.setSharedContext(null);
    }
  }
}
