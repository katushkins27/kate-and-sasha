package server.commands;
import common.commands.Command;
import common.network.Response;
import server.TicketCollection;
import common.data.Ticket;
import common.data.TicketType;


public class RemoveAnyByTypeCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        if (extraData instanceof TicketType){
            TicketType type = (TicketType) extraData;
            if (collection.removeByType(type)){
                return new Response(true, "Удален элемент с типом: "+type);
            } else {
                return new Response(false, "Элементы с типом "+type+" не найдены");
            }
        }
        return new Response(false, "Неправильно указан тип");
    }


    @Override
    public String getDescription() {
        return "Удаление элемента из коллекции по типу";
    }

    @Override
    public String getName() {
        return "remove_any_by_type";
    }
    @Override
    public boolean requiresTicket() { return false; }
}