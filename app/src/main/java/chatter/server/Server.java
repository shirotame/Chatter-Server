package chatter.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;

public class Server {
    private static List<ConnectedClient> clientList = new LinkedList<>();

    private Server() {}

    public static List<ConnectedClient> getClientList() {
        return clientList;
    }

    public static void deleteClientSocket(ConnectedClient cl) {
        clientList.remove(cl);
    }

    public static void runServer(final int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket() ) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            
            System.out.println("\nServer started on port " + port + ": " + new Date());
            while (true) {
                Socket newClient = serverSocket.accept();
                if (newClient != null) {
                    System.out.println("Someone is trying to connect.");
                    DataInputStream d = new DataInputStream(newClient.getInputStream());
                    if (d.readByte() != 1) {
                        newClient.close();
                        System.out.println("Socket didn't responsed correctly, connection closed");
                    }
                    else {
                        String n = d.readUTF();
                        if (n.length() > 52) {
                            n = n.substring(0, 51);
                        }
                        String ip = d.readUTF();
                        try {
                            clientList.add(new ConnectedClient(newClient, n, ip));
                        } catch (IOException e) {
                            newClient.close();
                        }
                    }
                }
            }
        }
    }
}
