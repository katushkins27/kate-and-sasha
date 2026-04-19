package server.commands;

import common.commands.Command;
import common.network.Response;
import server.TicketCollection;
import server.util.Parser;

public class SaveCommand implements Command {
    private final String filename;
    private final TicketCollection collection;

    public SaveCommand(String filename, TicketCollection collection) {
        this.filename = filename;
        this.collection = collection;
    }
    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        try {
            Parser.saveFileToCSV(collection.getCollection(), filename);
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