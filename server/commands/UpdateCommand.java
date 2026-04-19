package server.commands;

import common.commands.Command;
import common.data.Ticket;
import common.network.Response;
import server.TicketCollection;
import server.util.CreateID;


public class UpdateCommand implements Command {
    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        if (arg.isEmpty()) {
            return new Response(false, "Укажите ID для обновления");
        }
        try {
            int id = Integer.parseInt(arg);
            Ticket oldTicket = findTicketById(collection, id);
            if (oldTicket == null) {
                return new Response(false, "Билет с ID " + id + " не найден");
            }

            Ticket updTicket = (Ticket) extraData;
            updTicket.setID(id);
            if (updTicket.getVenue() != null && updTicket.getVenue().getID() == 0) {
                updTicket.getVenue().setID(CreateID.createVenueID());
            }
            if (collection.update(id, updTicket)) {
                return new Response(true, "Билет обновлен с ID" + id);
            } else {
                return new Response(false, "Билет не найден с ID" + id);
            }

        } catch (NumberFormatException e) {
            return new Response(false,"Ошибка в ID. Введите число ");
        }
    }
    private Ticket findTicketById(TicketCollection collection, int id) {
        for (Ticket ticket : collection.getCollection()) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "Обновление значения элемента коллекции по ID ";
    }
    @Override
    public String getName() {
        return "update";
    }
    @Override
    public boolean requiresTicket() { return true; }
}