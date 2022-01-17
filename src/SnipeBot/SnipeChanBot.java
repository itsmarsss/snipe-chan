package SnipeBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class SnipeChanBot {
	static ArrayList <Message> messageCache = new ArrayList <Message>();
	static ArrayList <MessageEmbed> snipedCache = new ArrayList <MessageEmbed>();

	static Config config;
	static JDA jda;

	private static String parent;
	private static final EnumSet<GatewayIntent> intent = EnumSet.of(GatewayIntent.GUILD_MESSAGES);
	public static void main(String[] args) throws UnsupportedEncodingException, URISyntaxException, FileNotFoundException, LoginException, InterruptedException {
		System.out.println(" ____  _   _ ___ ____  _____ ____ _   _    _    _   _ ");
		System.out.println("/ ___|| \\ | |_ _|  _ \\| ____/ ___| | | |  / \\  | \\ | |");
		System.out.println("\\___ \\|  \\| || || |_) |  _|| |   | |_| | / _ \\ |  \\| |");
		System.out.println(" ___) | |\\  || ||  __/| |__| |___|  _  |/ ___ \\| |\\  |");
		System.out.println("|____/|_| \\_|___|_|   |_____\\____|_| |_/_/   \\_\\_| \\_|");
		System.out.println("------------------------------------------------------");
		System.out.println("   =========== PROGRAM SOURCE CODE ===========");
		System.out.println("   = https://github.com/itsmarsss/Snipe-Chan =");
		System.out.println("   ===========================================");
		System.out.println("      Welcome to Snipe Chan's Control Prompt");
		System.out.println();
		System.out.println("Purpose: Allows your Discord Server members to snipe edited or deleted messages and files");
		System.out.println();
		System.out.println("Note: This program will only run for 1 Discord Server, if you have multiple Discord Servers that you want this program to work on, then you will need to run multiple copies of this program in different directories (Make sure to set Server ID in each config.yml)");
		System.out.println();
		System.out.println("Warning[1]: This program will use up alot of computer ram if you make the setCache value extremely high");
		System.out.println("Warning[2]: Use this program at your own risk, I (the creator of this program) will not be liable for any issues that it causes to your Discord Server or computer (or mental health?)");
		System.out.println();
		parent = SnipeChanBot.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();
		System.out.println("Path: " + parent);
		System.out.println();
		if(!readConfigYML()) {
			System.out.println("______________________________________________________");
			System.out.println("There was an error with config.yml");
			System.out.println("\t1. Make sure config.yml template exists");
			System.out.println("\t2. Make sure config.yml values are correctly inputted");
			System.exit(0);
		}
		if(!config.isValid()) {
			System.out.println("______________________________________________________");
			System.out.println("There was an error with config.yml");
			System.out.println("\t1. Make sure config.yml template exists");
			System.out.println("\t2. Make sure config.yml values are correctly inputted");
			System.exit(0);
		}
		System.out.println("~ Successfully read config.yml ~");
		System.out.println();
		System.out.println("** Press [enter] to start the bot **");
		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		sc.close();
		try {
			jda = JDABuilder.createDefault(config.getBotToken(), intent).build();
			System.out.println("Connecting to Discord...");
			System.out.println("Validating token...");
		jda.awaitReady();
		} catch(Exception e) {
			System.out.println("______________________________________________________");
			System.out.println("Given token is invalid.");
			System.out.println("\t- Make sure to enable MESSAGE CONTENT INTENT");
			System.exit(0);
		}
		jda.getPresence().setActivity(Activity.watching("for disappearing messages..."));
		System.out.println("Setting status message...");
		jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
		System.out.println("Setting status...");
		try {
			jda.getGuildById(config.getServerID());
			System.out.println("Checking server ID...");
		} catch(Exception e) {
			System.out.println("______________________________________________________");
			System.out.println("Given server ID is invalid.");
			System.exit(0);
		}
		System.out.println("Adding listeners...");
		jda.addEventListener(new EditedMessage());
		jda.addEventListener(new DeletedMessage());
		jda.addEventListener(new NewMessage());
		System.out.println("Done!");
	}

	private static boolean readConfigYML() {
		InputStream is;
		try {
			is = new FileInputStream(new File("C:\\Users\\kenny\\eclipse-workspace\\Snipe Chan\\bin\\config.yml"));
			Yaml yml = new Yaml(new Constructor(Config.class));
			config = yml.load(is);
			if(config.getBotToken().isBlank() || config.getServerID().isBlank()) {
				System.out.println("here");
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
