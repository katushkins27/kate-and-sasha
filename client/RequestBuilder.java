package client;
import common.data.*;
import common.network.Request;
import org.jline.reader.LineReader;
public class RequestBuilder {
    private final LineReader reader;

    public RequestBuilder(LineReader reader) {
        this.reader = reader;
    }

    public Request buildRequest(String command, String arg){
        switch (command){
            case "add":
                Ticket ticket;
                if (arg.isEmpty()) {
                    ticket = ConsoleReader.readTicket();
                } else {
                    ticket = ConsoleReader.readTicket(arg);
                }
                if (ticket == null) {
                    System.out.println("Не удалось создать билет. Команда отменена.");
                    return null;
                }
                return new Request(command, ticket);
            case "update":
                if (arg.isEmpty()){
                    System.out.println("Ошибка. Укажите ID для обновления");
                    return null;
                }
                System.out.println("Введите новые данные билета");
                Ticket updatedTicket = ConsoleReader.readTicket();
                if (updatedTicket == null) {
                    System.out.println("Не удалось создать билет. Команда отменена.");
                    return null;
                }
                return new Request(command, arg, updatedTicket);

            case "remove_greater":
                System.out.println("Введите билет для сравнения");
                Ticket compareTicket = ConsoleReader.readTicket();
                if (compareTicket == null) {
                    System.out.println("Не удалось создать билет для сравнения. Команда отменена.");
                    return null;
                }
                return new Request(command, compareTicket);

            case "remove_any_by_type":
                String typeInput;
                if (arg.isEmpty()) {
                    System.out.println(TicketType.AllDescriptions());
                    typeInput = reader.readLine().trim().toUpperCase();
                } else {
                    typeInput = arg.toUpperCase();
                }

                try {
                    common.data.TicketType type = common.data.TicketType.valueOf(typeInput);
                    return new Request(command, type);

                } catch (IllegalArgumentException e){
                    System.out.println("Ошибка! Неверный тип билета");
                    return null;
                }
            case "remove_all_by_price":
                String priceInput;
                if (arg.isEmpty()){
                    System.out.println("Введите цену билета:");
                    priceInput = reader.readLine().trim();
                } else {
                    priceInput = arg;
                }

                if (priceInput.isEmpty()) {
                    return new Request(command, (Long) null);
                }
                try {
                    Long price = Long.valueOf(priceInput);
                    //System.out.println(price);
                    return new Request(command, price);
                } catch (NumberFormatException e){
                    System.out.println("Ошибка! Цена должна быть числом!");
                    return null;
                }
            case "remove_by_id":
            case "head":
            case "remove_head":
            case "clear":
            case "show":
            case "info":
            case "help":
            case "min_by_venue":
                return new Request(command, arg);
            case "exit":
                if (ConsoleReader.isScriptRegime()) {
                    System.out.println("Скрипт прерван командой exit");
                    return null;
                } else {
                    System.out.println("Сеанс завершен");
                    return null;
                }
            case "execute_script":
                if (arg.isEmpty()) {
                    //System.out.println("Использование: execute_script file_name");
                    return null;
                }
                return null;

            default:
                System.out.println("Неизвестная команда. Введите 'help' для списка доступных команд.");
                return null;
        }
    }
}
