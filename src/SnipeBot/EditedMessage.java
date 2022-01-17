package SnipeBot;

import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EditedMessage extends ListenerAdapter {

	@SuppressWarnings("deprecation")
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
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

		Message originalMessage = SnipeChanBot.messageCache.get(messageIndex);
		SnipeChanBot.messageCache.remove(messageIndex);
		SnipeChanBot.messageCache.add(event.getMessage());

		EmbedBuilder emb = new EmbedBuilder()
				.setAuthor(originalMessage.getMember().getUser().getAsTag(), null, originalMessage.getMember().getUser().getAvatarUrl())
				.setDescription(originalMessage.getMember().getAsMention() + "'s message has been Edited")
				.setFooter(
						"Message Sent/Edited • " + originalMessage.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME).substring(5) + 
						"\nMessage Edited • " + new java.util.Date().toGMTString());
		if(SnipeChanBot.config.isSnipeEditedFiles() && SnipeChanBot.config.isSnipeEditedMessages()) {
			if(!originalMessage.getContentRaw().isBlank()) {
				emb.appendDescription("\n\n**Original Message:** " + originalMessage.getContentRaw())
				.appendDescription("\n**Current Message:** " + event.getMessage().getContentRaw());
			}
			if(originalMessage.getAttachments().size() > 0) {
				emb.appendDescription("\n\n" + originalMessage.getAttachments().size() + " attachments");
			}
			if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
				SnipeChanBot.snipedCache.remove(0);
			SnipeChanBot.snipedCache.add(emb.build());
		}else if(SnipeChanBot.config.isSnipeEditedFiles()) {
			if(originalMessage.getAttachments().size() > 0) {
				emb.appendDescription("\n\n" + originalMessage.getAttachments().size() + " attachments");
				if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
					SnipeChanBot.snipedCache.remove(0);
				SnipeChanBot.snipedCache.add(emb.build());
			}
		}else if(SnipeChanBot.config.isSnipeEditedMessages()) {
			if(!originalMessage.getContentRaw().isBlank()) {
				emb.appendDescription("\n\n**Original Message:** " + originalMessage.getContentRaw())
				.appendDescription("\n**Current Message:** " + event.getMessage().getContentRaw());
				if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
					SnipeChanBot.snipedCache.remove(0);
				SnipeChanBot.snipedCache.add(emb.build());
			}
		}
		if(SnipeChanBot.config.isSendSnipeNotifs()) {
			event.getChannel().sendMessageEmbeds(emb.build()).queue();
		}
		try{
			SnipeChanBot.jda.getTextChannelById(SnipeChanBot.config.getSnipeEditedLogsID()).sendMessageEmbeds(emb.build()).queue();
		}catch(Exception e) {
			if(!SnipeChanBot.config.getSnipeEditedLogsID().isBlank()) {
				System.out.println("______________________________________________________");
				System.out.println("Given edited message log channel ID is invalid.");
			}
		}
	}
}
