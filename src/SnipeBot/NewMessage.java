package SnipeBot;

import java.util.ArrayList;
import java.util.Collection;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class NewMessage extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String raw = event.getMessage().getContentRaw();

        if (!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
            return;

        if (SnipeChanBot.config.isEnableSnipeCommand() && raw.contains(SnipeChanBot.jda.getSelfUser().getId())) {
            event.getMessage().reply("My prefix is `" + SnipeChanBot.config.getPrefix() + "`, do `" + SnipeChanBot.config.getPrefix() + "sniped <index>` for a sniped message, `" + SnipeChanBot.config.getPrefix() + "snipelist` for a list of snipes, and `" + SnipeChanBot.config.getPrefix() + "snipe` for latest sniped message!").queue();
            return;
        }

        if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "sniped")) {
            try {
                int index = SnipeChanBot.snipedCache.size() - 1 - Integer.parseInt(raw.substring((SnipeChanBot.config.getPrefix() + "sniped").length() + 1));
                if (index > SnipeChanBot.config.getMaxSnipedCache() || index < 0) {
                    event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size() - 1) + "]").queue();
                    return;
                }

                MessageAction ma = event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index).getEmbed());
                if (SnipeChanBot.snipedCache.get(index).getMessage().getAttachments().size() > 0) {
                    Collection<ActionRow> collection = new ArrayList<>();
                    Collection<Button> collection1 = new ArrayList<>();
                    Collection<Button> collection2 = new ArrayList<>();
                    int filecount = 0;
                    for (Attachment i : SnipeChanBot.snipedCache.get(index).getMessage().getAttachments()) {
                        if (filecount < 5) {
                            collection1.add(Button.link(i.getUrl(), i.getFileName()));
                        } else {
                            collection2.add(Button.link(i.getUrl(), i.getFileName()));
                        }
                        filecount++;
                    }
                    ActionRow row1 = ActionRow.of(collection1);
                    collection.add(row1);
                    try {
                        ActionRow row2 = ActionRow.of(collection2);
                        collection.add(row2);
                    } catch (Exception e) {
                    }

                    ma.setActionRows(collection);
                }
                ma.queue();
            } catch (Exception e) {
                event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size() - 1) + "]").queue();
            }
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "snipelist")) {
            int index = SnipeChanBot.snipedCache.size() - 1;
            if (index == -1) {
                event.getMessage().reply("Nothing in sniped cache").queue();
            } else {
                viewSnipe(raw, event);
            }
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "snipe")) {
            int index = SnipeChanBot.snipedCache.size() - 1;
            if (index == -1) {
                event.getMessage().reply("Nothing in sniped cache").queue();
            } else {
                MessageAction ma = event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index).getEmbed());
                if (SnipeChanBot.snipedCache.get(index).getMessage().getAttachments().size() > 0) {
                    Collection<ActionRow> collection = new ArrayList<>();
                    Collection<Button> collection1 = new ArrayList<>();
                    Collection<Button> collection2 = new ArrayList<>();
                    int filecount = 0;
                    for (Attachment i : SnipeChanBot.snipedCache.get(index).getMessage().getAttachments()) {
                        if (filecount < 5) {
                            collection1.add(Button.link(i.getUrl(), i.getFileName()));
                        } else {
                            collection2.add(Button.link(i.getUrl(), i.getFileName()));
                        }
                        filecount++;
                    }
                    ActionRow row1 = ActionRow.of(collection1);
                    collection.add(row1);
                    try {
                        ActionRow row2 = ActionRow.of(collection2);
                        collection.add(row2);
                    } catch (Exception e) {
                    }

                    ma.setActionRows(collection);
                }
                ma.queue();
            }
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "version")) {
            event.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle(SnipeChanBot.version)
                            .setDescription(SnipeChanBot.versionCheck()
                                    .replaceAll("#", "")
                                    .replace("This program is up to date!", "__**This program is up to date!**__")
                                    .replace("[There is a newer version of Snipe Chan]", "__**[There is a newer version of Snipe Chan]**__")
                                    .replace("Author's Note:", "**Author's Note:**")
                                    .replace("New version:", "**New version:**"))
                            .build())
                    .queue();
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "remove")) {
            try {
                int param = 0;
                if (raw.length() > (SnipeChanBot.config.getPrefix() + "remove").length()) {
                    param = Integer.parseInt(raw.substring((SnipeChanBot.config.getPrefix() + "remove").length()).trim());
                }
                SnipeChanBot.snipedCache.remove(param);
                event.getMessage().reply("Snipe index **#" + param + "** successfully removed.").queue();
            } catch (Exception e) {
                event.getMessage().reply("Invalid snipe index.").queue();
            }
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "clearsnipe")) {
            if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().reply("You do not have `MESSAGE MANAGE` permission.").queue();
                return;
            }
            SnipeChanBot.snipedCache.clear();
            event.getMessage().reply("Snipe list has been cleared").queue();
        }


        if (!(SnipeChanBot.config.isSnipeMessageManagers()) && (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)))
            return;

        if (!(SnipeChanBot.config.isSnipeNonhumans()) && (event.getMember().getUser().isBot() || event.getMember().getUser().isSystem()))
            return;

        if (SnipeChanBot.messageCache.size() >= SnipeChanBot.config.getMaxMessageCache())
            SnipeChanBot.messageCache.remove(0);
        SnipeChanBot.messageCache.add(event.getMessage());
    }

    private void viewSnipe(String raw, MessageReceivedEvent event) {
        try {
            ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;
            int param = 0;
            if (raw.length() > (SnipeChanBot.config.getPrefix() + "snipelist").length()) {
                param = Integer.parseInt(raw.substring((SnipeChanBot.config.getPrefix() + "snipelist").length()).trim());
            }

            Button prevButton = Button.primary("prev-" + (param - 1), "\u2B05 Prev");
            Button nextButton = Button.primary("next-" + (param + 1), "Next \u27A1");
            Button hideButton = Button.secondary("hide", "Hide List");
            Button removeButton = Button.danger("remove-" + mi.get(param).getMessage().getId(), "Remove Snipe");
            Message message = new MessageBuilder()
                    .setEmbeds(new EmbedBuilder(mi.get(param).getEmbed()).setTitle("Snipe #*" + param + "* of *" + (mi.size() - 1) + "*:").build())
                    .setActionRows(ActionRow.of(prevButton, nextButton, hideButton, removeButton))
                    .build();

            event.getMessage().reply(message).queue();
        } catch (Exception e) {
            event.getMessage().reply("Invalid page index.").queue();
        }


        //		StringBuilder list = new StringBuilder();
        //		for(MessageInfo mi : SnipeChanBot.snipedCache) {
        //			StringBuilder temp = new StringBuilder();
        //			temp.append("SNIPE LIST");
        //			temp.append("\n--------------------");
        //			MessageEmbed em = mi.getEmbed();
        //			temp.append("\nAuthor: " + em.getAuthor().getName());
        //			temp.append("\nContent:");
        //			temp.append("\n" + em.getDescription());
        //			temp.append("\nInfo:");
        //			temp.append("\n" + em.getFooter().getText());
        //			temp.append("\n============================================================");
        //			list.append(temp);
        //		}
        //		event.getMessage().reply(list.toString().getBytes(), "Snipes.txt").queue();
    }
}
