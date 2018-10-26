package org.github.goldsam.diffsync.core;

public class Edit<P> {
  
  private final P patch;
  private final long version;
  private final int authorId;
  
  public Edit(P patch, long version, int authorId) {
    this.patch = patch;
    this.version = version;
    this.authorId = authorId;
  }

  public P getPatch() {
    return patch;
  }

  public long getVersion() {
    return version;
  }

  public int getAuthorId() {
    return authorId;
  }
}

