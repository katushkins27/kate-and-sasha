package server.commands;
import common.commands.Command;
import common.data.Ticket;
import common.network.Response;
import server.TicketCollection;


public class RemoveHeadCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        Ticket ticket = collection.head();
        if (ticket == null) {
            return new Response(true, "Коллекция пустая");
        } else {
            collection.removeHead();
            return new Response(true, "Удален первый элемент коллекции");
        }
    }

    @Override
    public String getDescription() {
        return "Удаление первого элемента коллекции";
    }

    @Override
    public String getName() {
        return "remove_head";
    }

    @Override
    public boolean requiresTicket() { return false; }
}