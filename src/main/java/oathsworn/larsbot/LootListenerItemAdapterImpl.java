package oathsworn.larsbot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(onConstructor=@__(@Inject))
final class LootListenerItemAdapterImpl implements LootListenerItemAdapter {

    private static final String THUMBNAIL_URL_PREFIX = "https://render-%s.worldofwarcraft.com/character/";

    private final ImmutableMap<Integer, ItemSlotNameEntry> itemSlotMapping;
    private final ImmutableMap<Integer, List<String>> itemStatMapping;
    private final WowClassColor classColorIdMapping;

    @Override
    public LootListenerItem from(
            WowItem wowItem,
            WowCharacterInfo receiver,
            List<Integer> bonusIds,
            String region,
            long receivedTime) {

        return LootListenerItem.builder()
                .id(wowItem.getId())
                .name(wowItem.getName())
                .receiver(from(receiver, region))
                .receivedTime(receivedTime)
                .slotName(itemSlotMapping.get(wowItem.getInventoryType()).getSlotName())
                .simcSlotName(itemSlotMapping.get(wowItem.getInventoryType()).getSimcSlotName())
                .bonusIds(ImmutableList.copyOf(bonusIds))
                .itemLevel(wowItem.getItemLevel())
                .stats(getStats(wowItem.getBonusStats()))
                .socketed(wowItem.isHasSockets())
                .build();
    }

    private ImmutableMap<String, Integer> getStats(List<WowItem.BonusStat> bonusStats) {

        Map<String, Integer> stats = new HashMap<>();
        for (WowItem.BonusStat bonusStat : bonusStats) {
            if (itemStatMapping.containsKey(bonusStat.getStat())) {
                itemStatMapping.get(bonusStat.getStat()).forEach(s -> stats.put(s, bonusStat.getAmount()));
            }
        }
        return ImmutableMap.copyOf(stats);
    }

    private LootListenerItemReceiver from(WowCharacterInfo characterInfo, String region) {

        return LootListenerItemReceiver.builder()
                .name(characterInfo.getName())
                .classColor(classColorIdMapping.getClassColor(characterInfo.getClassId()))
                .thumbnailUrl(String.format(THUMBNAIL_URL_PREFIX, region) + characterInfo.getThumbnail())
                .build();
    }
}
