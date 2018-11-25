package oathsworn.larsbot;

import java.util.List;

interface LootListenerItemAdapter {
    LootListenerItem from(
            WowItem wowItem,
            WowCharacterInfo receiver,
            List<Integer> bonusIds,
            String region,
            long receivedTime);
}
