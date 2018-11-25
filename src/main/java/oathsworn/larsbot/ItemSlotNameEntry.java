package oathsworn.larsbot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
final class ItemSlotNameEntry {
    private final String slotName;
    private final String simcSlotName;
}
