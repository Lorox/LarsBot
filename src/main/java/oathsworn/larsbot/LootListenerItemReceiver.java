package oathsworn.larsbot;

import lombok.Builder;
import lombok.Value;

import java.awt.Color;

@Value
@Builder
final class LootListenerItemReceiver {
    private final String name;
    private final Color classColor;
    private final String thumbnailUrl;
}
