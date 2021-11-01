package org.white5moke;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class NodeTransciever extends Thread {
    private static final int port = 5556;
    private byte[] buffer = new byte[16];
    private DatagramSocket socket;
    private List<InetAddress> remotes = new ArrayList<>();
    private Date localTime;
    private DatagramPacket query;
    private List<DatagramPacket> responses = new ArrayList<>();
    private int responsesSent = 0;

    public NodeTransciever(String[] hosts) {
        List<String> l = Arrays.stream(hosts).toList();
        l.forEach((host) -> {
            try {
                remotes.add(InetAddress.getByName(host));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        getTimes();
    }

    private void getTimes() {
        remotes.forEach((remote) -> {
            query = new DatagramPacket(buffer, buffer.length, remote, port);
            try {
                socket.send(query);
                responsesSent++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            localTime = new Date();

            try {
                socket.setSoTimeout(10000);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            responses.add(response);
        });
    }

    @Override
    public synchronized void start() {
        //super.start();

    }
}

public class Node {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private int port = 5555;
    private boolean isRunning = false;
    private byte[] buffer = new byte[16];
    private InetAddress clientAddress;
    private int clientPort;

    public Node() throws IOException {}

    public void start() throws IOException {
        isRunning = true;
        socket = new DatagramSocket(port);

        while(isRunning) {
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            clientAddress = packet.getAddress();
            clientPort = packet.getPort();

            /**
             * place the time in a byte array
             */
            buffer = getTimeBuffer();

            /**
             * create and send the packet data
             */
            packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(packet);
        }
        socket.close();
    }

    protected void stop() {
        isRunning = false;
    }

    private byte[] getTimeBuffer() {
        SimpleDateFormat dtf = new SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm:ss");
        String ts = dtf.format(new Date());

        return ts.getBytes(StandardCharsets.UTF_8);
    }
}
