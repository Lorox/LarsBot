package oathsworn.larsbot;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

final class MessagePartitioningServiceImpl implements  MessagePartitioningService {

    private static final int MAX_MESSAGE_LENGTH = 1950;

    public ImmutableList<String> getMessageParts(String message) {
        return ImmutableList.copyOf(splitMessagePart(message, "\n"));
    }

    private List<String> splitMessagePart(String messagePart, String delimiter) {

        List<String> messageParts = new ArrayList<>();

        String[] stringParts = messagePart.split(delimiter);

        StringBuilder sb = new StringBuilder();
        for (String s : stringParts) {
            String part = s + delimiter;
            if (sb.length() + part.length() > MAX_MESSAGE_LENGTH) {
                if (sb.length() != 0) {
                    messageParts.add(sb.toString());
                    sb = new StringBuilder();
                }
                if (part.length() > MAX_MESSAGE_LENGTH) {
                    if (delimiter.equals("\n")) {
                        messageParts.addAll(splitMessagePart(part, " "));
                    } else {
                        messageParts.addAll(splitWord(part));
                    }
                    continue;
                }
            }
            sb.append(part);
        }
        messageParts.add(sb.toString());

        return messageParts;
    }

    private List<String> splitWord(String messagePart) {

        List<String> messageParts = new ArrayList<>();

        for (int i = 0; i < messagePart.length(); i += MAX_MESSAGE_LENGTH) {
            messageParts.add(messagePart.substring(i, Math.min(MAX_MESSAGE_LENGTH, messagePart.length() - i)));
        }

        return messageParts;
    }
}
