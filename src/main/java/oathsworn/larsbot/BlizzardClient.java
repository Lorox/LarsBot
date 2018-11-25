package oathsworn.larsbot;

import java.util.List;

interface BlizzardClient {
    GuildNews getGuildNews(String guildRegion, String guildRealm, String guildName);
    WowItem getWowItem(String region, int itemId, List<Integer> bonusLists);
    WowCharacterInfo getWowCharacterInfo(String region, String realm, String characterName);
}
