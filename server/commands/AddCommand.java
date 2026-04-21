package server.commands;

import server.TicketCollection;
import common.commands.Command;
import common.data.Ticket;
import common.network.Response;
import server.util.CreateID;

public class AddCommand implements Command {
    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        if (!(extraData instanceof Ticket)) {
            return new Response(false, "Данные билета не переданы");
        }
        Ticket ticket = (Ticket) extraData;

        if (ticket.getId() == 0) {
            int newID = CreateID.createTicketID();
            ticket.setID(newID);
        }
        if (ticket.getVenue() != null && ticket.getVenue().getID() == 0) {
            ticket.getVenue().setID(CreateID.createVenueID());
        }
        collection.addElement(ticket);
        return new Response(true, "Данные билета переданы с ID: " + ticket.getId());
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