package org.github.goldsam.diffsync.core.context;

import org.github.goldsam.diffsync.core.Differencer;

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

  public ContextListener<D, P> getListener() {
    return listener;
  }
}
