package SnipeBot;

import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeletedMessage extends ListenerAdapter {

	@SuppressWarnings("deprecation")
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
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
				emb.appendDescription("\n\n" + originalMessage.getAttachments().size() + " attachments");
			}
			if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
				SnipeChanBot.snipedCache.remove(0);
			SnipeChanBot.snipedCache.add(emb.build());
		}else if(SnipeChanBot.config.isSnipeDeletedFiles()) {
			if(originalMessage.getAttachments().size() > 0) {
				emb.appendDescription("\n\n" + originalMessage.getAttachments().size() + " attachments");
				if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
					SnipeChanBot.snipedCache.remove(0);
				SnipeChanBot.snipedCache.add(emb.build());
			}
		}else if(SnipeChanBot.config.isSnipeDeletedMessages()) {
			if(!originalMessage.getContentRaw().isBlank()) {
				emb.appendDescription("\n\n**Message Deleted:** " + originalMessage.getContentRaw());
				if(SnipeChanBot.snipedCache.size() >= SnipeChanBot.config.getMaxSnipedCache())
					SnipeChanBot.snipedCache.remove(0);
				SnipeChanBot.snipedCache.add(emb.build());
			}
		}
		if(SnipeChanBot.config.isSendSnipeNotifs()) {
			event.getChannel().sendMessageEmbeds(emb.build()).queue();
		}
		try{
			SnipeChanBot.jda.getTextChannelById(SnipeChanBot.config.getSnipeDeletedLogsID()).sendMessageEmbeds(emb.build()).queue();
		}catch(Exception e) {
			if(!SnipeChanBot.config.getSnipeDeletedLogsID().isBlank()) {
				System.out.println("______________________________________________________");
				System.out.println("Given deleted message log channel ID is invalid.");
			}
		}
	}
}
