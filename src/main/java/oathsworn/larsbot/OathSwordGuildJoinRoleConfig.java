package oathsworn.larsbot;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
final class OathSwordGuildJoinRoleConfig {
    private final long roleId;
    private final String roleRulesPath;
}
