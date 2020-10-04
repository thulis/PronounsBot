package de.thulis.pronouns;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class DiscordMessage extends ListenerAdapter {
    // store the config file for reloading
    private Configuration config;
    // Prefix for Commands, e.g. "!"
    private String cmdPrefix;

    // default constructor
    DiscordMessage(Configuration config) {
        this.config = config;
        this.cmdPrefix = config.prefix;
    }

    /*// probably doesn't work that well if used on mulitple servers
    @Override
    public void onReady(ReadyEvent event) {
        this.roles = event.getJDA().getRoles();
        System.out.println(roles);
    }*/

    private Role getRoleByName(String name, List<Role> roles) {
        for (Role r : roles) {
            if(r.getName().compareTo(name) == 0) return r;
            //System.out.println(r.getName());
            //System.out.println("Role: \"" + r.getName() + "\", compareTo: " + r.getName().compareTo(name));
        }
        return null;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        // no need to switch on a message that doesn't contain a command
        if(!message.startsWith(this.cmdPrefix)) return;
        String[] messageSplit = message.split(" ");
        Guild server = event.getGuild();
        switch(messageSplit[0].trim()) {
            case "!list":
                break;
            case "!add":
                Member author = event.getMember();
                if(author == null) return;
                Role roleId = getRoleByName(messageSplit[1].trim(), server.getRoles());
                if(roleId == null) return;
                server.addRoleToMember(author, roleId).queue();
                break;
            case "!remove":
                Member authorR = event.getMember();
                if(authorR == null) return;
                Role roleIdR = getRoleByName(messageSplit[1].trim(), server.getRoles());
                if(roleIdR == null) return;
                server.removeRoleFromMember(authorR, roleIdR).queue();
                break;
            case "!change":
                Member authorC = event.getMember();
                if(authorC == null) return;
                Role roleIdC = getRoleByName(messageSplit[1].trim(), server.getRoles());
                Role newRole = getRoleByName(messageSplit[2].trim(), server.getRoles());
                if(roleIdC == null || newRole == null) return;
                server.removeRoleFromMember(authorC, roleIdC).queue();
                server.addRoleToMember(authorC, newRole).queue();
                break;
            case "!ADD":
                // TODO: check that messageSplit actually contains >1 element
                // role already exists
                Role r = getRoleByName(messageSplit[1].trim(), server.getRoles());
                if (r != null) return;
                System.out.println("Found a role: " + r);
                server.createRole().queue(role -> {
                      role.getManager().setName(messageSplit[1].trim())
                                       .setMentionable(true).queue();
                });
                break;
            case "!REMOVE":
                break;
            case "!reload":
                event.getChannel().sendMessage("Reloading config file...");
                // reaload config file and send errors to the channel the command was issued in
                try { this.config.parse(); } catch(Exception e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage(e.toString());
                }
                break;
            case "!henlo":
                event.getChannel().sendMessage("Henlo!").queue();
                break;
            default:
                event.getChannel().sendMessage("Unknown command!");
        }
    }
}
