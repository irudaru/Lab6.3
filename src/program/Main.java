package program;

import command.Command;
import exceptions.EndOfFileException;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        /*final long start = System.nanoTime();
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                program.Writer.write("Программа завершает работу");
                program.Writer.write("\nПрограмм работала "+ (System.nanoTime() - start) / 1e9f +" сек.\n");//ctrl-c
                System.exit(0);
            }
        });*/

        Collection collection = Collection.startFromSave(args);

        try {
            boolean programIsWorking = true;
            String[] com;
            while (programIsWorking) {
                Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                com = AbstractReader.splitter(Console.console.read());
                programIsWorking = Commander.switcher(Console.console, collection, com[0], com[1]);
                //program.RecursionHandler.resetIfChanged();
            }
        } catch (EndOfFileException e) {
            Writer.writeln("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
        }



       /* try {
            String[] com;
            Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
            com = AbstractReader.splitter(Console.console.read());
            Command command = CommanderClient.switcher(Console.console, com[0], com[1]);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            Command command1 = (Command) objectInputStream.readObject();

            objectInputStream.close();
            Writer w = CommanderServer.switcher(command1, collection);
            w.writeAll();
        } catch (EndOfFileException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }*/


    }
}
