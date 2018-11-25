package oathsworn.larsbot;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
final class WowCharacterInfo {
    private final String name;
    private final String thumbnail;
    @SerializedName("class")
    private final int classId;
}
