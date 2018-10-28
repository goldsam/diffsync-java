package org.github.goldsam.diffsync.core;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface SharedContextListener<D, P> {
  
  void onEditProcessed(
    LocalContext<D, P> processedContext, 
    boolean editApplied,
    P patch, 
    long patchSourceVersion, 
    long lastReceivedVersion);
  
}
