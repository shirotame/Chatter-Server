package chatter.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import java.net.Socket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;

public class ConnectedClient extends Thread {
    private String clientNickname;
    private String ip;
    private String countryName;
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public ConnectedClient(Socket socket, String nickname, String ip) throws IOException {
        this.client = socket;
        clientNickname = nickname;
        this.ip = ip;
        countryName = getCountryName(InetAddress.getByName(ip));
        in = new DataInputStream(this.client.getInputStream());
        out = new DataOutputStream(this.client.getOutputStream());
        start();
    }

    @Override
    public void run() {
        String msg;
        try {
            while (true) {
                msg = in.readUTF();
                if (msg.length() > 2500) {
                    break;
                }
                if (msg.equals("initmsg")) {
                    sendInitialMsg();
                }
                else if (msg.equals("dscmsg")) {
                    sendDisconnectMsg();
                }
                else {
                    formatAndSendMsg(msg);
                }
            }
        } catch (IOException e) {
            // waiting for message
        }
    }

    private String getCountryName(InetAddress address) {
        String name = "undefined";
        try {
            File database = new File("app\\src\\main\\resources\\GeoLite2-City.mmdb");
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            CityResponse response = reader.city(address);
            Country country = response.getCountry();
            name = country.getName();
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private void formatAndSendMsg(String msg) {
        System.out.println("Message from " + clientNickname + " (IP: " + ip + "): " + msg);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        for (ConnectedClient cl : Server.getClientList()) {
            if (cl == this) {
                StringBuilder bl = new StringBuilder();
                bl.append("(" + dateFormat.format(new Date()) + ")");
                bl.append(" " + clientNickname);
                bl.append(" (you): " + msg);
                cl.sendMessageToClient(bl.toString());
            }
            else {
                StringBuilder bl = new StringBuilder();
                bl.append("(" + dateFormat.format(new Date()) + ")");
                bl.append(" " + clientNickname);
                bl.append(": " + msg);
                cl.sendMessageToClient(bl.toString());
            }
        }
    }

    private void sendInitialMsg() {
        System.out.println(clientNickname + " connected to server. IP: " + ip + ". Country: " + countryName);
        sendMessageToEveryone(clientNickname + " connected. Country: " + countryName);
    }

    private void sendDisconnectMsg() {
        System.out.println(clientNickname + " disconnected from server. IP: " + ip + ". Country: " + countryName);
        sendMessageToEveryone(clientNickname + " disconnected. Country: " + countryName);
    }

    private void sendMessageToEveryone(String msg) {
        for (ConnectedClient cl : Server.getClientList()) {
            cl.sendMessageToClient(msg);
        }
    }

    private void sendMessageToClient(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException ignored) {
            // ignored
        }
    }
}
