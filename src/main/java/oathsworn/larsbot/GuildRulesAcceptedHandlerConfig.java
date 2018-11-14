package oathsworn.larsbot;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
final class GuildRulesAcceptedHandlerConfig {

    private final long channelId;
    private final long messageId;
    private final String reactionEmoji;
    private final long roleId;
}
