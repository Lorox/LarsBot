package oathsworn.larsbot;

import lombok.Value;
import java.util.List;

@Value
final class GuildNews {

    private final long lastModified;
    private final String name;
    private final String realm;
    private final List<News> news = null;

    @Value
    final static class News {

        private final String type;
        private final String character;
        private final long timestamp;
        private final int itemId;
        private final String context;
        private final List<Integer> bonusLists = null;
    }
}