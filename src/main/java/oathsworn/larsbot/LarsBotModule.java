package oathsworn.larsbot;

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
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
final class LarsBotModule extends AbstractModule {

    private static final String GUILD_RULES_ACCEPTED_HANDLER_CONFIG_PATH = "RulesAcceptedHandlerConfig.json";

    private final TokenRetriever tokenRetriever;
    private final ExecutorService messageSendingExecutor;

    public LarsBotModule(TokenRetriever tokenRetriever) {
        this.tokenRetriever = tokenRetriever;
        messageSendingExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void configure() {
        bind(MessagePartitioningService.class).to(MessagePartitioningServiceImpl.class);
        bind(ExecutorService.class)
                .annotatedWith(MessageSendingExecutor.class)
                .toInstance(messageSendingExecutor);
        bind(MessageSendingService.class).to(MessageSendingServiceImpl.class);
    }

    @Provides
    private JDA provideJda() {
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

    @Provides GuildRulesAcceptedHandlerConfig providesGuildRulesAcceptedHandlerConfig() {
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
}
