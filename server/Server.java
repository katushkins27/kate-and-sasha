package server;
import server.TicketCollection;
import common.network.Request;
import common.network.Response;
import java.net.*;
import java.util.logging.*;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final NetworkProvider network;
    private final CollectionManager collectionManager;
    private final CommandExecutor executor;
    private volatile boolean running = true;

    public Server(int port, String filename) throws SocketException {
        this.collectionManager = new CollectionManager(new TicketCollection(), filename);
        this.network = new NetworkProvider(port);
        this.executor = new CommandExecutor(collectionManager.getCollection(), filename);
    }

    public void start() {
        collectionManager.load();
        logger.info("Сервер запущен...");

        byte[] buffer = new byte[65507];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                Request request = network.receiveRequest(packet);

                logger.info("Запрос: " + request.getCommandName() + " от " + packet.getAddress());

                Response response = executor.execute(request);
                network.sendResponse(response, packet.getAddress(), packet.getPort());

            } catch (Exception e) {
                if (running) logger.severe("Ошибка цикла: " + e.getMessage());
            }
        }
        stop();
    }

    public void stop() {
        running = false;
        collectionManager.save();
        network.close();
        logger.info("Сервер остановлен.");
    }

    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Использование: java Server <port> <filename>");
            return;
        }
        try {
            Server server = new Server(Integer.parseInt(args[0]), args[1]);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();
        } catch (Exception e){
            e.printStackTrace();

        }
    }
}