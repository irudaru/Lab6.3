package program;

import command.Command;
import command.Commands;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;


public class Server {

    private static Collection collection;
    public static void main(String[]args) {
        collection = Collection.startFromSave(args);
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", 5454));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {

                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        register(selector, serverSocket);
                    }

                    if (key.isReadable()) {
                        readWithAnswer(buffer, key);
                    }
                    iter.remove();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void readWithAnswer(ByteBuffer buffer, SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);

        String result = new String(buffer.array()).trim();
        if (result.length() <= 0) {
            client.close();
            buffer.clear();
            Writer.writeln("Connection closed...");
            Writer.writeln("Server will keep running. Try running another client to re-establish connection");
            return;
        }

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
        Command command = (Command) objectInputStream.readObject();
        objectInputStream.close();
        buffer.clear();

        Writer w = CommanderServer.switcher(command, collection);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(w);

        ByteBuffer buffer1 = ByteBuffer.allocate(1024*1024);
        buffer1.put(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        buffer1.flip();
        client.write(buffer1);
        buffer1.clear();
        objectOutputStream.flush();
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    /*public static void main(String[] args)
    {
        Collection collection = Collection.startFromSave(args);
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", 1));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            SelectionKey key;
            while (true) {
                if (selector.select() <= 0)
                    continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = serverSocket.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        Writer.writeln("Connection Accepted: " + sc.getLocalAddress() + "\n");
                    }
                    Command command = new Command(Commands.NON);
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
                        bb.clear();
                        sc.read(bb);

                        String result = new String(bb.array()).trim();
                        if (result.length() <= 0) {
                            sc.close();
                            Writer.writeln("Connection closed...");
                            Writer.writeln("Server will keep running. Try running another client to re-establish connection");
                            continue;
                        }

                        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bb.array()));
                        command = (Command) objectInputStream.readObject();
                        bb.clear();
                        objectInputStream.close();
                    }
                    if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
                        bb.clear();
                        Writer w = CommanderServer.switcher(command, collection);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(w);
                        objectOutputStream.flush();

                        bb = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                        bb.flip();
                        sc.write(bb);
                        bb.clear();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    /*private static ServerSocket server;

    private static DataOutputStream dOut;
    private static DataInputStream dIn;

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(1);
                Collection collection = Collection.startFromSave(args);
                Writer.writeln("Сервер запущен!");
                try (Socket clientSocket = server.accept()) {
                    dOut = new DataOutputStream(clientSocket.getOutputStream());
                    dIn = new DataInputStream(clientSocket.getInputStream());

                    while (true) {
                        int length = dIn.readInt();
                        if (length > 0) {
                            byte[] message = new byte[length];
                            dIn.readFully(message, 0, message.length);

                            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message));

                            Command command = (Command) objectInputStream.readObject();
                            objectInputStream.close();

                            Writer w = CommanderServer.switcher(command, collection);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(w);
                            objectOutputStream.flush();

                            dOut.writeInt(byteArrayOutputStream.size());
                            dOut.write(byteArrayOutputStream.toByteArray());
                        }
                    }
                } finally {
                    SaveManagement.saveToFile(collection);
                    dIn.close();
                    dOut.close();
                }
            } finally {
                Writer.writeln("Сервер закрыт!");
                server.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }*/
}