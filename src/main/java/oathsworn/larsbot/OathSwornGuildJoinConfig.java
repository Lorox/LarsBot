package oathsworn.larsbot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
final class OathSwornGuildJoinConfig {

    private final long guildId;
    private final String joinMessagePath;

    private final Map<String, OathSwordGuildJoinRoleConfig> rolesConfig;
}
