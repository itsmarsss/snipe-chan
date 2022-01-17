package SnipeBot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewMessage extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
			return;

		if(SnipeChanBot.config.isEnableSnipeCommand() && event.getMessage().getContentRaw().startsWith("<@!" + SnipeChanBot.jda.getSelfUser().getId() + ">")) {
			event.getMessage().reply("My prefix is `" + SnipeChanBot.config.getPrefix() + "`, do `" + SnipeChanBot.config.getPrefix() + "sniped` for a sniped messages!").queue();
			return;
		}
		
		if(event.getMessage().getContentRaw().startsWith(SnipeChanBot.config.getPrefix() + "sniped")) {
			try {
				int index = Integer.parseInt(event.getMessage().getContentRaw().substring((SnipeChanBot.config.getPrefix() + "sniped").length()+1));
				if(index > SnipeChanBot.config.getMaxSnipedCache() || index < 0) {
					event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size()-1) + "]").queue();
					return;
				}
				event.getMessage().replyEmbeds(SnipeChanBot.snipedCache.get(index)).queue();
			}catch(Exception e) {
				event.getMessage().reply("Invalid number: [0 to " + (SnipeChanBot.snipedCache.size()-1) + "]").queue();
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
