package oathsworn.larsbot;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.charset.Charset;

@Log4j2
final class OathSwordGuildJoinHandler extends ListenerAdapter {

    private static final String JOIN_CONFIG_PATH = "JoinConfig.json";
    private static final String ROLE_COMMAND_PREFIX = "!";

    private final MessageSendingService messageSendingService;
    private final OathSwornGuildJoinConfig joinConfig;

    @Inject
    public OathSwordGuildJoinHandler(
            JDA jda,
            MessageSendingService messageSendingService) {

        this.messageSendingService = messageSendingService;
        try {
            joinConfig =
                    new GsonBuilder().create()
                            .fromJson(
                                    IOUtils.toString(
                                            this.getClass().getClassLoader()
                                                    .getResourceAsStream(JOIN_CONFIG_PATH),
                                            Charset.defaultCharset()),
                                    OathSwornGuildJoinConfig.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        jda.addEventListener(this);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() != joinConfig.getGuildId() || event.getUser().isBot()) {
            return;
        }

        try {
            StringBuilder joinMessage = new StringBuilder();

            joinMessage.append(
                    IOUtils.toString(
                            this.getClass().getClassLoader()
                                    .getResourceAsStream(joinConfig.getJoinMessagePath()),
                            Charset.defaultCharset()));

            joinConfig.getRolesConfig().keySet().forEach(
                    role -> joinMessage.append(ROLE_COMMAND_PREFIX).append(role).append("\n"));

            event.getMember().getUser().openPrivateChannel()
                    .queue(channel -> messageSendingService.sendMessage(joinMessage.toString(), channel));
        } catch (IOException e) {
            log.log(Level.ERROR, e);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.getMessage().getContentDisplay().startsWith(ROLE_COMMAND_PREFIX)) {
            return;
        }

        String roleCommand = event.getMessage().getContentDisplay().substring(1);

        if (!joinConfig.getRolesConfig().containsKey(roleCommand)) {
            messageSendingService.sendMessage("Sorry, that is not a valid selection.", event.getChannel());
            return;
        }

        try {

            // Send role specific message
            messageSendingService.sendMessage(
                    IOUtils.toString(
                            this.getClass().getClassLoader()
                                    .getResourceAsStream(
                                            joinConfig.getRolesConfig().get(roleCommand)
                                                    .getRoleRulesPath()),
                            Charset.defaultCharset()),
                    event.getChannel());
        } catch (IOException e) {
            messageSendingService.sendMessage(
                    "Failed to load role information, please contact an officer",
                    event.getChannel());
            log.log(Level.ERROR, e);
            return;
        }

        // Assign relevant role
        Guild guild = event.getJDA().getGuildById(joinConfig.getGuildId());
        guild.getController().addRolesToMember(
                guild.getMemberById(event.getAuthor().getId()),
                guild.getRoleById(joinConfig.getRolesConfig().get(roleCommand).getRoleId()))
                .queue();
    }
}
