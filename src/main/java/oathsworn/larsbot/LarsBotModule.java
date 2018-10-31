package oathsworn.larsbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.apache.logging.log4j.Level;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
final class LarsBotModule extends AbstractModule {

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

            JDA jda = new JDABuilder(new TokenRetriever().getBotToken())
                    .build();

            jda.awaitReady();

            return jda;
        } catch (InterruptedException | LoginException e) {
            log.log(Level.ERROR, e);
            throw new RuntimeException(e);
        }
    }
}
