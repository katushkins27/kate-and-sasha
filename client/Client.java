package client;

import common.network.*;
import java.io.*;
import org.jline.reader.*;
import org.jline.terminal.*;

public class Client {

    private NetworkManager network;
    private RequestBuilder builder;
    private ScriptExecutor scriptExecutor;
    private LineReader reader;
    private boolean running = true;

    public Client (String host, int port){
        try {
            this.network = new NetworkManager(host, port);
            initConsole();
            this.builder = new RequestBuilder(reader);
            this.scriptExecutor = new ScriptExecutor(builder, network);
        } catch (IOException e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
        }
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
        System.out.println("Клиент запущен.");
        System.out.println("Введите 'help' для списка команд, 'exit' для выхода");
        while (running){
            try{
                String inputLine = reader.readLine("> ");
                if (inputLine == null || inputLine.trim().isEmpty()) continue;

                String[] parts = inputLine.trim().split("\\s+", 2);
                String command = parts[0].toLowerCase();
                String arg = parts.length > 1 ? parts[1] : "";

                if (command.equals("exit")) {
                    running = false; continue;
                }

                if (command.equals("execute_script")) {
                    scriptExecutor.executeScript(arg);
                } else {
                    Request request = builder.buildRequest(command, arg);
                    if (request != null) {
                        Response response = network.sendWithRetry(request);
                        printResponse(response);
                    }
                }
            } catch (UserInterruptException | EndOfFileException e) {
                break;
            }
        }
        stop();
    }

    private void printResponse(Response response) {
        if (response == null) System.err.println("Ошибка! Сервер не отвечает");
        else System.out.println(response.getMessage());
    }

    private void stop() {
        try {
            network.close();
            System.out.println("Сеанс завершен");
        } catch (IOException e) { e.printStackTrace(); }
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