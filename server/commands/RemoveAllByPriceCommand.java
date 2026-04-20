package server.commands;
import common.commands.Command;
import common.network.Response;
import server.TicketCollection;
import common.data.Ticket;

public class RemoveAllByPriceCommand implements Command {

    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        Long price = null;
        if(extraData instanceof Long){
            price = (Long) extraData;
        } else if (arg!=null && !arg.isEmpty()){
            try{
                price=Long.parseLong(arg);
            } catch (NumberFormatException e){
                return new Response(false, "Ошибка в цене. Введите число");
            }
        }
        int removed = collection.removeAllByPrice(price);
        if (removed==0){
            return new Response(true, "Билеты с ценой "+price+" не найдены");
        } else {
            return new Response(true, "Билеты удалены. Количество удаленных билетов: "+removed);
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
