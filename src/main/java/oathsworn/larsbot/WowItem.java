package oathsworn.larsbot;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
final class WowItem {
    private final int id;
    private final String name;
    private final List<BonusStat> bonusStats;
    private final int inventoryType;
    private final int itemLevel;
    private boolean hasSockets;

    @Value
    @Builder
    public static class BonusStat {
        private final int stat;
        private final int amount;
    }
}
