package oathsworn.larsbot;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

@Log4j2
@AllArgsConstructor(onConstructor=@__(@Inject))
final class GuildRulesAcceptedHandler extends ListenerAdapter {

    private final GuildRulesAcceptedHandlerConfig config;

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getChannel().getIdLong() != config.getChannelId()
                || event.getMessageIdLong() != config.getMessageId()
                || !event.getReactionEmote().getName().equals(config.getReactionEmoji())) {
            return;
        }

        event.getGuild().getController().addRolesToMember(
                event.getMember(),
                event.getGuild().getRoleById(config.getRoleId())).queue();
    }
}
