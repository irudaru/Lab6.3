package program;/*
ОБЯЗАННОСТИ СЕРВЕРА
- Ожидать подключение или запрос
- Обработать полученную команду
- Отправить результат обработки клиенту
- Обмен данными по протоколу TCP
- Сохранить результат работы при завершении работы приложения в файл
- Сохранить результат работы в файл командой с сервера save(ТОЛЬКО ДЛЯ СЕРВЕРА)
 */

import command.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
ДАЛЕЕ БЕСПОЛЕЗНАЯ ИНФОРМАЦИЯ
    *   используя ServerSocket и Socket мы уже пользуемся протоколом TCP///
 */

public class Server {
    /*При инициализации объекта типа Socket,
     клиент, которому тот принадлежит, объявляет в сети,
     что хочет соединиться с сервером про определённому адресу и номеру порта.*/
    private static Socket clientSocket; //сокет для общения

    private static ServerSocket server; // серверсокет

    private static DataOutputStream dOut;
    private static DataInputStream dIn;

    public static void main(String[] args) {
        try {
            try  {
                server = new ServerSocket(1); // Говорим серверу прослушивать порт 1
                Writer.writeln("Сервер запущен!"); // Обратная связь
                clientSocket = server.accept(); // accept() будет ждать пока
                //кто-нибудь не захочет подключиться
                try {
                    dOut = new DataOutputStream(clientSocket.getOutputStream());
                    dIn = new DataInputStream(clientSocket.getInputStream());

                    Collection collection = Collection.startFromSave(args);

                    int length = dIn.readInt();
                    if(length>0) {
                        byte[] message = new byte[length];
                        dIn.readFully(message, 0, message.length); // read the message

                        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message));
                        Writer.writeln(message);
                        Writer.writeln(objectInputStream.readObject());
                        Command com = (Command) objectInputStream.readObject();
                        objectInputStream.close();

                        Writer w = CommanderServer.switcher(com, collection);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(w);
                        objectOutputStream.flush();

                        dOut.writeInt(byteArrayOutputStream.size());
                        dOut.write(byteArrayOutputStream.toByteArray());

                    }

                } finally { // в любом случае сокет будет закрыт
                    clientSocket.close();
                    // потоки тоже хорошо бы закрыть
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
    }
}