package com.marsss.snipebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class DeletedMessage extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
            return;

        String messageID = event.getMessageId();
        int messageIndex = -1;
        for (int i = 0; i < SnipeChanBot.messageCache.size(); i++) {
            if (SnipeChanBot.messageCache.get(i).getId().equals(messageID)) {
                messageIndex = i;
                break;
            }
        }
        if (messageIndex == -1)
            return;

        boolean addButton = false;

        Message originalMessage = SnipeChanBot.messageCache.get(messageIndex);
        SnipeChanBot.messageCache.remove(messageIndex);

        Date date = new Date();

        EmbedBuilder emb = new EmbedBuilder()
                .setAuthor(originalMessage.getMember().getUser().getAsTag(), null, originalMessage.getMember().getUser().getAvatarUrl())
                .setDescription(originalMessage.getMember().getAsMention() + "'s message has been deleted in channel " + originalMessage.getChannel().getAsMention())
                .appendDescription("\n[[jump to  message](" + originalMessage.getJumpUrl() + ")]")
                .setFooter(
                        "\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014" +
                                "\nMessage Sent \u2022 " + originalMessage.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME).substring(5) +
                                "\nMessage Deleted \u2022 " + date.toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME).substring(5));
        if (SnipeChanBot.config.isSnipeDeletedFiles() && SnipeChanBot.config.isSnipeDeletedMessages()) {
            if (!originalMessage.getContentRaw().isBlank()) {
                String msg = originalMessage.getContentRaw();
                if (msg.length() >= 1024)
                    msg = msg.substring(0, 1021) + "...";
                emb.addField("**Message Deleted:**", msg, true);
            }
            if (originalMessage.getAttachments().size() > 0) {
                emb.addField("**Other:**", originalMessage.getAttachments().size() + " attachment(s)", false);
                addButton = true;
            }
            if (SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
                SnipeChanBot.snipedCache.remove(0);
        } else if (SnipeChanBot.config.isSnipeDeletedFiles()) {
            if (originalMessage.getAttachments().size() > 0) {
                emb.addField("**Other:**", originalMessage.getAttachments().size() + " attachment(s)", false);
                if (SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
                    SnipeChanBot.snipedCache.remove(0);
                addButton = true;
            }
        } else if (SnipeChanBot.config.isSnipeDeletedMessages()) {
            if (!originalMessage.getContentRaw().isBlank()) {
                String msg = originalMessage.getContentRaw();
                if (msg.length() >= 1024)
                    msg = msg.substring(0, 1021) + "...";
                emb.addField("**Message Deleted:**", msg, true);
                if (SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
                    SnipeChanBot.snipedCache.remove(0);
            }
        }
        SnipeChanBot.snipedCache.add(new MessageInfo(emb.build(), originalMessage));
        MessageCreateBuilder mes = new MessageCreateBuilder()
                .setEmbeds(emb.build());
        if (SnipeChanBot.config.isSendSnipeNotifs()) {
            MessageCreateAction ma = event.getChannel().sendMessageEmbeds(emb.build());
            if (addButton) {
                Collection<ActionRow> collection = new ArrayList<>();
                Collection<Button> collection1 = new ArrayList<>();
                Collection<Button> collection2 = new ArrayList<>();
                int filecount = 0;
                for (Attachment i : originalMessage.getAttachments()) {
                    String name = (i.getFileName().length() > 80 ? i.getFileName().substring(0, 77) + "..." : i.getFileName());
                    String link = i.getUrl();
                    if (link.length() > 512) {
                        link = "https://www.generatormix.com/random-gif-generator?safe=on";
                        name = "[ \uF6C8 Link too long ]";
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
                mes.setComponents(collection);
            }
            ma.queue();
        }
        final TextChannel LOGS_CHANNEL = SnipeChanBot.jda.getTextChannelById(SnipeChanBot.config.getSnipeDeletedLogsID());
        if (LOGS_CHANNEL == null) {
            if (!SnipeChanBot.config.getSnipeDeletedLogsID().isBlank()) {
                System.out.println("______________________________________________________");
                System.out.println("Given deleted message log channel ID is invalid.");
            }
        } else {
            LOGS_CHANNEL.sendMessage(mes.build()).queue();
        }
    }
}
