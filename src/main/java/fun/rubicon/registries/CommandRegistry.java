package fun.rubicon.registries;

import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.moderation.*;
import fun.rubicon.commands.music.*;
import fun.rubicon.commands.settings.*;
import fun.rubicon.commands.tools.*;
import fun.rubicon.features.poll.PunishmentManager;
import fun.rubicon.features.verification.VerificationCommandHandler;
import lombok.Data;

@Data
public class CommandRegistry {

    private final CommandManager commandManager;
    private final PunishmentManager punishmentManager;

    public void register() {
        //Bot Owner
        commandManager.registerCommandHandlers(
                new CommandEval(),
                new CommandBotstatus(),
                new CommandBotplay(),
                new CommandDisco(),
                new CommandTest(),
                new CommandBeta()
        );

        //Admin
        commandManager.registerCommandHandlers(
                new CommandPortal()
        );

        // Settings
        commandManager.registerCommandHandlers(
                new CommandJoinMessage(),
                new CommandLeaveMessage(),
                new CommandAutochannel(),
                new CommandJoinImage(),
                new CommandAutorole(),
                new CommandRanks(),
                new CommandLog()
        );

        // Fun
        commandManager.registerCommandHandlers(
                new CommandRandom(),
                new CommandLmgtfy(),
                new CommandAscii(),
                new CommandGiphy(),
                new CommandRip(),
                new CommandMedal(),
                new CommandRoadSign(),
                new CommandWeddingSign(),
                new CommandDice(),
                new CommandQR(),
                new CommandFortnite(),
                new CommandOverwatch(),
                new CommandMinecraft()
        );

        //General
        commandManager.registerCommandHandlers(
                new CommandHelp(),
                new CommandInfo(),
                new CommandAFK(),
                new CommandPrefix(),
                new CommandBio(),
                new CommandInvite(),
                new CommandSay(),
                new CommandUptime(),
                new CommandUserinfo(),
                new CommandMoney(),
                new CommandStatistics(),
                new CommandYTSearch(),
                new CommandPremium(),
                new CommandKey(),
                new CommandPing(),
                new CommandPermissionCheck(),
                new CommandProfile(),
                new CommandSupport(),
                new CommandBug()
        );

        //Moderation
        commandManager.registerCommandHandlers(
                new CommandUnmute(),
                new CommandUnban(),
                new CommandMoveall(),
                new CommandWarn(),
                new CommandClear()
        );

        //Punishments
        punishmentManager.registerPunishmentHandlers(
                new CommandMute(),
                new CommandBan()
        );

        //Tools
        commandManager.registerCommandHandlers(
                new CommandPoll(),
                new CommandShort(),
                new CommandYouTube(),
                new CommandNick(),
                new VerificationCommandHandler(),
                new CommandChoose(),
                new fun.rubicon.commands.tools.CommandSearch(),
                new CommandServerInfo(),
                new CommandRemindMe(),
                new CommandLeet(),
                new CommandGiveaway()
        );

        //Music
        commandManager.registerCommandHandlers(
                new CommandJoin(),
                new CommandLeave(),
                new CommandPlay(),
                new CommandForcePlay(),
                new CommandVolume(),
                new CommandSkip(),
                new CommandClearQueue(),
                new CommandQueue(),
                new CommandStop(),
                new CommandPause(),
                new CommandResume(),
                new CommandShuffle(),
                new CommandNow(),
                new CommandPlaylist()
        );

        //RPG
        commandManager.registerCommandHandlers(
        );
    }
}
