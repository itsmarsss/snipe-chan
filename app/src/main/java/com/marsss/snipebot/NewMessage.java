package com.marsss.snipebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.Collection;

public class NewMessage extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String raw = event.getMessage().getContentRaw();

        if (!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
            return;

        if (SnipeChanBot.config.isEnableSnipeCommand() && raw.contains(SnipeChanBot.jda.getSelfUser().getId())) {
            event.getMessage().reply("My prefix is `" + SnipeChanBot.config.getPrefix() + "`, do `" + SnipeChanBot.config.getPrefix() + "help` for a list of my commands!").queue();
            return;
        }

        if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "sniped")) {
            int index;
            try {
                index = SnipeChanBot.snipedCache.size() - 1 - Integer.parseInt(raw.substring((SnipeChanBot.config.getPrefix() + "sniped").length() + 1));
            } catch (Exception e) {
                event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size() - 1) + "]").queue();
                return;
            }
            if (index >= SnipeChanBot.snipedCache.size() || index < 0) {
                event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size() - 1) + "]").queue();
                return;
            }

            MessageCreateAction ma = event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index).getEmbed());
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

                if (collection2.size() != 0) {
                    ActionRow row2 = ActionRow.of(collection2);
                    collection.add(row2);
                }

                ma = ma.setComponents(collection);
            }
            ma.queue();
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
                MessageCreateAction ma = event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index).getEmbed());
                if (SnipeChanBot.snipedCache.get(index).getMessage().getAttachments().size() > 0) {
                    Collection<ActionRow> collection = new ArrayList<>();
                    Collection<Button> collection1 = new ArrayList<>();
                    Collection<Button> collection2 = new ArrayList<>();
                    int filecount = 0;
                    for (Attachment i : SnipeChanBot.snipedCache.get(index).getMessage().getAttachments()) {
                        String name = (i.getFileName().length() > 80 ? i.getFileName().substring(0, 77) + "..." : i.getFileName());
                        String link = i.getUrl();
                        if (link.length() > 512) {
                            link = "https://www.generatormix.com/random-gif-generator?safe=on";
                            name = "[Link too long]";
                        }
                        if (filecount < 5) {
                            collection1.add(Button.link(link, name));
                        } else {
                            collection2.add(Button.link(link, name));
                        }
                        filecount++;
                    }
                    ActionRow row1 = ActionRow.of(collection1);
                    collection.add(row1);

                    if (collection2.size() != 0) {
                        ActionRow row2 = ActionRow.of(collection2);
                        collection.add(row2);
                    }

                    ma = ma.setComponents(collection);
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
            if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().reply("You do not have `MESSAGE MANAGE` permission.").queue();
                return;
            }
            int index = -1;
            if (raw.length() > (SnipeChanBot.config.getPrefix() + "remove").length()) {
                try {
                    index = Integer.parseInt(raw.substring((SnipeChanBot.config.getPrefix() + "remove").length()).trim());
                } catch (Exception e) {
                    event.getMessage().reply("Invalid snipe index.").queue();
                    return;
                }
            }
            if (index >= SnipeChanBot.snipedCache.size() || index < 0) {
                event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size() - 1) + "]").queue();
                return;
            }
            SnipeChanBot.snipedCache.remove(index);
            event.getMessage().reply("Snipe index **#" + index + "** successfully removed.").queue();
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "clear")) {
            if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().reply("You do not have `MESSAGE MANAGE` permission.").queue();
                return;
            }
            SnipeChanBot.snipedCache.clear();
            event.getMessage().reply("Snipe list has been cleared.").queue();
        } else if (raw.toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "help")) {
            StringBuilder help = new StringBuilder();
            help.append("**__Commands__**")
                    .append("\n\n**Main *(Some may be disabled)***")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "help` - this menu")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "version` - check for newer versions")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "snipe` - shows the latest snipe")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "sniped` - shows a snipe in sniped cache")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "snipelist [index | nothing]` - shows interactive snipe list")
                    .append("\n\t`Prev` - browse previous snipe")
                    .append("\n\t`Next` - browse next snipe")
                    .append("\n\t`Hide List` - hide embed")
                    .append("\n\t`Remove Snipe` - remove from cache")
                    .append("\n\n**Message Manage Permission**")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "remove [index]` - removes index from cache")
                    .append("\n`" + SnipeChanBot.config.getPrefix() + "clear` - clears cache");

            event.getMessage().reply(help.toString()).queue();
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
        ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;
        int param = mi.size() - 1;
        if (raw.length() > (SnipeChanBot.config.getPrefix() + "snipelist").length()) {
            try {
                param = Integer.parseInt(raw.substring((SnipeChanBot.config.getPrefix() + "snipelist").length()).trim());
            } catch (Exception e) {
                event.getMessage().reply("Invalid page index.").queue();
            }
        }

        if (param < 0 || param >= mi.size()) {
            event.getMessage().reply("Invalid page index.").queue();
            return;
        }

        Button prevButton = Button.primary("prev-" + (param - 1), "\u2B05 Prev");
        Button nextButton = Button.primary("next-" + (param + 1), "Next \u27A1");
        Button hideButton = Button.secondary("hide", "Hide List");
        Button removeButton = Button.danger("remove-" + mi.get(param).getMessage().getId(), "Remove Snipe");
        MessageCreateData message = new MessageCreateBuilder()
                .setEmbeds(new EmbedBuilder(mi.get(param).getEmbed()).setTitle("Snipe #*" + param + "* of *" + (mi.size() - 1) + "*:").build())
                .setComponents(ActionRow.of(prevButton, nextButton, hideButton, removeButton))
                .build();

        event.getMessage().
                reply(message).
                queue();

    }
}
