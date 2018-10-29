package oathsworn.larsbot;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

final class MessageSendingServiceImpl implements MessageSendingService {

    private final MessagePartitioningService messagePartitioner;
    private final ExecutorService executorService;

    @Inject
    public MessageSendingServiceImpl(
            MessagePartitioningService messagePartitioner,
            @MessageSendingExecutor
            ExecutorService executorService) {
        this.messagePartitioner = messagePartitioner;
        this.executorService = executorService;
    }

    @Override
    public Future sendMessage(String message, MessageChannel channel) {

        return executorService.submit(new SendMessageTask(message, channel));
    }

    @Value
    @AllArgsConstructor
    final class SendMessageTask implements Runnable {

        private final String message;
        private final MessageChannel channel;

        public void run() {

            List<String> parts = messagePartitioner.getMessageParts(message);

            for (String part : parts) {
                channel.sendMessage(part).complete();
            }
        }
    }
}