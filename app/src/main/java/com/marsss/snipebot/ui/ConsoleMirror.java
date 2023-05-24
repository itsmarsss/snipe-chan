package com.marsss.snipebot.ui;

import com.marsss.snipebot.SnipeChanBot;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

public class ConsoleMirror extends JFrame {

    public ConsoleMirror() {
        super("Snipe Bot Console v" + SnipeChanBot.getVersion());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setForeground(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder());
        textArea.setBackground(new Color(30, 31, 34));
        textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

        PrintStream printStream = new PrintStream(new ConsoleOutputStream(textArea));
        System.setOut(printStream);

        JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        Button startButton = new Button("Start", new Color(88, 101, 242), new Color(71, 82, 196), new Color(60, 69, 165));
        startButton.addActionListener(actionEvent -> {
            if(SnipeChanBot.getJDA() != null) {
                System.out.println("Bot already Started.");
                return;
            }
            SnipeChanBot.start();
        });
        buttonPanel.add(startButton);

        Button endButton = new Button("Stop", new Color(242, 63, 66), new Color(198, 36, 36), new Color(161, 40, 40));
        endButton.addActionListener(actionEvent -> SnipeChanBot.stop());
        buttonPanel.add(endButton);

        Button siteButton = new Button("Control Panel", new Color(36, 128, 70), new Color(26, 99, 52), new Color(21, 86, 43));
        siteButton.addActionListener(actionEvent -> SnipeChanBot.controlPanel());
        buttonPanel.add(siteButton);

        Button editButton = new Button("config.yml", new Color(78, 80, 88), new Color(65, 68, 74), new Color(78, 80, 88));
        editButton.addActionListener(actionEvent -> {
            System.out.println();
            ProcessBuilder pb = new ProcessBuilder("Notepad.exe", SnipeChanBot.getParent() + "/config.yml");
            try {
                pb.start();

                System.out.println("You will need to restart the program for new changes to take place.");
                JOptionPane.showMessageDialog(null,
                        "You will need to restart the program for new changes to take place.",
                        "Snipe Bot Warning",
                        JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Notepad.exe not found:");
                System.out.println("\tUnable to open: " + SnipeChanBot.getParent() + "/config.yml");
                JOptionPane.showMessageDialog(null,
                        "Notepad.exe not found: Unable to open: " + SnipeChanBot.getParent() + "/config.yml",
                        "Snipe Bot Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(editButton);

        buttonPanel.setBackground(new Color(49, 51, 56));

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        ImageIcon icon = loadIconFromInternet("https://raw.githubusercontent.com/itsmarsss/Snipe-Chan/main/assets/image.png");
        if (icon != null) {
            setIconImage(icon.getImage());
        }

        setBackground(new Color(49, 51, 56));
        setSize(new Dimension(600, 400));
        setVisible(true);
    }

    private static ImageIcon loadIconFromInternet(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return new ImageIcon(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}