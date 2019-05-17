package org.github.goldsam.diffsync.diffpatch;

/**
 * Options controlling how document patch operations are applied.
 * 
 * @see DiffPatch#patch(java.lang.Object, java.lang.Object, org.github.goldsam.diffsync.diffpatch.PatchOptions) 
 */
public class PatchOptions {
  
  private final boolean fuzzy;
  
  PatchOptions(boolean fuzzy) {
    this.fuzzy = fuzzy;
  }

  /**
   * Returns a boolean indicating if patch operation are fuzzy.
   * @return {@code true} if patch operations are fuzzy; {@code false} if they are strict.
   */
  public boolean isFuzzy() {
    return fuzzy;
  }

  /**
   * Contains static creation methods for {@link PatchOptions}.
   */
  public static class Builder {
    
    private static final PatchOptions FUZZY_PATCH_OPTIONS = new PatchOptions(true);
    
    private static final PatchOptions STRICT_PATCH_OPTIONS = new PatchOptions(false);

    /**
     * Returns {@link PatchOptions} that specify fuzzy patching.
     * @return {@link PatchOptions} that specify fuzzy patching.
     */
    public static PatchOptions withFuzzy() {
      return FUZZY_PATCH_OPTIONS;
    }

    /**
     * Returns {@link PatchOptions} that specify strict patching.
     * @return {@link PatchOptions} that specify strict patching.
     */
    public static PatchOptions withStrict() {
      return STRICT_PATCH_OPTIONS;
    }
  }
}
