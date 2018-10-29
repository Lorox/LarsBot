package oathsworn.larsbot;


import com.google.inject.Guice;
import com.google.inject.Injector;

public final class Main {

    public static void main(String[] args) {
       Injector injector =
               Guice.createInjector(new LarsBotModule());
       injector.getInstance(OathSwordGuildJoinHandler.class);
    }
}
