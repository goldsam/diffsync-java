package org.github.goldsam.diffsync.core.context;

import java.util.List;
import org.github.goldsam.diffsync.core.edit.Edit;

/**
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public interface ContextListener<D, P> {
  
  void onDocumentReset(LocalContext<D, P> localContext);
  
  void onEditsProcessed(LocalContext<D, P> localContext, List<Edit<P>> processedEdits);
  
  void onDocumentUpdated(LocalContext<D, P> localContext);
  
  void onCollision(LocalContext<D, P> localContext, Edit<P> collidingEdit);
}
