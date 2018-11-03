package org.github.goldsam.diffsync.core.edit;

public class ImmutableEdit<P> implements Edit<P> {

  private final P patch;
  private final long version;
  
  public ImmutableEdit(P patch, long version) {
    this.patch = patch;
    this.version = version;
  }
  
  @Override
  public P getPatch() {
    return patch;
  }

  @Override
  public long getVersion() {
    return version;
  }
}
