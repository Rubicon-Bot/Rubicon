package fun.rubicon.features.poll;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandHandler;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PunishmentManager {

    private List<PunishmentHandler> punishmentHandlers = new ArrayList<>();

    private HashMap<Member, Long> muteCache = new HashMap<>();

    public void registerPunishmentHandler(PunishmentHandler handler) {
        if (RubiconBot.getCommandManager() != null) {
            RubiconBot.getCommandManager().registerCommandHandler((CommandHandler) handler);
        }
        punishmentHandlers.add(handler);
    }

    public void registerPunishmentHandlers(PunishmentHandler... handlers) {
        Collections.addAll(punishmentHandlers, handlers);
        for (PunishmentHandler handler : handlers) {
            assert RubiconBot.getCommandManager() != null;
            RubiconBot.getCommandManager().registerCommandHandler((CommandHandler) handler);
        }
    }

    public void loadPunishments() {
        punishmentHandlers.forEach(PunishmentHandler::loadPunishments);
    }

    public HashMap<Member, Long> getMuteCache() {
        return muteCache;
    }

}
