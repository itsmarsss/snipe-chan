package com.marsss.snipebot;

import com.marsss.snipebot.ui.ConsoleMirror;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;

public class SnipeChanBot {
    static final ArrayList<Message> messageCache = new ArrayList<>();
    static final ArrayList<MessageInfo> snipedCache = new ArrayList<>();

    static Config config;
    static JDA jda;

    static final String version = "1.6.4";
    private static String parent;
    private static final EnumSet<GatewayIntent> intent = EnumSet.of(
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.MESSAGE_CONTENT);

    private static boolean head = true;

    public static void main(String[] args) throws URISyntaxException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--nohead")) {
                head = false;
            }
        }
        if (head) {
            System.out.println("Loading UI...");

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Failed to set look and feel");
                System.out.println("\tYou can ignore this");
            }

            new ConsoleMirror();
        }

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
        System.out.println("Warning[1]: This program will use up a lot of computer ram if you make the setCache value extremely high");
        System.out.println("Warning[2]: Use this program at your own risk, I (the creator of this program) will not be liable for any issues that it causes to your Discord Server or computer (or mental health?)");
        System.out.println();
        System.out.println("Version:" + versionCheck());
        System.out.println();
        parent = new File(SnipeChanBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        System.out.println("Path: " + parent);

        boolean parentPass = true;
        if (parent == null) {
            System.out.println("______________________________________________________");
            System.out.println("Unable to obtain path.");

            if (!head) {
                System.exit(0);
            }
            parentPass = false;
        }

        if (parentPass) {
            readConfig();
        }
    }

    static void readConfig() {
        System.out.println();
        boolean configPass = true;
        if (!readConfigYML()) {
            System.out.println("______________________________________________________");
            System.out.println("There was an error with config.yml");
            System.out.println("\t1. Make sure config.yml template exists");
            System.out.println("\t2. Make sure config.yml values are correctly inputted");
            if (!head) {
                System.exit(0);
            }
            configPass = false;
        }

        if (configPass) {
            validate();
        }
    }

    static void validate() {
        boolean validPass = true;
        if (!config.isValid()) {
            System.out.println("______________________________________________________");
            System.out.println("There was an error with config.yml");
            System.out.println("\t1. Make sure config.yml template exists");
            System.out.println("\t2. Make sure config.yml values are correctly inputted");
            if (!head) {
                System.exit(0);
            }
            validPass = false;
        }

        if (validPass) {
            prompt();
        }
    }

    static void prompt() {
        System.out.println("~ Successfully read config.yml ~");
        System.out.println();
        System.out.println("** Press [enter] to start the bot **");
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        sc.close();
        start();
    }

    public static void start() {
        boolean setupPass = true;
        try {
            jda = JDABuilder.createDefault(config.getBotToken(), intent).build();
            System.out.println("Connecting to Discord...");
            System.out.println("Validating token...");
            jda.awaitReady();
        } catch (Exception e) {
            System.out.println("______________________________________________________");
            System.out.println("Given token is invalid.");
            System.out.println("\t- Make sure to enable MESSAGE CONTENT INTENT");
            if (!head) {
                System.exit(0);
            }
            setupPass = false;
        }

        if (setupPass) {
            activate();
        }
    }

    static void activate() {
        System.out.println("Setting status...");
        jda.getPresence().setStatus(config.getParsedStatus());

        System.out.println("Setting status message...");
        jda.getPresence().setActivity(Activity.of(config.getParsedActivity(), config.getParsedName(), config.getParsedUrl()));

        System.out.println("Checking server ID...");
        boolean found = false;
        for (Guild guild : jda.getGuilds()) {
            if (guild.getId().equals(config.getServerID())) {
                found = true;
            }
        }

        boolean serverIDPass = true;
        if (!found) {
            System.out.println("______________________________________________________");
            System.out.println("Given server ID is invalid.");

            if (!head) {
                System.exit(0);
            }
            serverIDPass = false;
        }

        if (serverIDPass) {
            complete();
        }
    }

    static void complete() {
        System.out.println("Adding listeners...");
        jda.addEventListener(new EditedMessage());
        jda.addEventListener(new DeletedMessage());
        jda.addEventListener(new NewMessage());
        jda.addEventListener(new ButtonListener());
        System.out.println("Done!");

        setupWebpage();
    }

    private static Webserver server;

    static void setupWebpage() {
        System.out.println();
        System.out.println("Starting Webserver...");
        try {
            server = new Webserver();
            server.startServer();

            System.out.println("Webpage setup completed!");
            System.out.println("\tOn port: " + server.getPort());

            System.out.println();
            System.out.println("Opening control panel...");

            controlPanel();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to start Webserver.");
            System.out.println("\tError message: " + e.getMessage());
        }
    }

    static String versionCheck() {
        URL url;
        String newest;
        StringBuilder note = new StringBuilder("Author's Note: ");
        try {
            url = new URL("https://raw.githubusercontent.com/itsmarsss/Snipe-Chan/main/newestversion");
            URLConnection uc;
            uc = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            newest = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null)
                note.append(line).append("\n");

            if (note.toString().equals("Author's Note: "))
                note = new StringBuilder();

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
                "\n" + note +
                "\n[https://github.com/itsmarsss/Snipe-Chan/releases]";
    }

    private static boolean readConfigYML() {
        InputStream is;
        try {
            is = new FileInputStream(parent + "/config.yml");
            Yaml yml = new Yaml(new Constructor(Config.class));
            config = (Config) yml.load(is);
            return !config.getBotToken().isBlank() && !config.getServerID().isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    public static void remove(String msgid) {
        for (int i = 0; i < snipedCache.size(); i++) {
            if (snipedCache.get(i).getMessage().getId().equals(msgid)) {
                snipedCache.remove(i);
                return;
            }
        }
    }

    public static void stop() {
        if (jda == null) {
            System.out.println("Bot already Stopped.");
            return;
        }
        System.out.println("Terminating connection with Discord...");
        jda.shutdownNow();
        jda = null;
        System.out.println("Connection terminated!");

        System.out.println("Closing server...");
        server.terminate();
        server = null;
        System.out.println("Server closed!");

        System.out.println("Bot Stopped.");
    }

    public static void controlPanel() {
        if (server == null) {
            System.out.println("Click [Start] first.");
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + server.getPort()));
            }
            System.out.println("Successfully sent user to control panel...");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open website.");
            System.out.println("\tVisit http://localhost:" + server.getPort() + " to access control panel.");
        }
    }

    public static String getVersion() {
        return version;
    }

    public static JDA getJDA() {
        return jda;
    }

    public static String getParent() {
        return parent;
    }
}
