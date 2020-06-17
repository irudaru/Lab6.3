package program;/*
ОБЯЗАННОСТИ КЛИЕНТА
- Подключиться к серверу
- Считать команду с консоли
- Валидировать эту команду(проверка на корректность вводимых команд)
- Команда должна представлять из себя объект класса, а не строку
- Сериализация объекта команды перед отправкой его на сервер
- Отправить команду на сервер
- Обмен данными по протоколу TCP
- Обработать ответ от сервера(и вывести резульатат в консоль)
- Завершить работу клиента при команде exit
 */
import command.Command;
import exceptions.EndOfFileException;

import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket clientSocket; //сокет для общения
    private static DataOutputStream dOut;
    private static DataInputStream dIn;

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", 1);

                dOut = new DataOutputStream(clientSocket.getOutputStream());
                dIn = new DataInputStream(clientSocket.getInputStream());

                try {
                    String[] com;
                    while (true) {
                        Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                        com = AbstractReader.splitter(Console.console.read());
                        Command command = CommanderClient.switcher(Console.console, com[0], com[1]);
                        if (command == null)
                            continue;

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(command);
                        objectOutputStream.flush();

                        dOut.writeInt(byteArrayOutputStream.size());
                        dOut.write(byteArrayOutputStream.toByteArray());

                        int length = dIn.readInt();
                        if (length > 0) {
                            byte[] message = new byte[length];
                            dIn.readFully(message, 0, message.length); // read the message
                            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message));
                            Writer w = (Writer) objectInputStream.readObject();
                            w.writeAll();
                            objectInputStream.close();
                        }
                    }

                } catch (EndOfFileException e) {
                    Writer.writeln("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } finally { // в любом случае необходимо закрыть сокет и потоки
                Writer.writeln("Клиент был закрыт...");
                clientSocket.close();
                dOut.close();
                dIn.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}