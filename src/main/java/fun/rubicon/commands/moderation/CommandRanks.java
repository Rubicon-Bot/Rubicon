package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Configuration;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRanks extends CommandHandler {
    public CommandRanks() {
        super(new String[]{"role", "roles", "rank", "ranks"}, CommandCategory.MODERATION, new PermissionRequirements("command.rank", false, false), "Easily create ranks, that users can assign herself", "<rolename>... <rolename>\n" +
                "add <rolename>\n" +
                "remove <rolename>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        new File("data/ranks").mkdirs();
        File file = new File("data/ranks/" + message.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        //Fixes bug, that roles cannot be deleted
        ranks.set("bugfix", "true");
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (args.length == 0)
            return createHelpMessage();
        if (args[0].equals("add")) {
            if (args.length > 2)
                return new MessageBuilder().setEmbed(EmbedUtil.error("Incorect rolename", "Roles cannot contain spaces c:").build()).build();
            if (ranks.has(args[1]))
                return new MessageBuilder().setEmbed(EmbedUtil.error("Name used", "This name is already used").build()).build();
            Role role;
            try {
                role = message.getGuild().getController().createRole().setName(args[1]).complete();
            } catch (Exception error) {
                return new MessageBuilder().setEmbed(EmbedUtil.error("No permissions", "I'm not permitted to create roles c:").build()).build();
            }
            ranks.set(args[1], role.getId());
            return new MessageBuilder().setEmbed(EmbedUtil.success("Created rank", "Successfully created rank! When you update rolename I will automatically update it too in my config, but when the new rolename contains spaces I will remove it from my list").build()).build();
        } else if (args[0].equals("remove")) {
            if (args.length == 1)
                return createHelpMessage();
            if (args.length > 2)
                return new MessageBuilder().setEmbed(EmbedUtil.error("Incorect rolename", "Roles cannot contain spaces c:").build()).build();
            if (!ranks.has(args[1]))
                return new MessageBuilder().setEmbed(EmbedUtil.error("Name not used", "This rolename is not used").build()).build();
            ranks.unset(args[1]);
            return new MessageBuilder().setEmbed(EmbedUtil.success("Removed rank", "Successfully removed rank").build()).build();
        } else if (args[0].equals("list")) {
            StringBuilder ranksList = new StringBuilder();
            if (ranks.keySet().stream().filter(k -> !k.equals("bugfix")).collect(Collectors.toList()).isEmpty())
                return new MessageBuilder().setEmbed(EmbedUtil.info("No ranks", "There are no ranks on this server").build()).build();
            ranks.keySet().forEach(k -> {
                if (!k.equals("bugfix"))
                    ranksList.append(k).append(", ");
                ranksList.replace(ranksList.lastIndexOf(","), ranksList.lastIndexOf(",") + 1, "");
            });
            return new MessageBuilder().setEmbed(EmbedUtil.info("Ranks", "```" + ranksList.toString() + "```").build()).build();
        } else {
            StringBuilder added = new StringBuilder();
            StringBuilder removed = new StringBuilder();
            List<Role> toAdd = new ArrayList<>();
            List<Role> toRemove = new ArrayList<>();
            for (String arg : args) {
                if (ranks.has(arg) && !arg.equals("bugfix")) {
                    Role role = message.getGuild().getRoleById(ranks.getString(arg));
                    if (message.getMember().getRoles().contains(role)) {
                        toRemove.add(role);
                        removed.append(role.getName()).append(", ");
                    } else {
                        toAdd.add(role);
                        added.append(role.getName()).append(", ");
                    }
                }
            }
            if (!toAdd.isEmpty()) {
                message.getGuild().getController().addRolesToMember(message.getMember(), toAdd).queue();
                added.replace(added.lastIndexOf(","), added.lastIndexOf(",") + 1, "");
            }
            if (!toRemove.isEmpty()) {
                message.getGuild().getController().removeRolesFromMember(message.getMember(), toRemove).queue();
                removed.replace(removed.lastIndexOf(","), removed.lastIndexOf(",") + 1, "");
            }
            EmbedBuilder result = new EmbedBuilder();
            result.setColor(Colors.COLOR_PRIMARY);
            result.setTitle("Modified ranks");
            if (toAdd.isEmpty() && toRemove.isEmpty())
                return new MessageBuilder().setEmbed(EmbedUtil.error("Unknown roles", "You entered incorrect rolenames").build()).build();
            if (!added.toString().equals(""))
                result.addField("Added ranks", added.toString(), false);
            if (!removed.toString().equals(""))
                result.addField("Removed ranks", removed.toString(), false);
            return new MessageBuilder().setEmbed(result.build()).build();

        }
    }


    public static boolean isRank(Role role) {
        File file = new File("data/ranks/" + role.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        return ranks.has(role.getName());
    }

    public static void handleRoleModification(RoleUpdateNameEvent event) {
        File file = new File("data/ranks/" + event.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        Role role = event.getRole();
        if (!isRank(role)) return;
        if (role.getName().split(" ").length > 0) return;
        ranks.unset(event.getOldName());
        ranks.set(role.getName(), role.getId());
    }

    public static void handleRoleDeletion(RoleDeleteEvent event) {
        File file = new File("data/ranks/" + event.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        Role role = event.getRole();
        if (!isRank(role)) return;
        ranks.unset(role.getName());
    }
}
