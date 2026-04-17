package common.commands;

import collection.TicketCollection;
import common.network.*;

public interface Command {
    Response void execute(TicketCollection collection, String arg, Object extraData);
    String String getDescription();
    String String getName();
    boolean requiresTicket();
}
