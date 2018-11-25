package oathsworn.larsbot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
final class BossLootListenerConfig {
    private final String guildRegion;
    private final String guildRealm;
    private final String guildName;
    private final long messageChannelId;
    private final int minimumItemLevel;
}
