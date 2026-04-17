package server.commands;
import common.commands.Command;
import common.network.Response;
import server.TicketCollection;

public class ClearCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        collection.clearCollection();
        return new Response(true, "Коллекция очищена");
    }

    @Override
    public String getDescription() {
        return "Очищение коллекции";
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public boolean requiresTicket() {
        return false;
    }

}