/*
ОБЯЗАННОСТИ СЕРВЕРА
- Ожидать подключение или запрос
- Обработать полученную команду
- Отправить результат обработки клиенту
- Обмен данными по протоколу TCP
- Сохранить результат работы при завершении работы приложения в файл
- Сохранить результат работы в файл командой с сервера save(ТОЛЬКО ДЛЯ СЕРВЕРА)
 */

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

    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static void main(String[] args) {
        try {
            try  {
                server = new ServerSocket(1); // Говорим серверу прослушивать порт 1
                System.out.println("Сервер запущен!"); // Обратная связь
                clientSocket = server.accept(); // accept() будет ждать пока
                //кто-нибудь не захочет подключиться
                try { // установив связь и воссоздав сокет для общения с клиентом можно перейти
                    // к созданию потоков ввода/вывода.
                    // теперь мы можем принимать сообщения
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // и отправлять
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                    System.out.println(word);
                    // не долго думая отвечает клиенту
                    out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                    out.flush(); // выталкиваем все из буфера

                } finally { // в любом случае сокет будет закрыт
                    clientSocket.close();
                    // потоки тоже хорошо бы закрыть
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("Сервер закрыт!");
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}