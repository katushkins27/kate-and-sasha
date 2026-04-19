package server.commands;

import common.commands.Command;
import common.data.Ticket;
import common.network.Response;
import server.TicketCollection;

public class HeadCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        Ticket ticket = collection.head();
        if (ticket == null) {
            return new Response(true, "Коллекция пустая");
        } else {
            return new Response(true, ticket.toString());
        }
    }

    @Override
    public String getDescription() {
        return "Первый билет коллекции";
    }
    @Override
    public String getName() {
        return "head";
    }
    @Override
    public boolean requiresTicket() { return false; }
}