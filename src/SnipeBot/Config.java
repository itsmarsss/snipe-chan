package SnipeBot;

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

	public void setbotToken(String botToken) {
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

	public boolean isValid() {
		if(botToken.isBlank())
			return false;
		
		if(serverID.isBlank())
			return false;
		
		return true;
	}
}
