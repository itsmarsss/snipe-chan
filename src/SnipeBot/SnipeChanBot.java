package SnipeBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class SnipeChanBot {
    static ArrayList<Message> messageCache = new ArrayList<>();
    static ArrayList<MessageInfo> snipedCache = new ArrayList<>();

    static Config config;
    static JDA jda;

    static final String version = "1.5.4";
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
        System.out.println("Version:" + versionCheck());
        System.out.println();
        parent = new File(SnipeChanBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        System.out.println("Path: " + parent);
        if (parent == null) {
            System.out.println("______________________________________________________");
            System.out.println("Unable to obtain path.");
            System.exit(0);
        }
        System.out.println();
        if (!readConfigYML()) {
            System.out.println("______________________________________________________");
            System.out.println("There was an error with config.yml");
            System.out.println("\t1. Make sure config.yml template exists");
            System.out.println("\t2. Make sure config.yml values are correctly inputted");
            System.exit(0);
        }
        if (!config.isValid()) {
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
        } catch (LoginException e) {
            System.out.println("______________________________________________________");
            System.out.println("Given token is invalid.");
            System.out.println("\t- Make sure to enable MESSAGE CONTENT INTENT");
            System.exit(0);
        }
        jda.getPresence().setActivity(Activity.watching("for disappearing messages..."));
        System.out.println("Setting status message...");
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        System.out.println("Setting status...");

        System.out.println("Checking server ID...");
        boolean found = false;
        for (Guild guild : jda.getGuilds()) {
            if (guild.getId().equals(config.getServerID())) {
                found = true;
            }
        }
        if (!found) {
            System.out.println("______________________________________________________");
            System.out.println("Given server ID is invalid.");
            System.exit(0);
        }

        System.out.println("Adding listeners...");
        jda.addEventListener(new EditedMessage());
        jda.addEventListener(new DeletedMessage());
        jda.addEventListener(new NewMessage());
        jda.addEventListener(new ButtonListener());
        System.out.println("Done!");
    }

    static String versionCheck() {
        URL url = null;
        String newest = "";
        String note = "Author's Note: ";
        try {
            url = new URL("https://raw.githubusercontent.com/itsmarsss/Snipe-Chan/main/newestversion");
            URLConnection uc;
            uc = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            newest = reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null)
                note += line + "\n";

            if (note.equals("Author's Note: "))
                note = "";

        } catch (Exception e) {
            return "Unable to check for version and creator's note";
        }
        if (!newest.equals(version)) {
            return "   [There is a newer version of Snipe Chan]" +
                    "\n\t##############################################" +
                    "\n\t   " + version + "(current) >> " + newest + "(newer)" +
                    "\nNew version: https://github.com/itsmarsss/Snipe-Chan/releases" +
                    "\n\t##############################################" +
                    "\n" + note;
        }
        return " This program is up to date!" +
                "\n" + note;
    }

    private static boolean readConfigYML() {
        InputStream is;
        try {
            is = new FileInputStream(new File(parent + "/config.yml"));
            Yaml yml = new Yaml(new Constructor(Config.class));
            config = yml.load(is);
            if (config.getBotToken().isBlank() || config.getServerID().isBlank()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
