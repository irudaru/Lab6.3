package program;

import command.Command;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;



public class Server {

    private static Collection collection;
    public static void main(String[]args) {
        collection = Collection.startFromSave(args);
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", 1));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);

            InputStreamReader fileInputStream = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(fileInputStream);

            while (true) {
                if (bufferedReader.ready())
                    if(bufferedReader.readLine().equals("exit"))
                        break;
                    else
                        Writer.writeln("Неизвестная комманда.");

                if (selector.selectNow() <= 0)
                    continue;

                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {

                    SelectionKey key = iter.next();
                    iter.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        register(selector, key);
                    } else if (key.isWritable()) {
                        answer(buffer, key);
                    } else if (key.isReadable()) {
                        read(buffer, key);
                    }
                }
            }
            SaveManagement.saveToFile(collection);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void answer(ByteBuffer buffer, SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel client = (SocketChannel) key.channel();

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
        Command command = (Command) objectInputStream.readObject();
        objectInputStream.close();
        buffer.clear();
        Writer.writeln("Вызвана команада: " + command.getCurrent().toString());
        Writer w = CommanderServer.switcher(command, collection);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(w);

        buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

        client.write(buffer);
        while (buffer.hasRemaining()) {
            buffer.compact();
            client.write(buffer);
        }

        buffer.clear();
        objectOutputStream.flush();
        key.interestOps(SelectionKey.OP_READ);
    }

    private static void read(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            if (client.read(buffer) <= 0)
                throw new SocketException();
        } catch (SocketException e){
            client.close();
            buffer.clear();
            Writer.writeln("Connection closed...");
            Writer.writeln("Server will keep running. Try running another client to re-establish connection");
            return;
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void register(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Connection Accepted: " + client.getLocalAddress());
    }
}