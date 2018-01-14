package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Configuration;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;

import java.io.File;
import java.io.IOException;

public class CommandRanks extends CommandHandler{
    public CommandRanks() {
        super(new String[] {"ranks"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.EVERYONE, "command.rank"), "Easily create ranks, that users can assign herself", "<role1, role2, .../add/remove> <rolename>", true);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.invocationMessage;
        String[] args = parsedCommandInvocation.args;
        File file = new File("data/ranks/" + message.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        //Fixes bug, that roles cannot be deleted
        ranks.set("bugfix", "true");
        if(file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(args.length == 0)
            return createHelpMessage();
        if(args[0].equals("add")){
            if(args.length < 1)
                return createHelpMessage();
           if(args.length > 2)
               return new MessageBuilder().setEmbed(EmbedUtil.error("Incorect rolename", "Roles cannot contain spaces c:").build()).build();
           if(ranks.has(args[1]))
                return new MessageBuilder().setEmbed(EmbedUtil.error("Name used", "This name is already used").build()).build();
           Role role;
           try{
               role = message.getGuild().getController().createRole().setName(args[1]).complete();
           } catch (Exception error){
               return new MessageBuilder().setEmbed(EmbedUtil.error("No permissions", "I'm not permitted to create roles c:").build()).build();
           }
           ranks.set(args[1], role.getId());
           return new MessageBuilder().setEmbed(EmbedUtil.success("Created rank", "Successfully created rank! When you update rolename I will automatically update it too in my config, but when the new rolename contains spaces I will remove it from my list").build()).build();
        } else if(args[0].equals("remove")) {
            if(args.length == 1)
                return createHelpMessage();
            if(args.length > 2)
                return new MessageBuilder().setEmbed(EmbedUtil.error("Incorect rolename", "Roles cannot contain spaces c:").build()).build();
            if(!ranks.has(args[1]))
                return new MessageBuilder().setEmbed(EmbedUtil.error("Name not used", "This rolename is not used").build()).build();
            ranks.unset(args[1]);
            System.out.println(args[1]);
            return new MessageBuilder().setEmbed(EmbedUtil.success("Removed rank", "Successfully removed rank").build()).build();
        } else {
            System.out.println("moin");
                for (String arg : args) {
                    StringBuilder added = new StringBuilder();
                    StringBuilder removed = new StringBuilder();
                    if(ranks.has(arg)){
                        Role role = message.getGuild().getRoleById(ranks.getString(arg));
                        if(message.getMember().getRoles().contains(role)) {
                            message.getGuild().getController().removeRolesFromMember(message.getMember(), role).queue();
                            added.append(role.getName()).append(", ");
                        } else {
                            message.getGuild().getController().addRolesToMember(message.getMember(), role).queue();
                            removed.append(role.getName()).append(", ");
                        }
                    }
                }

            }
        return null;
    }

    public static boolean isRank(Role role){
        File file = new File("data/ranks/" + role.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        return ranks.has(role.getName());
    }

    public static void handleRoleModification(RoleUpdateNameEvent event){
        File file = new File("data/ranks/" + event.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        Role role = event.getRole();
        if(role.getName().split(" ").length > 0) return;
        ranks.unset(event.getOldName());
        ranks.set(role.getName(), role.getId());
    }
}
