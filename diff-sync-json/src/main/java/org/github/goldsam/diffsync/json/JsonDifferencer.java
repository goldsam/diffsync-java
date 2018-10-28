package org.github.goldsam.diffsync.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.CompatibilityFlags;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import java.util.EnumSet;
import org.github.goldsam.diffsync.core.Differencer;
import org.github.goldsam.diffsync.core.PatchFailedException;

public class JsonDifferencer implements Differencer<JsonNode, JsonNode>{
  
  private final EnumSet<DiffFlags> diffFlags;
  private final EnumSet<CompatibilityFlags> patchFlags;
  
  public JsonDifferencer(EnumSet<DiffFlags> diffFlags, EnumSet<CompatibilityFlags> patchFlags) {
    this.diffFlags = diffFlags;
    this.patchFlags = patchFlags;
  }
  
  public JsonDifferencer() {
    this(DiffFlags.defaults(), CompatibilityFlags.defaults());
  }

  @Override
  public JsonNode difference(JsonNode sourceDocument, JsonNode destDocument) {
    return JsonDiff.asJson(sourceDocument, destDocument, diffFlags);
  }

  @Override
  public JsonNode patch(JsonNode sourceDocument, JsonNode patch, boolean fuzzy) throws PatchFailedException {
    try {
      return JsonPatch.apply(patch, sourceDocument, patchFlags);
    } catch(JsonPatchApplicationException e) {
      throw new PatchFailedException("Unable to apply JSON patch", e);
    }
  }
}
