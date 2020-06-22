package program;

import exceptions.EndOfFileException;

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
    }
}