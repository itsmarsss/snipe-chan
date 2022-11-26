package SnipeBot;

import java.util.ArrayList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonListener extends ListenerAdapter {
    private ButtonClickEvent e;

    public void onButtonClick(ButtonClickEvent event) {
        try {
            if (!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
                return;

            e = event;

            String id = event.getButton().getId();
            if (id.equals("hide")) {
                event.getMessage().delete().queue();
                e.reply("Request successful").setEphemeral(true).queue();
            } else if (id.startsWith("remove-")) {
                if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    e.reply("You do not have `MESSAGE MANAGE` permission.").setEphemeral(true).queue();
                }
                String param = id.replace("remove-", "");

                ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;
                for (int i = 0; i < mi.size(); i++) {
                    if (mi.get(i).getMessage().getId().equals(param)) {
                        mi.remove(i);
                        e.reply("Snipe #" + i + "successfully removed.").setEphemeral(true).queue();
                        break;
                    }
                }
            } else if (id.startsWith("next-")) {

                int param = Integer.parseInt(id.replace("next-", ""));

                ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;

                try {
                    Button prevButton = Button.primary("prev-" + (param - 1), "\u2B05 Prev");
                    Button nextButton = Button.primary("next-" + (param + 1), "Next \u27A1");
                    Button hideButton = Button.secondary("hide", "Hide List");
                    Button removeButton = Button.secondary("remove", "Remove Snipe");
                    Message message = new MessageBuilder()
                            .setEmbeds(new EmbedBuilder(mi.get(param).getEmbed()).setTitle("Snipe #*" + param + "* of *" + (mi.size() - 1) + "*:").build())
                            .setActionRows(ActionRow.of(prevButton, nextButton, hideButton, removeButton))
                            .build();

                    e.deferEdit().queue();
                    e.getMessage().editMessage(message).queue();
                } catch (Exception e) {
                    this.e.reply("No next page.").setEphemeral(true).queue();
                }

            } else if (id.startsWith("prev-")) {

                int param = Integer.parseInt(id.replace("prev-", ""));

                ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;

                try {
                    Button prevButton = Button.primary("prev-" + (param - 1), "\u2B05 Prev");
                    Button nextButton = Button.primary("next-" + (param + 1), "Next \u27A1");
                    Button deleteButton = Button.secondary("delete", "Delete");
                    Message message = new MessageBuilder()
                            .setEmbeds(new EmbedBuilder(mi.get(param).getEmbed()).setTitle("Snipe #*" + param + "* of *" + (mi.size() - 1) + "*:").build())
                            .setActionRows(ActionRow.of(prevButton, nextButton, deleteButton))
                            .build();

                    e.deferEdit().queue();
                    e.getMessage().editMessage(message).queue();
                } catch (Exception e) {
                    this.e.reply("No previous page.").setEphemeral(true).queue();
                }
            }
        } catch (Exception e) {
            this.e.reply("Request unsuccessful *(Hint: Embed possibly removed?)*").setEphemeral(true).queue();
        }
    }

}
