package SnipeBot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class DeletedMessage extends ListenerAdapter {

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		if(!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
			return;

		String messageID = event.getMessageId();
		int messageIndex = -1;
		for(int i = 0 ; i < SnipeChanBot.messageCache.size(); i++) {
			if(SnipeChanBot.messageCache.get(i).getId().equals(messageID)) {
				messageIndex = i;
				break;
			}
		}
		if(messageIndex == -1)
			return;

		boolean addButton = false;

		Message originalMessage = SnipeChanBot.messageCache.get(messageIndex);
		SnipeChanBot.messageCache.remove(messageIndex);

		EmbedBuilder emb = new EmbedBuilder()
				.setAuthor(originalMessage.getMember().getUser().getAsTag(), null, originalMessage.getMember().getUser().getAvatarUrl())
				.setDescription(originalMessage.getMember().getAsMention() + "'s message has been deleted")
				.setFooter(
						"Message Sent • " + originalMessage.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME).substring(5) + 
						"\nMessage Deleted • " + new java.util.Date().toGMTString());
		if(SnipeChanBot.config.isSnipeDeletedFiles() && SnipeChanBot.config.isSnipeDeletedMessages()) {
			if(!originalMessage.getContentRaw().isBlank()) {
				emb.appendDescription("\n\n**Message Deleted:** " + originalMessage.getContentRaw());
			}
			if(originalMessage.getAttachments().size() > 0) {
				emb.appendDescription("\n\n" + originalMessage.getAttachments().size() + " attachment(s)");
				addButton = true;
			}
			if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
				SnipeChanBot.snipedCache.remove(0);
			SnipeChanBot.snipedCache.add(new MessageInfo(emb.build(), originalMessage));
		}else if(SnipeChanBot.config.isSnipeDeletedFiles()) {
			if(originalMessage.getAttachments().size() > 0) {
				emb.appendDescription("\n\n" + originalMessage.getAttachments().size() + " attachment(s)");
				if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
					SnipeChanBot.snipedCache.remove(0);
				SnipeChanBot.snipedCache.add(new MessageInfo(emb.build(), originalMessage));
				addButton = true;
			}
		}else if(SnipeChanBot.config.isSnipeDeletedMessages()) {
			if(!originalMessage.getContentRaw().isBlank()) {
				emb.appendDescription("\n\n**Message Deleted:** " + originalMessage.getContentRaw());
				if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
					SnipeChanBot.snipedCache.remove(0);
				SnipeChanBot.snipedCache.add(new MessageInfo(emb.build(), originalMessage));
			}
		}
		emb.appendDescription("\n\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_");
		MessageBuilder mes = new MessageBuilder()
				.setEmbeds(emb.build());
		if(SnipeChanBot.config.isSendSnipeNotifs()) {
			MessageAction ma = event.getChannel().sendMessageEmbeds(emb.build());
			if(addButton) {
				Collection<ActionRow> collection = new ArrayList<ActionRow>();
				Collection<Button> collection1 = new ArrayList<Button>();
				Collection<Button> collection2 = new ArrayList<Button>();
				int filecount = 0;
				for(Attachment i : originalMessage.getAttachments()) {
					if(filecount < 5) {
						collection1.add(Button.link(i.getUrl(), i.getFileName()));
					}else {
						collection2.add(Button.link(i.getUrl(), i.getFileName()));
					}
					filecount++;
				}
				ActionRow row1 = ActionRow.of(collection1);
				collection.add(row1);
				try {
					ActionRow row2 = ActionRow.of(collection2);
					collection.add(row2);
				}catch(Exception e) {}

				ma.setActionRows(collection);
				mes.setActionRows(collection);
			}
			ma.queue();
		}
		try{
			SnipeChanBot.jda.getTextChannelById(SnipeChanBot.config.getSnipeDeletedLogsID()).sendMessage(mes.build()).queue();
		}catch(Exception e) {
			if(!SnipeChanBot.config.getSnipeDeletedLogsID().isBlank()) {
				System.out.println("______________________________________________________");
				System.out.println("Given deleted message log channel ID is invalid.");
			}
		}
	}
}
