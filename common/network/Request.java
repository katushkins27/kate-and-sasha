package common.network;

import common.data.*;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String commandName;
    private final String arg;
    private final Ticket ticket;
    private final TicketType type;
    private final Long price;

    public Request(String commandName){
        this(commandName, null, null, null, null);
    }
    public Request(String commandName, String arg){
        this(commandName, arg, null, null, null);
    }
    public Request(String commandName, Ticket ticket){
        this(commandName, null, ticket, null, null);
    }
    public Request(String commandName, Long price){
        this(commandName, null, null, price, null);
    }
    public Request(String commandName, TicketType type){
        this(commandName, null, null, null, type);
    }
    public Request(String commandName, String arg, Ticket ticket) {
        this(commandName, arg, ticket, null, null);
    }
    public Request(String commandName, Ticket ticket, boolean isCompareTicket) {
        this(commandName, null, ticket, null, null);
    }

    private Request(String commandName, String arg, Ticket ticket, Long price, TicketType type){
        this.commandName = commandName;
        this.arg = arg;
        this.ticket = ticket;
        this.price = price;
        this.type = type;
    }
    public String getCommandName(){return commandName;}
    public String getArg(){return arg;}
    public Ticket getTicket(){return ticket;}
    public Long getPrice(){return price;}
    public TicketType getType() {return type;}
}
