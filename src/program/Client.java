package program;

import command.Command;
import command.Commands;
import exceptions.EndOfFileException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Client {

    public static void main(String[] args)
    {
        try {
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("localhost"), 1);
            Selector selector = Selector.open();
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(addr);
            sc.register(selector, SelectionKey.OP_CONNECT /*| SelectionKey.OP_READ | SelectionKey.OP_WRITE*/);
            try {
                while (true) {
                    if (selector.select() > 0) {
                        Boolean doneStatus = process(selector);
                        if (doneStatus) {
                            break;
                        }
                    }
                    //Writer.writeln("3");
                }
            } catch ( EndOfFileException e) {
                Writer.writeln("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
            } catch (SocketException e) {
                Writer.writeln("Сервер недоступен.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Writer.writeln("Клиент был закрыт...");
            sc.close();
        } catch (IOException e) {
            Writer.writeln("Сервер недоступен!");
        }
    }
    public static Boolean process(Selector selector) throws IOException, EndOfFileException, ClassNotFoundException {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isConnectable()) {
                Boolean connected = processConnect(selector, key);
                if (!connected) {
                    return true;
                }
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer bb = ByteBuffer.allocate(1024*1024);
                bb.clear();

                sc.read(bb);

                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bb.array()));

                Writer w = (Writer) objectInputStream.readObject();
                if (w != null)
                    w.writeAll();
                else
                    Writer.writeln("Что-то не так");
                key.interestOps(SelectionKey.OP_WRITE);
            }
            if (key.isWritable()) {

                Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                String[] com = AbstractReader.splitter(Console.console.read());
                Command command = CommanderClient.switcher(Console.console, com[0], com[1]);

                if (command == null)
                    return false;
                else if (command.getCurrent() == Commands.EXIT)
                    return true;

                SocketChannel sc = (SocketChannel) key.channel();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(command);
                objectOutputStream.flush();

                ByteBuffer bb = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                sc.write(bb);
                bb.clear();

                key.interestOps(SelectionKey.OP_READ);
            }
        }
        return false;
    }
    public static Boolean processConnect(Selector selector, SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }
}