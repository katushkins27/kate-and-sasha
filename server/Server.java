package server;

import common.data.Ticket;
import common.network.Request;
import common.network.Response;
import server.util.CreateID;
import server.util.Parser;
import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.logging.*;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private final TicketCollection collection;
    private final CommandExecutor executor;
    private DatagramSocket socket;
    private volatile boolean running = true;
    private final String filename;

    public Server(int port, String filename, TicketCollection collection){
        this.filename = filename;
        this.port = port;
        this.collection = collection;
        this.executor = new CommandExecutor(collection, filename);
        loadCollection();
    }
    private void loadCollection() {
        try {
            System.out.println("Загрузка из файла: " + filename);
            File file = new File(filename);
            System.out.println("Файл существует: " + file.exists());
            System.out.println("Размер файла: " + file.length());

            ArrayDeque<Ticket> loaded = Parser.parseFile(filename);
            System.out.println("Загружено билетов: " + loaded.size());

            for (Ticket ticket : loaded) {
                collection.addElement(ticket);
                CreateID.addTicketID(ticket.getId());
                if (ticket.getVenue() != null) {
                    CreateID.addVenueID(ticket.getVenue().getID());
                }
            }
        } catch (FileNotFoundException e) {
            logger.warning("Файл не найден: " + filename + ". Будет создана пустая коллекция.");
        } catch (IOException e) {
            logger.severe("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    public void saveCollection() {
        try {
            Parser.saveFileToCSV(collection.getCollection(), filename);
            logger.info("Коллекция сохранена в файл: " + filename);
        } catch (IOException e) {
            logger.severe("Ошибка сохранения коллекции: " + e.getMessage());
        }
    }

    public void start(){
        try{
            socket = new DatagramSocket(port);
            logger.info("Порт запущенного сервера " + port);
            byte[] recBuffer = new byte[65507];
            while (running){
                DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
                socket.receive(recPacket);

                InetAddress clientAddress = recPacket.getAddress();
                int clientPort = recPacket.getPort();
                logger.info("Получили запрос " + clientAddress + ", порт " + clientPort);

                byte[] reqData = Arrays.copyOf(recPacket.getData(), recPacket.getLength());
                Request request;
                try (ByteArrayInputStream bais = new ByteArrayInputStream(reqData);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    request = (Request) ois.readObject();
                    logger.info("Имя команды " + request.getCommandName());
                }

                Response response = executor.execute(request);
                byte[] respData;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)){
                    oos.writeObject(response);
                    respData = baos.toByteArray();
                }

                DatagramPacket sendPacket = new DatagramPacket(respData, respData.length, clientAddress, clientPort);
                socket.send(sendPacket);
                logger.info("Отправили ответ клиенту " + clientAddress + ", порт " + clientPort);
            }

        }catch (SocketException e){
            logger.severe("Ошибка сокета " + e.getMessage());
        } catch (IOException e){
            logger.severe("Ошибка ввода-вывода " + e.getMessage());
        } catch (ClassNotFoundException e){
            logger.severe("Ошибка десериализации " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            saveCollection();
            logger.info("Сервер остановился, коллекцию сохранили");
        }
    }
    public void stop(){
        running = false;
        if (socket != null){
            socket.close();
        }
    }
    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Использование: java Server <port> <filename>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String filename = args[1];
        Server server = new Server(port, filename, new TicketCollection());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Получили сигнал завершения!");
            server.saveCollection();
            System.out.println("Коллекция сохранена. Сервер остановлен.");
        }));
        server.start();
    }
}
