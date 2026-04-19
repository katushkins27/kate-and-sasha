package common.commands;

import server.TicketCollection;
import common.network.*;

public interface Command {
    Response execute(TicketCollection collection, String arg, Object extraData);
    String getDescription();
    String getName();
    boolean requiresTicket();
}
