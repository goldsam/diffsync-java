package org.github.goldsam.diffsync.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MockLocalContextListener<D, P> implements LocalContextListener<D, P> {
  
  public static class Update<T> {
    
    private final List<T> patches;
    private final long newestPatchSourceVersion;
    private final long lastReceviedVersion;
      
    public Update(Collection<T> patches, long newestPatchSourceVersion, long lastReceviedVersion) {
      this.patches = new ArrayList<>(patches);
      this.newestPatchSourceVersion = newestPatchSourceVersion;
      this.lastReceviedVersion = lastReceviedVersion;
    }

    public List<T> getPatches() {
      return patches;
    }

    public long getNewestPatchSourceVersion() {
      return newestPatchSourceVersion;
    }

    public long getLastReceviedVersion() {
      return lastReceviedVersion;
    }
  }
  
  private final List<Update<P>> updates = new ArrayList<>();
  private D initializedDocument;
  
  @Override
  public void onDocumentInitialize(LocalContext<D, P> source, D document) {
    initializedDocument = document;
  }
  
  @Override
  public void onDocumentUpdated(LocalContext<D, P> source, Collection<P> patches, long newestPatchSourceVersion, long lastReceviedVersion) {
    updates.add(new Update<>(patches, newestPatchSourceVersion, lastReceviedVersion));
  }  
  
  public List<Update<P>> getUpdates() {
    return Collections.unmodifiableList(updates);
  }

  public D getInitializedDocument() {
    return initializedDocument;
  }
}
