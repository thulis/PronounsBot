package de.thulis.pronouns;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class Configuration {
    // configuration file
    private File configFile;
    public String prefix;
    public String[] pronouns;
    public String token;

    Configuration(File configFile) {
        this.configFile = configFile;
    }

    public void defaultConfig() {
        this.prefix = "!";
        this.pronouns = new String[5];
        this.pronouns[0] = "she/they";
        this.pronouns[1] = "he/they";
        this.pronouns[2] = "she/her";
        this.pronouns[3] = "he/him";
        this.pronouns[4] = "they/them";
    }

    public void parse() throws Exception {
        Scanner scanner = new Scanner(this.configFile);
        Vector<String> lines = new Vector<String>();
        // parse out lines of config file
        while(scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        for(String line : lines) {
            String[] lineSplit = line.split("=");
            // error in config file
            if(lineSplit.length < 2) throw new Exception("Fehler in Konfigurationsdatei!");
            switch(lineSplit[0].trim()) {
                case "token":
                    this.token = lineSplit[1].trim();
                    break;
                case "prefix":
                    this.prefix = lineSplit[1].trim();
                    break;
                case "pronouns":
                    // TODO: parse out tokens, cause it's a list of them...
                    break;
                default:
                    System.out.println("Unbekannte Konfigurationsoption: \"" + lineSplit[0] + "\"");
            }
        }
        scanner.close();
    }
}
