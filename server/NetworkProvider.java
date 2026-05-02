package server;
import common.network.Request;
import common.network.Response;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Logger;
public class NetworkProvider {
    private static final Logger logger = Logger.getLogger(NetworkProvider.class.getName());
    private final DatagramSocket socket;

    public NetworkProvider(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public Request receiveRequest(DatagramPacket packet) throws IOException, ClassNotFoundException {
        socket.receive(packet);
        byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (Request) ois.readObject();
        }
    }

    public void sendResponse(Response response, InetAddress address, int port) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            byte[] data = baos.toByteArray();
            socket.send(new DatagramPacket(data, data.length, address, port));
        }
    }

    public void close() {
        if (socket != null && !socket.isClosed()) socket.close();
    }
}
