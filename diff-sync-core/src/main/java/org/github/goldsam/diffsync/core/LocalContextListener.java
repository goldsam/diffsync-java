package org.github.goldsam.diffsync.core;

import java.util.Collection;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface LocalContextListener<D, P> {
  void onDocumentInitialize(
    LocalContext<D, P> source,
    D document);
  
  void onDocumentUpdated(
    LocalContext<D, P> source,
    Collection<P> patches, 
    long newestPatchSourceVersion,
    long lastReceviedVersion);
  
}
