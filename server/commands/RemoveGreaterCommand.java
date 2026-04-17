package server.commands;
import common.commands.Command;
import common.data.Ticket;
import common.network.Response;
import server.TicketCollection;
import common.data.Coordinates;
import common.data.Ticket;
import common.data.TicketType;
import common.data.Venue;
import to_use.CreateID;
import to_use.InputReader;

import java.time.LocalDateTime;

public class RemoveGreaterCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        if (!(extraData instanceof Ticket)){
            return new Response(false,"Не передан элемент для сравнения");
        }
        Ticket compareTicket = (Ticket) extraData;
        return new Response(true, "Элементы удалены");
    }


    @Override
    public String getDescription() {
        return "Удаление элементов из коллекции превышающие заданный";
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public boolean requiresTicket() { return true; }
}