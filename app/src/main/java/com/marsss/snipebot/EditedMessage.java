package com.marsss.snipebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
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

public class EditedMessage extends ListenerAdapter {

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
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
        SnipeChanBot.messageCache.add(event.getMessage());

        Date date = new Date();

        EmbedBuilder emb = new EmbedBuilder()
                .setAuthor(originalMessage.getMember().getUser().getAsTag(), null, (originalMessage.getMember().getUser().getAvatarUrl() == null ? "https://cdn.discordapp.com/embed/avatars/" + ((int) (Math.random() * 6)) + ".png" : originalMessage.getMember().getUser().getAvatarUrl()))
                .setDescription(originalMessage.getMember().getAsMention() + "'s message has been edited in channel " + originalMessage.getChannel().getAsMention())
                .appendDescription("\n[[jump to  message](" + originalMessage.getJumpUrl() + ")]")
                .setFooter(
                        "\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014" +
                                "\nMessage Sent/Edited \u2022 " + originalMessage.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME).substring(5) +
                                "\nMessage Edited \u2022 " + date.toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME).substring(5));
        if (SnipeChanBot.config.isSnipeEditedFiles() && SnipeChanBot.config.isSnipeEditedMessages()) {
            if (!event.getMessage().getContentRaw().isBlank()) {
                String msg1 = originalMessage.getContentRaw().length() == 0 ? "N/A" : originalMessage.getContentRaw();
                if (msg1.length() >= 1024)
                    msg1 = msg1.substring(0, 1021) + "...";

                String msg2 = event.getMessage().getContentRaw().length() == 0 ? "N/A" : event.getMessage().getContentRaw();
                if (msg2.length() >= 1024)
                    msg2 = msg2.substring(0, 1021) + "...";
                emb.addField("**Original Message:**", msg1, true)
                        .addField("**Current Message:**", msg2, true);
            }
            if (originalMessage.getAttachments().size() > 0) {
                emb.addField("**Other:**", originalMessage.getAttachments().size() + " attachment(s)", false);
                addButton = true;
            }
            if (SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
                SnipeChanBot.snipedCache.remove(0);
        } else if (SnipeChanBot.config.isSnipeEditedFiles()) {
            if (originalMessage.getAttachments().size() > 0) {
                emb.addField("**Other:**", originalMessage.getAttachments().size() + " attachment(s)", false);
                if (SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
                    SnipeChanBot.snipedCache.remove(0);
                addButton = true;
            }
        } else if (SnipeChanBot.config.isSnipeEditedMessages()) {
            if (!originalMessage.getContentRaw().isBlank()) {
                String msg1 = originalMessage.getContentRaw().length() == 0 ? "N/A" : originalMessage.getContentRaw();
                if (msg1.length() >= 1024)
                    msg1 = msg1.substring(0, 1021) + "...";

                String msg2 = event.getMessage().getContentRaw().length() == 0 ? "N/A" : event.getMessage().getContentRaw();
                if (msg2.length() >= 1024)
                    msg2 = msg2.substring(0, 1021) + "...";
                emb.addField("**Original Message:**", msg1, true)
                        .addField("**Current Message:**", msg2, true);
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
        final TextChannel LOGS_CHANNEL = SnipeChanBot.jda.getTextChannelById(SnipeChanBot.config.getSnipeEditedLogsID());
        if (LOGS_CHANNEL == null) {
            if (!SnipeChanBot.config.getSnipeEditedLogsID().isBlank()) {
                System.out.println("______________________________________________________");
                System.out.println("Given edited message log channel ID is invalid.");
            }
        } else {
            LOGS_CHANNEL.sendMessage(mes.build()).queue();
        }
    }
}
