package snipebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MessageInfo {
    private MessageEmbed embed;
    private Message message;

    public MessageInfo(MessageEmbed embed, Message message) {
        this.setEmbed(embed);
        this.setMessage(message);
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public void setEmbed(MessageEmbed embed) {
        this.embed = embed;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
