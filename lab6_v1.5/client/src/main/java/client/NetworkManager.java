package client;
import java.net.*;
import java.nio.channels.DatagramChannel;
import common.network.Request;
import common.network.Response;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class NetworkManager {
    private static final int max_retries=3;
    private static final int timeout = 5000;
    private DatagramChannel channel;
    private SocketAddress serverAddress;

    public NetworkManager (String host, int port) throws IOException{
        this.channel= DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.serverAddress = new InetSocketAddress(host, port);
    }

    public Response sendWithRetry(Request request) {
        for (int attempt = 0; attempt < max_retries; attempt++) {
            try {
                sendRequest(request);
                Response response = receiveWithTimeOut(timeout);
                if (response != null) return response;
                System.out.println("Время вышло, попытка " + (attempt + 1) + " из " + max_retries);
            } catch (Exception e) {
                System.out.println("Ошибка при обмене данными: " + e.getMessage());
            }
        }
        System.out.println("Превышено время ожидания ответа от сервера");
        return null;
    }

    private void sendRequest(Request request) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(request);
            oos.flush();
            channel.send(ByteBuffer.wrap(bos.toByteArray()), serverAddress);
        }
    }

    private Response receiveWithTimeOut(long time) throws IOException {
        long startTime = System.currentTimeMillis();
        long timeLeft = time;
        ByteBuffer buffer = ByteBuffer.allocate(65536);
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_READ);
            while (timeLeft > 0) {
                int rdyChannels = selector.select(timeLeft);
                if (rdyChannels > 0) {
                    selector.selectedKeys().clear();
                    SocketAddress receivedFrom = channel.receive(buffer);
                    if (receivedFrom != null && receivedFrom.equals(serverAddress)) {
                        buffer.flip();
                        byte[] responseData = new byte[buffer.remaining()];
                        buffer.get(responseData);
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
                             ObjectInputStream ois = new ObjectInputStream(bais)) {
                            return (Response) ois.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                timeLeft = time - (System.currentTimeMillis() - startTime);
            }
        }
        return null;
    }
    public void close() throws IOException {
        if (channel != null) channel.close();
    }
}

