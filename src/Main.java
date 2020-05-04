import Exceptions.EndOfFileException;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Main {
    public static void main(String[] args) {
        final long start = System.nanoTime();
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Программа завершает работу");
                //if (Console.Console.HandlerB("Введите Bool: ", Command.boolCheck)) {
                System.out.format("\nПрограмм работала %f сек.\n", (System.nanoTime() - start) / 1e9f);//ctrl-c
                System.exit(0);
                //}
            }
        });

        Collection collection = Collection.startFromSave(args);

        try {
            boolean programIsWorking = true;
            String[] com;
            while (programIsWorking) {
                System.out.print("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                com = AbstractReader.splitter(Console.console.read());
                programIsWorking = Commander.switcher(Console.console, collection, com[0], com[1]);
                //RecursionHandler.resetIfChanged();
            }
        } catch (EndOfFileException e) {
            System.out.println("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
        }
    }
}
