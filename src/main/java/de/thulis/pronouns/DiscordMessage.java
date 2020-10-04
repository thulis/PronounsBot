package de.thulis.pronouns;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
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

    private Role getRoleByName(String name, List<Role> roles) {
        /* for (Role r : roles) {
            if(r.getName().compareTo(name) == 0) return r;
            System.out.println("Role: \"" + r.getName() + "\", compareTo: " + r.getName().compareTo(name));
        } */
        for (Role r : roles) if (r.getName().compareTo(name) == 0) return r;
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
                List<Role> roles = server.getRoles();
                StringBuilder sb = new StringBuilder();
                /*for (int i = 0; i < roles.size()-1; i++) {
                    sb.append(roles.get(i).getName());
                    sb.append(", ");
                }*/
                for (Role r : roles) sb.append(r.getName() + ", ");
                event.getChannel().sendMessage(sb.toString()).queue();
                break;
            case "!change":
                Member authorC = event.getMember();
                if(authorC == null) return;
                Role roleIdC = getRoleByName(messageSplit[2].trim(), server.getRoles());
                if(roleIdC == null) return;
                server.removeRoleFromMember(authorC, roleIdC).queue();
                event.getChannel().sendMessage("Changed your pronouns from \"" + roleIdC.getName() + "\" to \"" + messageSplit[1].trim() + "\"").queue();
                // expected fallthrough
            case "!add":
                Member author = event.getMember();
                if(author == null) return;
                Role roleId = getRoleByName(messageSplit[1].trim(), server.getRoles());
                if(roleId == null) {
                    event.getChannel()
                            .sendMessage("Couldn't find role \"" + messageSplit[1].trim() + "\". You can add it with the command \"!ADD " + messageSplit[1].trim()+"\"").queue();
                    return;
                }
                if (roleId.getName().equals("Admin")) {
                    event.getChannel().sendMessage("Don't you even try... You can't assign yourself this role!").queue();
                    return;
                }
                try {
                    server.addRoleToMember(author, roleId).queue();
                } catch (HierarchyException e) {
                    event.getChannel().sendMessage("Don't you even try... You can't assign yourself this role!").queue();
                    return;
                }
                event.getChannel().sendMessage("Added \"" + roleId.getName() + "\" to your account.").queue();
                break;
            case "!remove":
                Member authorR = event.getMember();
                if(authorR == null) return;
                Role roleIdR = getRoleByName(messageSplit[1].trim(), server.getRoles());
                if(roleIdR == null) return;
                server.removeRoleFromMember(authorR, roleIdR).queue();
                event.getChannel().sendMessage("Removed \"" + roleIdR.getName() + "\" from your account.").queue();
                break;
            case "!ADD":
                // TODO: check that messageSplit actually contains >1 element
                // role already exists
                Role r = getRoleByName(messageSplit[1].trim(), server.getRoles());
                if (r != null) return;
                try {
                    server.createRole().queue(role -> {
                        role.getManager().setName(messageSplit[1].trim())
                                .setMentionable(true).queue();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage(e.getStackTrace().toString()).queue();
                }
                event.getChannel().sendMessage("Added \"" + messageSplit[1].trim() + "\" to this server. You can apply it to your account with \"!add " + messageSplit[1].trim() + "\"").queue();
                break;
            case "!REMOVE":
                Role toBeDeleted = getRoleByName(messageSplit[1].trim(), server.getRoles());
                toBeDeleted.delete().queue();
                event.getChannel().sendMessage("Permanently removed \"" + toBeDeleted.getName() + "\" from this server.").queue();
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
            case "!help":
                event.getChannel().sendMessage(
                        "The Pronouns Bot can add roles whose names are pronouns to your account.\n" +
                        "All pronouns are/shall be formatted in a format like a/b, e.g.: \"they/them\", all commands start with \"" + this.cmdPrefix + "\".\n" +
                        "To get a list of all pronouns on this server type \"!list\".\n" +
                        "For a list of all available commands and this hopefully helpful help message, type \"!help\".\n" +
                        "List of commands:\n" +
                        "- !add a/b           Add given pronouns a/b to your account.\n" +
                        "- !remove a/b        Remove given pronouns a/b from your account.\n" +
                        "- !change to from    Change pronouns, where \"to\" = a/b and \"from\" = c/d.\n" +
                        "- !ADD a/b           Add the given pronouns a/b to the server if they don't already exist. Executing it anyway won't have an effect.\n" +
                        "- !REMOVE a/b        Permanently remove pronouns a/b from this server. They can be added via \"!ADD a/b\" though.\n" +
                        "The code for this bot can be found at https://github.com/thulis/PronounsBot, contributions/ideas welcome (you don't need an account, just open an issue)."
                ).queue();
                break;
            default:
                event.getChannel().sendMessage("Unknown command: \"" + messageSplit[0].trim() + "\"").queue();
        }
    }
}
