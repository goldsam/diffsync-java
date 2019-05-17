package org.github.goldsam.diffsync.edit;

/**
 * Possible results while applying an {@link Edit} to a synchronized document.
 */
public enum EditProcessingResult {
  
  /**
   * Indicates an edit normally was applied without collision.
   */
  APPLIED_NO_COLLISION(true, false),
  
  /**
   * Indicates an edit was applied, but with collisions.
   */
  APPLIED_COLLISION(true, true),
  
  /**
   * Indicates an edit was previously applied with a given version number.
   */
  DISCARDED_ALREADY_APPLIED(false, false),
  
  /**
   * Indicates an edit was received out of order (i.e. "from the future").
   */
  DISCARDED_OUT_OF_ORDER(false, false),
  
  /**
   * Indicates an edit was discarded because the acknowledged local version 
   * number does not match the current local shadow version.
   */
  DISCARDED_ACK_VERSION_MISMATCH(false, false),
  
  /**
   * Indicates an edit was discarded due collision with competing edits.
   */
  DISCARDED_DUE_TO_COLLISION(false, true);
  
  private final boolean applied;
  private final boolean collision;
  
  
  private EditProcessingResult(boolean applied, boolean collision) {
    this.applied = applied;
    this.collision = collision;
  }
  
  public boolean isApplied() {
    return applied;
  }
  
  public boolean isCollision() {
    return collision;
  }
}
