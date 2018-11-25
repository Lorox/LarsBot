package oathsworn.larsbot;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
final class LarsBotModule extends AbstractModule {

    private static final String GUILD_RULES_ACCEPTED_HANDLER_CONFIG_PATH = "RulesAcceptedHandlerConfig.json";
    private static final String BOSS_LOOT_LISTENER_CONFIG_PATH = "BossLootListenerConfig.json";
    private static final String ITEM_SLOT_IDS_PATH = "ItemSlotIds.json";
    private static final String ITEM_STAT_IDS_PATH = "ItemStatIds.json";

    private final TokenRetriever tokenRetriever;
    private final ExecutorService messageSendingExecutor;
    private final ExecutorService bossLootListenerExecutor;

    private final JDA jda;

    public LarsBotModule(TokenRetriever tokenRetriever) {
        this.tokenRetriever = tokenRetriever;
        messageSendingExecutor = Executors.newSingleThreadExecutor();
        bossLootListenerExecutor = Executors.newSingleThreadExecutor();

        jda = initalizeJDA();
    }

    @Override
    protected void configure() {
        bind(MessagePartitioningService.class).to(MessagePartitioningServiceImpl.class);
        bind(ExecutorService.class)
                .annotatedWith(MessageSendingExecutor.class)
                .toInstance(messageSendingExecutor);
        bind(ExecutorService.class)
                .annotatedWith(BossLootListenerExecutor.class)
                .toInstance(bossLootListenerExecutor);
        bind(MessageSendingService.class).to(MessageSendingServiceImpl.class);
        bind(JDA.class).toInstance(jda);
    }

    private JDA initalizeJDA() {
        try {

            JDA jda = new JDABuilder(tokenRetriever.getBotToken())
                    .build();

            jda.awaitReady();

            return jda;
        } catch (InterruptedException | LoginException e) {
            log.log(Level.ERROR, e);
            throw new RuntimeException(e);
        }
    }

    @Provides
    private GuildRulesAcceptedHandlerConfig providesGuildRulesAcceptedHandlerConfig() {
        try {
            return new GsonBuilder().create()
                            .fromJson(
                                    IOUtils.toString(
                                            this.getClass().getClassLoader()
                                                    .getResourceAsStream(GUILD_RULES_ACCEPTED_HANDLER_CONFIG_PATH),
                                            Charset.defaultCharset()),
                                    GuildRulesAcceptedHandlerConfig.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    private LootListenerItemAdapter providesLootListenerItemAdapter() {
        Type itemSlotMappingType = new TypeToken<Map<Integer, ItemSlotNameEntry>>(){}.getType();
        Map<Integer, ItemSlotNameEntry> itemSlotMapping;
        Type itemStatMappingType = new TypeToken<Map<Integer, List<String>>>(){}.getType();
        Map<Integer, List<String>> itemStatMapping;

        try {
            itemSlotMapping = new GsonBuilder().create()
                    .fromJson(
                            IOUtils.toString(
                                    this.getClass().getClassLoader()
                                            .getResourceAsStream(ITEM_SLOT_IDS_PATH),
                                    Charset.defaultCharset()),
                            itemSlotMappingType);

            itemStatMapping = new GsonBuilder().create()
                    .fromJson(
                            IOUtils.toString(
                                    this.getClass().getClassLoader()
                                            .getResourceAsStream(ITEM_STAT_IDS_PATH),
                                    Charset.defaultCharset()),
                            itemStatMappingType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new LootListenerItemAdapterImpl(
                ImmutableMap.copyOf(itemSlotMapping),
                ImmutableMap.copyOf(itemStatMapping),
                new WowClassColor());
    }

    @Provides
    private BlizzardClient providesBlizzardClient() {
        return new BlizzardClientImpl(tokenRetriever.getBlizzardClientId(), tokenRetriever.getBlizzardClientSecret());
    }

    @Provides
    private BossLootListenerConfig providesBossLootListenerConfig() {
        try {
            return new GsonBuilder().create()
                    .fromJson(
                            IOUtils.toString(
                                    this.getClass().getClassLoader()
                                            .getResourceAsStream(BOSS_LOOT_LISTENER_CONFIG_PATH),
                                    Charset.defaultCharset()),
                            BossLootListenerConfig.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
