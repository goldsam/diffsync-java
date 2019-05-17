package org.github.goldsam.diffsync.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.CompatibilityFlags;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import java.util.EnumSet;
import org.github.goldsam.diffsync.diffpatch.DiffPatch;
import org.github.goldsam.diffsync.diffpatch.PatchFailedException;
import org.github.goldsam.diffsync.diffpatch.PatchOptions;

public class JsonDiffPatch implements DiffPatch<JsonNode, JsonNode> {

    private final EnumSet<DiffFlags> diffFlags;
    private final EnumSet<CompatibilityFlags> patchFlags;

    public JsonDiffPatch(EnumSet<DiffFlags> diffFlags, EnumSet<CompatibilityFlags> patchFlags) {
        this.diffFlags = diffFlags;
        this.patchFlags = patchFlags;
    }

    public JsonDiffPatch() {
        this(DiffFlags.defaults(), CompatibilityFlags.defaults());
    }

    @Override
    public JsonNode diff(JsonNode source, JsonNode target) {
        return JsonDiff.asJson(source, target, diffFlags);
    }

    @Override
    public JsonNode patch(JsonNode source, JsonNode patch, PatchOptions options) throws PatchFailedException {
//        try {
//            return JsonPatch.apply(patch, source, patchFlags);
//        } catch(JsonPatchApplicationException e) {
//            throw new PatchFailedException("Unable to apply JSON patch", e);
//        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
