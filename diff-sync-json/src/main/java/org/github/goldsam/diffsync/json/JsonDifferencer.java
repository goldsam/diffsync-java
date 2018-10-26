package org.github.goldsam.diffsync.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.CompatibilityFlags;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import java.util.EnumSet;
import org.github.goldsam.diffsync.core.Differencer;

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
  public JsonNode Difference(JsonNode sourceDocument, JsonNode targetDocument) {
    return JsonDiff.asJson(sourceDocument, targetDocument, diffFlags);
  }

  @Override
  public JsonNode Patch(JsonNode soureDocument, JsonNode patch) {
    return JsonPatch.apply(patch, soureDocument, patchFlags);
  }
}
