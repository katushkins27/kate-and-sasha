package server.commands;

import collection.TicketCollection;
import common.commands.Command;
import common.data.Ticket;
import common.network.Response;

public class AddCommand implements Command {
    @Override
    public Response execute(TicketCollection collection, String arg Object extraData) {
        if (!(extraData instanceof Ticket)) {
        return new Response(false, "Данные билета не переданы");
        }
        Ticket ticket = (Ticket) extraData;
        collection.add(Ticket);
        return new Response(true, "Данные билета переданы с ID: " + ticket.getID());
    }

    @Override
    public String getDescription() {
        return "Добавление элемента в коллекцию";
    }
    @Override
    public String getName() {return "add";}
    @Override
    public boolean requiresTicket() { return true; }
}