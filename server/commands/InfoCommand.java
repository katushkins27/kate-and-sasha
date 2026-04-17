package server.commands;

import common.commands.Command;
import common.network.Response;
import collection.TicketCollection;

public class InfoCommand implements Command {
    @Override
    ppublic Response execute(TicketCollection collection, String arg, Object extraData) {
        return new Response(true, collection.getInfo());
    }

    @Override
    public String getDescription() {
        return "Вывод инфо о коллекции";
    }
    @Override
    public String getName() {
        return "info";
    }
    @Override
    public boolean requiresTicket() { return false; }
}