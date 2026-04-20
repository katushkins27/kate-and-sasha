package server.commands;
import common.commands.Command;
import common.network.Response;
import server.TicketCollection;
import common.data.Ticket;

public class RemoveAllByPriceCommand implements Command {
    private TicketCollection collection;

    private Ticket findTicketByPrice(Long price) {
        for (Ticket ticket : collection.getCollection()) {
            Long ticketPrice = ticket.getPrice();
            if (price == null && ticketPrice == null) {
                return ticket;
            }
            if (price != null && price.equals(ticketPrice)) {
                return ticket;
            }
        }
        return null;
    }

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        try {
            Long price = (Long) extraData;
            int removed = collection.removeAllByPrice(price);
            return new Response(true, "Билеты удалены " + removed);
        } catch (NumberFormatException e) {
            return new Response(false, "Ошибка в цене. Введите число");
        }
    }


    @Override
    public String getDescription() {
        return "Удаление элементов из коллекции по заданной цене";
    }

    @Override
    public String getName() {
        return "remove_all_by_price";
    }

    @Override
    public boolean requiresTicket() { return false; }
}