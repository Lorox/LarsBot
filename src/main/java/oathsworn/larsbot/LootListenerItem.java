package oathsworn.larsbot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Value;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.Date;

@Value
@Builder
final class LootListenerItem {
    private final int id;
    private final String name;
    private final LootListenerItemReceiver receiver;
    private final long receivedTime;
    private final String slotName;
    private final String simcSlotName;
    private final ImmutableList<Integer> bonusIds;

    private final int itemLevel;
    private final ImmutableMap<String, Integer> stats;
    private final boolean socketed;

    public MessageEmbed getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(receiver.getName() + " received item: " + name);
        eb.setColor(receiver.getClassColor());
        eb.setThumbnail(receiver.getThumbnailUrl());

        eb.addField(getStatsField());
        eb.addField(getSimStringField());
        eb.addField(getWowheadUrlField());

        eb.setFooter(new Date(receivedTime).toString(), null);

        return eb.build();
    }

    private MessageEmbed.Field getStatsField() {

        String title = slotName + ": Item level " + itemLevel;
        StringBuilder statString = new StringBuilder();

        stats.entrySet().forEach(
                entry ->
                        statString.append(entry.getKey() + ": " + entry.getValue() + "\n"));
        if (socketed) {
            statString.append("Socket\n");
        }
        statString.append("\n");
        return new MessageEmbed.Field(title, statString.toString(), false);
    }

    private MessageEmbed.Field getSimStringField() {
        StringBuilder simcString = new StringBuilder();
        simcString.append(simcSlotName + "=,id=" + id + ",bonusid=");
        bonusIds.forEach(i -> simcString.append(i + "/"));
        return new MessageEmbed.Field("Sim String: ", simcString.toString(), false);
    }

    private MessageEmbed.Field getWowheadUrlField() {
        StringBuilder wowheadUrl = new StringBuilder();
        wowheadUrl.append("https://www.wowhead.com/item=" + id + "/&bonus=");
        bonusIds.forEach(i -> wowheadUrl.append(":" + i));
        return new MessageEmbed.Field("Wowhead url: ", wowheadUrl.toString(), false);
    }
}
