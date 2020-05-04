import Exceptions.FailedCheckException;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Класс, предотвращающий рекурсии
 */
public class RecursionHandler {
    private static Stack<String> usedFiles = new Stack<>();

    public static void addToFiles(String s) {
        usedFiles.push(s);
    }

    public static void removeLast() {
        usedFiles.pop();
    }

    public static boolean isContains(String s) {
        return usedFiles.search(s) == -1;
    }

    //private static String second;
    //private static int counter = -1;
    /*public static boolean SmartAdd(String s) {
        if (counter == -1) {
            for (String file : usedFiles) {
                if (file.equals(s)) {
                    second = s;
                    return StartCounter();//
                }
            }
            usedFiles.add(s);
            return true;
        } else if (!second.equals(s))
            return true;
        else if (counter > 1) {
            counter--;
            return true;
        }
        return false;
    }

    public static boolean StartCounter() {
        System.out.println("Найдено повторение файла!");
        counter = Console.console.handlerI("Введите количество повторений:", check);

        return counter > 0;
    }

    public static void resetIfChanged() {
        if (usedFiles.size() > 0) {
            usedFiles.clear();
            second = null;
            counter = -1;
        }
    }

    public static void resetCounter()//
    {
        counter = -1;
    }

    public static Checker<Integer> check = (Integer I) -> {
        if (I != null && I <= 10) return I;
        else throw new FailedCheckException();
    };*/
}