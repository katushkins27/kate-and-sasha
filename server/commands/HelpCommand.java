package server.commands;

import common.commands.Command;
import common.network.Response;
import server.CommandExecutor;
import server.TicketCollection;

public class HelpCommand implements Command {
    private final CommandExecutor executor;
    public HelpCommand(CommandExecutor executor){
        this.executor = executor;
    }
    @Override
    public Response execute(TicketCollection collection, String arg, Object extraData) {
        StringBuilder stringBuilder = new StringBuilder("Всевозможные команды:\n");
        for (Command cmd : executor.getCommands().values()) {
            stringBuilder.append("  ").append(cmd.getName()).append(" - ").append(cmd.getDescription()).append("\n");}
        stringBuilder.append("  exit - Завершение пользования клиентским модулем\n");
        return new Response(true, stringBuilder.toString());
    }

    @Override
    public String getDescription() {
        return "Вывод справочной информации по командам";
    }
    @Override
    public String getName() {
        return "help";
    }
    @Override
    public boolean requiresTicket() { return false; }
}