package org.github.goldsam.diffsync.edit;

import java.util.Objects;

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

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + Objects.hashCode(this.patch);
    hash = 23 * hash + (int) (this.version ^ (this.version >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ImmutableEdit<?> other = (ImmutableEdit<?>) obj;
    return Objects.equals(this.patch, other.patch);
  }

  @Override
  public String toString() {
    return "ImmutableEdit{" + "patch=" + patch + ", version=" + version + '}';
  } 
}
