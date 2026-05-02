package client;
import common.network.Request;
import common.network.Response;
import java.io.*;
import java.util.Scanner;
public class ScriptExecutor {
    private final RequestBuilder builder;
    private final NetworkManager network;

    public ScriptExecutor(RequestBuilder builder, NetworkManager network) {
        this.builder = builder;
        this.network = network;
    }

    public void executeScript(String filename) {
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            ConsoleReader.setScriptRegime(fileScanner);
            String commandLine;
            while ((commandLine = ConsoleReader.readNextCommand()) != null) {
                String[] parts = commandLine.split("\\s+", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1] : "";

                if (cmd.equals("execute_script")) {
                    System.out.println("Рекурсивный вызов скриптов ограничен логикой ConsoleReader");
                }

                Request request = builder.buildRequest(cmd, arg);
                if (request != null) {
                    Response response = network.sendWithRetry(request);
                    printResponse(response);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + filename);
        } finally {
            ConsoleReader.setInteractiveRegime();
        }
    }
    private void printResponse(Response response) {
        if (response == null) System.err.println("Сервер не отвечает");
        else System.out.println(response.getMessage());
    }
}
