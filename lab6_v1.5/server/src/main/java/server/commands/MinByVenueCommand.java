package server.commands;
import common.network.Response;
import common.data.TicketCollection;
import common.data.Ticket;
import common.commands.Command;

public class MinByVenueCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        Ticket ticket = collection.getMinByVenue();
        if (ticket == null) {
            return new Response(true, "Коллекция пустая");
        } else {
            return new Response(true, ticket.toString());
        }
    }

    @Override
    public String getDescription() {
        return "Билет с минимальным Venue";
    }


    @Override
    public String getName() {
        return "min_by_venue";
    }

    @Override
    public boolean requiresTicket() {
        return false; }
}