package oathsworn.larsbot;

import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.concurrent.Future;

public interface MessageSendingService {

    public Future sendMessage(String message, MessageChannel channel);
}
