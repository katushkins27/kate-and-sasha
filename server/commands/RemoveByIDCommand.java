package server.commands;
import common.commands.Command;
import common.network.Response;
import server.TicketCollection;
import common.data.Ticket;
import to_use.CreateID;

public class RemoveByIDCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        try {
            int id = Integer.parseInt(arg);
            if (collection.removeById(id)){
                return new Response(true, "Билет с ID "+id+" удален");
            } else {
                return new Response(false, "Билет с ID "+id+"не найден");
            }
        } catch (NumberFormatException e) {
            return new Response(false, "Ошибка в ID. Введите число");
        }
    }

    @Override
    public String getDescription() {
        return "Удаление элемента из коллекции по ID";
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public boolean requiresTicket() { return false; }
}