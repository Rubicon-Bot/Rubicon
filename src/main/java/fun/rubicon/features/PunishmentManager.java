package fun.rubicon.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PunishmentManager {

    private List<PunishmentHandler> punishmentHandlers = new ArrayList<>();

    public void registerPunishmentHandler(PunishmentHandler handler){
        punishmentHandlers.add(handler);
    }

    public void registerPunishmentHandlers(PunishmentHandler... handlers){
        Collections.addAll(punishmentHandlers, handlers);
    }

    public void loadPunishments(){
        punishmentHandlers.forEach(PunishmentHandler::loadPunishments);
    }
}
