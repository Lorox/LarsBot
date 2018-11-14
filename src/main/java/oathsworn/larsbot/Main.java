package oathsworn.larsbot;


import com.google.inject.Guice;
import com.google.inject.Injector;
import net.dv8tion.jda.core.JDA;

public final class Main {

    public static void main(String[] args) {
       Injector injector =
               Guice.createInjector(new LarsBotModule(new TokenRetrieverProdImpl()));
       JDA jda = injector.getInstance(JDA.class);
       jda.addEventListener(injector.getInstance(GuildRulesAcceptedHandler.class));
    }
}
