package server.commands;
import common.commands.Command;
import common.network.Response;
import common.data.TicketCollection;

public class RemoveAllByPriceCommand implements Command {
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