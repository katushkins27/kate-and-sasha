package client;
import common.data.Ticket;
import common.data.TicketType;
import common.network.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Scanner;
import org.jline.reader.Completer;
import org.jline.reader.Candidate;

import org.jline.reader.*;
import org.jline.terminal.*;

public class Client {
    private static final int max_retries=3;
    private static final int timeout = 5000;

    private final String host;
    private final int port;
    private DatagramChannel channel;
    private SocketAddress serverAddress;
    private LineReader reader;
    private boolean running = true;

    public Client (String host, int port){
        this.host = host;
        this.port= port;
        initConsole();
    }
    private void initConsole(){
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            Completer completer = (reader, line, candidates) -> {
                String buffer = line.line();
                String[] parts = buffer.split("\\s+");
                String prefix = parts[0].toLowerCase();
                String[] commands = {"add", "show", "update", "remove_by_id", "remove_head",
                        "head", "clear", "remove_greater", "remove_all_by_price",
                        "remove_any_by_type", "min_by_venue", "help", "info", "exit", "execute_script"};

                for (String cmd : commands) {
                    if (cmd.startsWith(prefix)) {
                        candidates.add(new Candidate(cmd));
                    }
                }
            };
            reader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
        } catch (IOException e) {
           System.out.println("Ошибка инициализации jline");
           reader = null;
        }
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
                String inputLine;
                if (reader != null){
                    try {
                        inputLine = reader.readLine("> ");
                        if (inputLine.isEmpty()) continue;
                        inputLine = inputLine.trim();
                    } catch (UserInterruptException e){
                        System.out.println("\nВыход");
                        break;
                    } catch (EndOfFileException e){
                        System.out.println("\nВыход");
                        break;
                    }
                } else {
                    System.out.print("> ");
                    inputLine = new java.util.Scanner(System.in).nextLine().trim();
                }
                if (inputLine.isEmpty()) continue;
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
                    running = false;
                    System.out.println("Сеанс завершен");
                    return null;
                }
            case "execute_script":
                if (arg.isEmpty()) {
                    //System.out.println("Использование: execute_script file_name");
                    return null;
                }
                executeScript(arg);
                return null;


            default:
                System.out.println("Неизвестная команда. Введите 'help' для списка доступных команд.");
                return null;
        }
    }

    private void executeScript(String filename) {
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            ConsoleReader.setScriptRegime(fileScanner);

            String commandLine;
            while ((commandLine = ConsoleReader.readNextCommand()) != null) {
                String[] parts = commandLine.split("\\s+", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1] : "";
                System.out.println("\nВыполняется: " + cmd + " " + arg);

                Request request = buildRequest(cmd, arg);
                if (request != null) {
                    Response response = sendWithRetry(request);
                    printResponse(response);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + filename);
        } finally {
            ConsoleReader.setInteractiveRegime();
        }
    }
    /**private Response sendWithRetry(Request request) throws IOException{
        for (int attempt = 0; attempt<max_retries; attempt++){
            try{
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(request);
                oos.flush();
                byte[] data = bos.toByteArray();
                channel.send(ByteBuffer.wrap(data),serverAddress);
                ByteBuffer buffer = ByteBuffer.allocate(65507);
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
                System.out.println("Время вышло, попытка "+(attempt+1) + " из " + max_retries);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            } catch (ClassNotFoundException e){
                System.err.println("Ошибка десериализации");
            }
        }
        return new Response(false,"Сервер недоступен, попробуйте позже");
    }
     **/
    private void sendRequest(Request request) throws IOException{
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(request);
            oos.flush();
            byte[] data = bos.toByteArray();
            channel.send(ByteBuffer.wrap(data), serverAddress);
        }
    }
    public Response sendWithRetry(Request request){
        for (int attempt = 0; attempt<max_retries; attempt++){
            try{
                sendRequest(request);
                Response responce = receiveWithTimeOut(timeout);
                if (responce != null){
                    return responce;
                }
                System.out.println("Время вышло, попытка "+(attempt+1) + " из " + max_retries);
            } catch (Exception e){
                System.out.println("Ошибка при обмене данными" + e.getMessage());
            }
        }
        System.out.println("Превышено время ожидания ответа от сервера");
        return null;
    }

    private Response receiveWithTimeOut(long time) throws IOException{
        long starTime = System.currentTimeMillis();
        long timeLeft = timeout;
        ByteBuffer buffer = ByteBuffer.allocate(65536);
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_READ);
            while (timeLeft > 0){
                int rdyChannels = selector.select(timeLeft);
                if (rdyChannels >0){
                    selector.selectedKeys().clear();
                    SocketAddress receivedFrom = channel.receive(buffer);
                    if (receivedFrom != null && receivedFrom.equals(serverAddress)){
                        buffer.flip();
                        byte[] responseData = new byte[buffer.remaining()];
                        buffer.get(responseData);
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
                        ObjectInputStream ois = new ObjectInputStream(bais)){
                            return (Response) ois.readObject();
                        } catch (ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
                timeLeft = timeout - (System.currentTimeMillis() - starTime);
            }
        }
        return null;
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