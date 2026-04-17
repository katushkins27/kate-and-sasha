package server;

import common.network.Request;
import common.network.Response;
import collection.TicketCollection;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.*;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private final TicketCollection collection;
    private final CommandExecutor executor;
    private DatagramSocket socket;
    private volatile boolean running = true;

    public Server(int port, String filename){
        this.executor = new CommandExecutor();
        this.port = port;
        this.collection = new TicketCollection(filename);
    }
    public void start(){
        try{
            socket = new DatagramSocket(port);
            logger.info("Порт запущенного сервера" + port);
            byte[] recBuffer = new byte[65507];
            while (running){
                DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
                socket.receive(recPacket);

                InetAddress clientAddress = recPacket.getAddress();
                int clientPort = recPacket.getPort();
                logger.info("Получили запрос" + clientAddress + ", порт" + clientPort);

                byte[] reqData = Arrays.copyOf(recPacket.getData(), recPacket.getLength());
                Request request;
                try (ByteArrayInputStream bais = new ByteArrayInputStream(reqData);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    request = (Request) ois.readObject();
                    logger.info("Имя команды" + request.getCommandName());
                }

                Response response = executor.execute(request, collection);
                byte[] respData;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)){
                    oos.writeObject(response);
                    respData = baos.toByteArray();
                }

                DatagramPacket sendPacket = new DatagramPacket(respData, respData.length, clientAddress, clientPort);
                socket.send(sendPacket);
                logger.info("Отправили ответ клиенту" + clientAddress + ", порт" + clientPort);
            }

        }catch (SocketException e){
            logger.severe("Ошибка сокета" + e.getMessage());
        } catch (IOException e){
            logger.severe("Ошибка ввода-вывода" + e.getMessage());
        } catch (ClassNotFoundException e){
            logger.severe("Ошибка десериализации" + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            collection.saveFileToCSV();
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
        Server server = new Server(port, filename);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}
