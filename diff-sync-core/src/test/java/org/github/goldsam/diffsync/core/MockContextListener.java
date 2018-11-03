package org.github.goldsam.diffsync.core;

import org.github.goldsam.diffsync.core.context.LocalContext;
import org.github.goldsam.diffsync.core.context.ContextListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.github.goldsam.diffsync.core.edit.Edit;

public class MockContextListener<D, P> implements ContextListener<D, P> {

  @Override
  public void onDocumentReset(LocalContext<D, P> localContext) {
  }

  @Override
  public void onEditsProcessed(LocalContext<D, P> localContext, List<Edit<P>> edits) {
    processedEdits.add(new ProcessedEdit<>(
      localContext, 
      edits, 
      localContext.getSharedContext().getDocument(), 
      localContext.getRemoteVersion()));   
  }

  @Override
  public void onDocumentUpdated(LocalContext<D, P> localContext) {
  }

  @Override
  public void onCollision(LocalContext<D, P> localContext, Edit<P> collidingEdit) {
  }

  public static class ProcessedEdit<S, U> {
    private final LocalContext<S, U> localContext;
    private final List<Edit<U>> edits;
    private final S document;
    private final long remoteVersion; 
      
    public ProcessedEdit(LocalContext<S, U> localContext, List<Edit<U>> edits, S document, long lastReceivedVersion) {
      this.localContext = localContext;
      this.edits = edits;
      this.document = document;
      this.remoteVersion = lastReceivedVersion;
    }

    public LocalContext<S, U> getLocalContext() {
      return localContext;
    }

    public List<Edit<U>> getEdits() {
      return edits;
    }

    public S getDocument() {
      return document;
    }

    public long getRemoteVersion() {
      return remoteVersion;
    }
  }
  
  private final List<ProcessedEdit<D, P>> processedEdits = new ArrayList<>();
 
  public List<ProcessedEdit<D, P>> getProcessedEdits() {
    return Collections.unmodifiableList(processedEdits);
  }
}
