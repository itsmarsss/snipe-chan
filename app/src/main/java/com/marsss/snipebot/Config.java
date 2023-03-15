package com.marsss.snipebot;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class Config {
    private String botToken;
    private String serverID;
    private String prefix;

    private boolean snipeDeletedMessages;
    private boolean snipeDeletedFiles;
    private boolean snipeEditedMessages;
    private boolean snipeEditedFiles;
    private boolean sendSnipeNotifs;
    private boolean snipeNonhumans;
    private boolean snipeMessageManagers;
    private boolean enableSnipeCommand;

    private int maxMessageCache;
    private int maxSnipedCache;

    private String snipeDeletedLogsID;
    private String snipeEditedLogsID;


    private String status;
    private String activity;
    private String name;
    private String url;

    public Config() {
//			String bt, 
//			String sid, 
//			boolean sdm, 
//			boolean sdf, 
//			boolean sem, 
//			boolean sef, 
//			boolean ssn, 
//			boolean snh, 
//			boolean smm,  
//			boolean esc,
//			int mmc,
//			int msc,
//			String sdlid,
//			String selid) {

//		botToken = bt;
//		serverID = sid;
//		
//		snipeDeletedMessages = sdm;
//		snipeDeletedFiles = sdf;
//		snipeEditedMessages = sem;
//		snipeEditedFiles = sef;
//		sendSnipeNotifs = ssn;
//		snipeNonhumans = snh;
//		snipeMessageManagers = smm;
//		enableSnipeCommand = esc;
//		
//		maxMessageCache = mmc;
//		maxSnipedCache = msc;
//		
//		snipeDeletedLogsID = sdlid;
//		snipeEditedLogsID = selid;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isSnipeDeletedMessages() {
        return snipeDeletedMessages;
    }

    public void setSnipeDeletedMessages(boolean snipeDeletedMessages) {
        this.snipeDeletedMessages = snipeDeletedMessages;
    }

    public boolean isSnipeDeletedFiles() {
        return snipeDeletedFiles;
    }

    public void setSnipeDeletedFiles(boolean snipeDeletedFiles) {
        this.snipeDeletedFiles = snipeDeletedFiles;
    }

    public boolean isSnipeEditedMessages() {
        return snipeEditedMessages;
    }

    public void setSnipeEditedMessages(boolean snipeEditedMessages) {
        this.snipeEditedMessages = snipeEditedMessages;
    }

    public boolean isSnipeEditedFiles() {
        return snipeEditedFiles;
    }

    public void setSnipeEditedFiles(boolean snipeEditedFiles) {
        this.snipeEditedFiles = snipeEditedFiles;
    }

    public boolean isSendSnipeNotifs() {
        return sendSnipeNotifs;
    }

    public void setSendSnipeNotifs(boolean sendSnipeNotifs) {
        this.sendSnipeNotifs = sendSnipeNotifs;
    }

    public boolean isSnipeNonhumans() {
        return snipeNonhumans;
    }

    public void setSnipeNonhumans(boolean snipeNonhumans) {
        this.snipeNonhumans = snipeNonhumans;
    }

    public boolean isSnipeMessageManagers() {
        return snipeMessageManagers;
    }

    public void setSnipeMessageManagers(boolean snipeMessageManagers) {
        this.snipeMessageManagers = snipeMessageManagers;
    }

    public boolean isEnableSnipeCommand() {
        return enableSnipeCommand;
    }

    public void setEnableSnipeCommand(boolean enableSnipeCommand) {
        this.enableSnipeCommand = enableSnipeCommand;
    }

    public int getMaxMessageCache() {
        return maxMessageCache;
    }

    public void setMaxMessageCache(int maxMessageCache) {
        this.maxMessageCache = maxMessageCache;
    }

    public int getMaxSnipedCache() {
        return maxSnipedCache;
    }

    public void setMaxSnipedCache(int maxSnipedCache) {
        this.maxSnipedCache = maxSnipedCache;
    }

    public String getSnipeDeletedLogsID() {
        return snipeDeletedLogsID;
    }

    public void setSnipeDeletedLogsID(String snipeDeletedLogsID) {
        this.snipeDeletedLogsID = snipeDeletedLogsID;
    }

    public String getSnipeEditedLogsID() {
        return snipeEditedLogsID;
    }

    public void setSnipeEditedLogsID(String snipeEditedLogsID) {
        this.snipeEditedLogsID = snipeEditedLogsID;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValid() {
        if (botToken.isBlank())
            return false;

        if (serverID.isBlank())
            return false;

        return !prefix.isBlank();
    }

    public OnlineStatus getParsedStatus() {
        if(status == null) {
            return OnlineStatus.DO_NOT_DISTURB;
        }

        switch(status.toLowerCase()) {
            case "online":
                return OnlineStatus.ONLINE;
            case "idle":
                return OnlineStatus.IDLE;
            default:
                return OnlineStatus.DO_NOT_DISTURB;
        }
    }

    public Activity.ActivityType getParsedActivity() {
        if(activity == null) {
            return Activity.ActivityType.WATCHING;
        }

        switch(activity.toLowerCase()) {
            case "playing":
                return Activity.ActivityType.PLAYING;
            case "competing":
                return Activity.ActivityType.COMPETING;
            case "listening":
                return Activity.ActivityType.LISTENING;
            case "streaming":
                return Activity.ActivityType.STREAMING;
            default:
                return Activity.ActivityType.WATCHING;
        }
    }

    public String getParsedName() {
        if(name == null) {
            return "for disappearing messages...";
        }
        if(name.isBlank()) {
            return "for disappearing messages...";
        }
        return name;
    }

    public String getParsedUrl() {
        if(url == null) {
            return "https://www.twitch.tv/Twitch";
        }

        if(getParsedActivity() == Activity.ActivityType.STREAMING) {
            return url;
        }
        return "https://www.twitch.tv/Twitch";
    }
}
