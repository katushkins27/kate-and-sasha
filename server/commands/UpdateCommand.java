package server.commands;

import common.commands.Command;
import common.data.Ticket;
import common.network.Response;
import collection.TicketCollection;


public class UpdateCommand implements Command {
    @Override
    public Response execute(TicketCollection collection, String arg Object extraData) {
        try {
            int id = Integer.parseInt(arg);
            if (!(extraData instanceof Ticket)) {
                return new Response(false, "Данные билета не переданы");
            }
            Ticket updTicket = (Ticket) extraData;
            updTicket.setId(id);
            if (collection.update(id, updTicket)){
                return new Response(true, "Билет обновлен с ID" + id);
            } else{
                return new Response(false, "Билет не найден с ID" + id);
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка в ID. Введите число");
        }
    }

    @Override
    public String getDescription() {
        return "Обновление значения элемента коллекции по ID";
    }
    @Override
    public String getName() {
        return "update";
    }
    @Override
    public boolean requiresTicket() { return false; }
}