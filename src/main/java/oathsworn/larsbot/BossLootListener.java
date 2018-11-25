package oathsworn.larsbot;

import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import org.apache.logging.log4j.Level;

import java.util.concurrent.ExecutorService;

@Log4j2
final class BossLootListener {

    private static final int WAIT_TIME_MILLIS = 5000;

    private final BossLootListenerConfig config;
    private final BlizzardClient blizzardClient;
    private final JDA jda;
    private final LootListenerItemAdapter lootListenerItemAdapter;
    private final ExecutorService executorService;

    private boolean running;
    private long lastUpdateTime;

    @Inject
    public BossLootListener(
            BossLootListenerConfig config,
            BlizzardClient blizzardClient,
            JDA jda,
            LootListenerItemAdapter lootListenerItemAdapter,
            @BossLootListenerExecutor
                    ExecutorService executorService) {
        this.config = config;
        this.blizzardClient = blizzardClient;
        this.jda = jda;
        this.lootListenerItemAdapter = lootListenerItemAdapter;
        this.executorService = executorService;
    }

    public void start() {
        running = true;
        lastUpdateTime = System.currentTimeMillis();
        executorService.submit(this::listenLoop);
    }

    public void stop() {
        running = false;
    }

    private void listenLoop() {

        log.log(Level.INFO, "Starting boss loot listener...");
        while (running) {

            try {
                Thread.sleep(WAIT_TIME_MILLIS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            GuildNews guildNews =
                    blizzardClient.getGuildNews(
                            config.getGuildRegion(),
                            config.getGuildRealm(),
                            config.getGuildName());

            if (guildNews == null) {
                continue;
            }

            if (guildNews.getLastModified() <= lastUpdateTime) {
                continue;
            }

            for (GuildNews.News news : guildNews.getNews()) {
                if (news.getTimestamp() <= lastUpdateTime) {
                    break;
                }

                if (!news.getType().equals("itemLoot")
                        || !(news.getContext().equals("raid-normal")
                        || news.getContext().equals("raid-heroic")
                        || news.getContext().equals("raid-mythic"))) {
                    continue;
                }

                WowItem wowItem =
                        blizzardClient.getWowItem(
                                config.getGuildRegion(),
                                news.getItemId(),
                                news.getBonusLists());
                WowCharacterInfo receiverInfo =
                        blizzardClient.getWowCharacterInfo(
                                config.getGuildRegion(),
                                config.getGuildRealm(),
                                news.getCharacter());

                if (wowItem == null || receiverInfo == null || wowItem.getItemLevel() < config.getMinimumItemLevel()) {
                    continue;
                }

                LootListenerItem lootListenerItem =
                        lootListenerItemAdapter.from(
                                wowItem,
                                receiverInfo,
                                news.getBonusLists(),
                                config.getGuildRegion(),
                                news.getTimestamp());

                log.log(Level.INFO, "Sending loot update...");
                jda.getTextChannelById(config.getMessageChannelId())
                        .sendMessage(lootListenerItem.getEmbed())
                        .queue();
            }
            lastUpdateTime = guildNews.getLastModified();
        }
        log.log(Level.INFO, "Boss loot listener shutting down.");
    }
}
