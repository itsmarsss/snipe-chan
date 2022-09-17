package SnipeBot;

import java.util.ArrayList;
import java.util.Collection;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class NewMessage extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
			return;
		
		if((SnipeChanBot.config.isEnableSnipeCommand() && event.getMessage().getContentRaw().contains(SnipeChanBot.jda.getSelfUser().getId()))
				||
				((SnipeChanBot.config.isEnableSnipeCommand() && event.getMessage().getContentRaw().startsWith(SnipeChanBot.config.getPrefix()+"help")))) {
			event.getMessage().reply("My prefix is `" + SnipeChanBot.config.getPrefix() + "`, do `" + SnipeChanBot.config.getPrefix() + "sniped <index>` for a sniped message, `" + SnipeChanBot.config.getPrefix() + "snipelist` for a list of snipes, and `" + SnipeChanBot.config.getPrefix() + "snipe` for latest sniped message!").queue();
			return;
		}

		if(event.getMessage().getContentRaw().toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "sniped")) {
			try {
				int index = SnipeChanBot.snipedCache.size()-1 - Integer.parseInt(event.getMessage().getContentRaw().substring((SnipeChanBot.config.getPrefix() + "sniped").length()+1));
				if(index > SnipeChanBot.config.getMaxSnipedCache() || index < 0) {
					event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size()-1) + "]").queue();
					return;
				}

				MessageAction ma = event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index).getEmbed());
				if(SnipeChanBot.snipedCache.get(index).getMessage().getAttachments().size() > 0) {
					Collection<ActionRow> collection = new ArrayList<ActionRow>();
					Collection<Button> collection1 = new ArrayList<Button>();
					Collection<Button> collection2 = new ArrayList<Button>();
					int filecount = 0;
					for(Attachment i : SnipeChanBot.snipedCache.get(index).getMessage().getAttachments()) {
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
				}
				ma.queue();
			}catch(Exception e) {
				event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size()-1) + "]").queue();
			}
		}else if(event.getMessage().getContentRaw().toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "snipelist")) {
			int index = SnipeChanBot.snipedCache.size()-1;
			if(index == -1) {
				event.getMessage().reply("Nothing in sniped cache").queue();
			}else {
				StringBuilder list = new StringBuilder();
				for(MessageInfo mi : SnipeChanBot.snipedCache) {
					StringBuilder temp = new StringBuilder();
					temp.append("SNIPE LIST");
					temp.append("\n--------------------");
					MessageEmbed em = mi.getEmbed();
					temp.append("\nAuthor: " + em.getAuthor().getName());
					temp.append("\nContent:");
					temp.append("\n" + em.getDescription());
					temp.append("\nInfo:");
					temp.append("\n" + em.getFooter().getText());
					temp.append("\n============================================================");
					list.append(temp);
				}
				event.getMessage().reply(list.toString().getBytes(), "Snipes.txt").queue();
			}
		}else if(event.getMessage().getContentRaw().toLowerCase().startsWith(SnipeChanBot.config.getPrefix() + "snipe")) {
			int index = SnipeChanBot.snipedCache.size()-1;
			if(index == -1) {
				event.getMessage().reply("Nothing in sniped cache").queue();
			}else {
				MessageAction ma = event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index).getEmbed());
				if(SnipeChanBot.snipedCache.get(index).getMessage().getAttachments().size() > 0) {
					Collection<ActionRow> collection = new ArrayList<ActionRow>();
					Collection<Button> collection1 = new ArrayList<Button>();
					Collection<Button> collection2 = new ArrayList<Button>();
					int filecount = 0;
					for(Attachment i : SnipeChanBot.snipedCache.get(index).getMessage().getAttachments()) {
						if(filecount < 5) {
							collection1.add(Button.link(i.getUrl(), i.getFileName()));
						}else {
							collection2.add(Button.link(i.getUrl(), i.getFileName()));
						}
						filecount++;
					}
					ActionRow row1 = ActionRow.of(collection1);
					ActionRow row2 = ActionRow.of(collection2);
					collection.add(row1);
					collection.add(row2);

					ma.setActionRows(collection);
				}
				ma.queue();
			}
		}


		if(!(SnipeChanBot.config.isSnipeMessageManagers()) && (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)))
			return;

		if(!(SnipeChanBot.config.isSnipeNonhumans()) && (event.getMember().getUser().isBot() || event.getMember().getUser().isSystem()))
			return;

		if(SnipeChanBot.messageCache.size() >= SnipeChanBot.config.getMaxMessageCache()) 
			SnipeChanBot.messageCache.remove(0);
		SnipeChanBot.messageCache.add(event.getMessage());
	}
}
