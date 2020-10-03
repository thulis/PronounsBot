package de.thulis.pronouns;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Configuration config = new Configuration(new File("config.txt"));
        config.defaultConfig();
        try { config.parse(); } catch(Exception e) { e.printStackTrace(); }

        try {
            JDA jda = new JDABuilder(config.token)
                    .addEventListeners(new DiscordMessage(config))
                    .build();

            // block until jda is ready
            jda.awaitReady();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
