package client;
import com.sun.net.httpserver.Request;
import common.network.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
public class Client {
    private static final int max_retries=3;
    private static final int timeout = 5000;

    private final String host;
    private final int port;
    private DatagramChannel channel;
    private SocketAddress serverAddress;
    private Scanner scanner;

    public Client (String host, int port){
        this.host = host;
        this.port= port;
        this.scanner = new Scanner(System.in);
    }
    public void start(){
        try{
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            serverAddress = new InetSocketAddress(host, port);
             System.out.println("Клиент запущен. Подключение к "+host+":"+port);
             System.out.println("Введите 'help' для списка команд, 'exit' для выхода");
             boolean running = true;
             while (running){
                 System.out.print("> ");
                 String inputLine = scanner.nextLine().trim();
                 if (inputLine.isEmpty()) continue;
                 if (inputLine.equalsIgnoreCase("exit")){
                     running = false;
                     System.out.println("Сеанс завершен");
                     break;
                 }
                 processCommand(inputLine);
             }
        } catch (IOException e){
            System.err.println("Ошибка клиента: "+e.getMessage());
        } finally {
            try {
                if (channel != null) channel.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void processCommand(String inputLine){
        String[] parts = inputLine.split(" ",2);
        String command = parts[0].toLowerCase();
        String arg = parts.length>1 ? parts[1] : "";
        try{
            Request request = buildRequest(command, arg);
            if (request==null) return;
            Response response = sendWithRetry(request);
            printResponse(response);

        } catch (Exception e){
            System.err.println("Ошибка: "+e.getMessage());
        }
    }

    private Request buildRequest(String command, String arg){
        switch (command){
            case "add":
                System.out.println("Введите данные билета: ");
                return new Request(command, arg, ConsoleReader.readTicket());
            case "update":
                if (arg.isEmpty()){
                    System.out.println("Ошибка. Укажите ID для обновления");
                    return null;
                }
                System.out.println("Введите новые данные билета");
                return new Request(command, arg, ConsoleReader.readTicket());
            case "remove_greater":
                System.out.println("Введите билет для сравнения");
                return new Request(command, ConsoleReader.readTicket());
            case "remove_any_by_type":
                if (arg.isEmpty()){
                    System.out.println("Введите тип билета: VIP, USUAL, BUDGETARY, CHEAP");
                    String typeInput = scanner.nextLine().trim().toUpperCase();
                    try {
                        return new Request(command, common.data.TicketType.valueOf(typeInput));
                    } catch (IllegalArgumentException e){
                        System.out.println("Ошибка! Неверный тип билета");
                        return null;
                    }
                }
                try {
                    return new Request(command, common.data.TicketType.valueOf(arg.toUpperCase()));
                } catch (IllegalArgumentException e){
                    System.out.println("Ошибка! Неверный тип билета");
                    return null;
                }
            case "remove_all_by_price":
                if (arg.isEmpty()){
                    System.out.println("Введите цену билета:");
                    String priceInput = scanner.nextLine().trim();
                    if (priceInput.isEmpty()){
                        return new Request(command, (Long) null);
                    }
                    try {
                        return new Request(command, Long.parseLong(priceInput));
                    } catch (NumberFormatException e){
                        System.out.println("Ошибка! Цена должна быть числом!");
                        return null;
                    }
                }
                try {
                    return new Request(command, arg.isEmpty() ? null : Long.parseLong(arg));
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

            default:
                System.out.println("Неизвестная команда. Введите 'help' для списка доступных команд.");
                return null;
        }
    }
    private Response sendWithRetry(Request request) throws IOException{
        for (int attempt = 0; attempt<max_retries; attempt++){
            try{
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(request);
                oos.flush();
                byte[] data = bos.toByteArray();
                channel.send(ByteBuffer.wrap(data),serverAddress);
                ByteBuffer buffer = ByteBuffer.allocate(65507);
                channel.socket().setSoTimeout(timeout);
                long startime = System.currentTimeMillis();
                while (System.currentTimeMillis()-startime<timeout){
                    SocketAddress receivedFrom = channel.receive(buffer);
                    if (receivedFrom != null && receivedFrom.equals(serverAddress)){
                        buffer.flip();
                        byte[] responseData = new byte[buffer.remaining()];
                        buffer.get(responseData);

                        ByteArrayInputStream bis = new ByteArrayInputStream(responseData);
                        ObjectInputStream ois = new ObjectInputStream(bis);
                        return (Response) ois.readObject();
                    }
                    Thread.sleep(100);
                }
                System.out.println("Время вышло, попытка "+(attempt+1)+" из "+max_retries);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            } catch (ClassNotFoundException e){
                System.err.println("Ошибка десериализации");
            }
        }
        return new Response(false,"Сервер недоступен, попробуйте позже");
    }
    private void printResponse(Response response){
        if (response==null){
            System.err.println("Ошибка! Сервер не отвечает");
            return;
        }
        if (response.isSuccess()){
            System.out.println(response.getMessage());
        } else {
            System.err.println("Ошибка: "+response.getMessage());
        }
    }

    //здесь мэйн надо это глянуть!!!
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        Client client = new Client(host, port);
        client.start();
    }
}
