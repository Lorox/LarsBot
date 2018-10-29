package oathsworn.larsbot;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.util.Providers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.utils.IOUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
final class LarsBotModule extends AbstractModule {

    private static final String TOKEN_PATH = "tokens/botToken";

    private final ExecutorService messageSendingExecutor;

    public LarsBotModule() {
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
            String token = IOUtils.toString(
                    this.getClass().getClassLoader()
                            .getResourceAsStream(TOKEN_PATH),
                    Charset.defaultCharset());

            JDA jda = new JDABuilder(token)
                    .build();

            jda.awaitReady();

            return jda;
        } catch (InterruptedException | LoginException | IOException e) {
            log.log(Level.ERROR, e);
            throw new RuntimeException(e);
        }
    }
}
