package server.commands;

import common.commands.Command;
import common.network.Response;
import collection.TicketCollection;

public class SaveCommand implements Command {
    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        try {
            collection.saveFileToCSV();
            return new Response(true, "Коллекция сохранена в файл");
        } catch (Exception e) {
            return new Response(false, "Ошибка при сохранении файла " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Сохранить коллекцию в файл";
    }
    @Override
    public String getName() {
        return "save";
    }
    @Override
    public boolean requiresTicket() { return false; }
}