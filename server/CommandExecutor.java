package server;
import com.sun.net.httpserver.Request;
import common.commands.Command;
import common.network.Response;
import common.network.Request;
import server.commands.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class CommandExecutor {
    private final TicketCollection collection;
    private final Map<String, Command> commands = new HashMap<>();

    public CommandExecutor(TicketCollection collection){
        this.collection=collection;
        addAllCommands();
    }
    private void addAllCommands(){
        commands.put('help',new HelpCommand(this));
        commands.put('info',new InfoCommand());
        commands.put('show',new ShowCommand());
        commands.put('add',new AddCommand());
        commands.put('update',new UpdateCommand());
        commands.put('remove_by_id',new RemoveByIDCommand());
        commands.put('clear',new ClearCommand());
        commands.put('head',new HeadCommand());
        commands.put('remove_head',new RemoveHeadCommand());
        commands.put('remove_greater',new RemoveGreaterCommand());
        commands.put('remove_all_by_price',new RemoveAllByPriceCommand());
        commands.put('remove_any_by_type',new RemoveAnyByTypeCommand());
        commands.put('min_by_venue',new MinByVenueCommand());
        commands.put('save',new SaveCommand());
    }

    public Map<String, Command> getCommands(){
        return commands;
    }
    public Response execute(Request request){
        String commandName = request.getCommandName();
        Command command = commands.get(commandName);
        if (command==null){
            return new Response(false, "Неизвестная команда: "+ commandName)
        }
        try {
            Object extraData = null;
            if(command.requiresTicket()){
                extraData = request.getTicket();
            } else if (request.getPrice()!=null){
                extraData = request.getPrice();
            } else if (request.getType()!=null){
                extraData = request.getType();
            }
            return command.execute(collection,request.getArg(),extraData);
        } catch (Exception e){
            return new Response(false, "Ошибка выполнения: "+e.getMessage());
        }
    }

}
