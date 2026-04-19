package server.commands;

import common.commands.Command;
import common.network.Response;
import server.TicketCollection;

public class ShowCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        String res = collection.showAll();
        return new Response(true, res);
    }

    @Override
    public String getDescription() {
        return "Вывод всех элементов коллекции";
    }
    @Override
    public String getName() {
        return "show";
    }
    @Override
    public boolean requiresTicket() { return false; }
}