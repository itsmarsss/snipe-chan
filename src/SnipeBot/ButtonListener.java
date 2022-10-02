package SnipeBot;

import java.util.ArrayList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonListener extends ListenerAdapter {
	private ButtonClickEvent e;
	public void onButtonClick(ButtonClickEvent event) {
		try {
			if(!event.getGuild().getId().equals(SnipeChanBot.config.getServerID()))
				return;

			e = event;

			String id = event.getButton().getId();
			if(id.equals("delete")) {
				event.getMessage().delete().queue();
				e.reply("Request successful").setEphemeral(true).queue();
			}else if(id.startsWith("next-")) {

				int param = Integer.parseInt(id.replace("next-", ""));

				ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;

				try {
					Button prevButton = Button.primary("prev-"+(param-1), "\u2B05 Prev");
					Button nextButton = Button.primary("next-"+(param+1), "Next \u27A1");
					Button deleteButton = Button.secondary("delete", "Delete");
					Message message = new MessageBuilder()
							.setEmbeds(new EmbedBuilder(mi.get(param).getEmbed()).setTitle("Snipe #*" + param + "* of *" + (mi.size()-1) + "*:").build())
							.setActionRows(ActionRow.of(prevButton, nextButton, deleteButton))
							.build();

					e.deferEdit().queue();
					e.getMessage().editMessage(message).queue();
				}catch(Exception e) {
					this.e.reply("No next page.").setEphemeral(true).queue();
				}

			}else if(id.startsWith("prev-")) {

				int param = Integer.parseInt(id.replace("prev-", ""));

				ArrayList<MessageInfo> mi = SnipeChanBot.snipedCache;

				try {
					Button prevButton = Button.primary("prev-"+(param-1), "\u2B05 Prev");
					Button nextButton = Button.primary("next-"+(param+1), "Next \u27A1");
					Button deleteButton = Button.secondary("delete", "Delete");
					Message message = new MessageBuilder()
							.setEmbeds(new EmbedBuilder(mi.get(param).getEmbed()).setTitle("Snipe #*" + param + "* of *" + (mi.size()-1) + "*:").build())
							.setActionRows(ActionRow.of(prevButton, nextButton, deleteButton))
							.build();

					e.deferEdit().queue();
					e.getMessage().editMessage(message).queue();
				}catch(Exception e) {
					this.e.reply("No previous page.").setEphemeral(true).queue();
				}
			}
		}catch(Exception e) {
			this.e.reply("Request unsuccessful *(Hint: Embed possibly removed?)*").setEphemeral(true).queue();
		}
	}

}