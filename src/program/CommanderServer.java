package program;

import command.Command;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Класс - обработчик команд с консоли
 */

public class CommanderServer {
    /**
     * Обработка команд, вводимых с консоли
     */
    public static Writer switcher(Command com, Collection c) {
        switch (com.getCurrent()) {
            case HELP:
                return help();
            case INFO:
                return info(c);
            case SHOW:
                return show(c);
            case ADD:
                return add(c, com);
            case UPDATE:
                return update(c, com);
            case REMOVE_BY_ID:
                return removeById(c, com);
            case CLEAR:
                return clear(c);
            case SAVE:
                return save(c);
            case EXECUTE_SCRIPT:
                return executeScript(c, com);
            case ADD_IF_MIN:
                return addIfMin(c, com);
            case REMOVE_GREATER:
                return removeGreater(c, com);
            case REMOVE_LOWER:
                return removeLower(c, com);
            case AVERAGE_OF_DISTANCE:
                return averageOfDistance(c);
            case MIN_BY_CREATION_DATE:
                return minByCreationDate(c);
            case PRINT_FIELD_ASCENDING_DISTANCE:
                return printFieldAscendingDistance(c);
            default:
                Writer.writeln("Такой команды нет");
        }
        return null;
    }

    /**
     * Показывает информацию по всем возможным командам
     */
    public static Writer help() {
        Writer w = new Writer();
        w.addToList(true,
                "help : вывести справку по доступным командам\n" +
                        "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "add {element} : добавить новый элемент в коллекцию\n" +
                        "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_by_id id : удалить элемент из коллекции по его id\n" +
                        "clear : очистить коллекцию\n" +
                        "save : сохранить коллекцию в файл\n" +
                        "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "exit : завершить программу (без сохранения в файл)\n" +
                        "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                        "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                        "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                        "average_of_distance : вывести среднее значение поля distance для всех элементов коллекции\n" +
                        "min_by_creation_date : вывести любой объект из коллекции, значение поля creationDate которого является минимальным\n" +
                        "print_field_ascending_distance : вывести значения поля distance в порядке возрастания"
        );
        return w;
    }

    /**
     * Показывает информацию о коллекции
     */
    public static Writer info(Collection collection) {
        Writer w = new Writer();
        w.addToList(true,"Тип коллекции: " + collection.list.getClass().getName());
        w.addToList(true,"Колличество элементов: " + collection.list.size());
        w.addToList(true,"Коллеция создана: " + collection.getDate());
        return w;
    }

    /**
     * Выводит значения поля distance в порядке возрастания
     */
    public static Writer printFieldAscendingDistance(Collection c) {
        Writer w = new Writer();
        if (c.list.size() > 0) {
            LinkedList<Long> distances = new LinkedList<>();
            //c.list.stream().filter(r -> r.getDistance() != null).forEach(r -> distances.add(r.getDistance()));
            for (Route r : c.list) {
                if (r.getDistance() != null)
                    distances.add(r.getDistance());
            }
            Collections.sort(distances);
            for (Long dis : distances) {
                w.addToList(true, dis);
            }
            return w;
        }
        w.addToList(true,"В коллекции нет элементов");
        return w;
    }

    /**
     * выводит объект из коллекции, значение поля creationDate которого является минимальным
     */
    public static Writer minByCreationDate(Collection c) {
        Writer w = new Writer();
        if (c.list.size() > 0) {
            Route minR = c.list.get(0);
            for (Route r : c.list) {
                if (r.getCreationDate().compareTo(minR.getCreationDate()) < 0)
                    minR = r;
            }
            w.addToList(true, minR.toString());
            return w;
        }
        w.addToList(true, "В коллекции нет элементов");
        return w;
    }

    /**
     * Выводит среднее значение поля distance
     */
    public static Writer averageOfDistance(Collection c) {
        Writer w = new Writer();
        if (c.list.size() > 0) {
            long sum = 0L;
            int count = 0;
            for (Route r : c.list) {
                if (r.getDistance() != null)
                    sum += r.getDistance();
                else
                    count++;
            }
            if (c.list.size() - count > 0)
                w.addToList(true,"Среднее значение distance: " + sum / (c.list.size() - count));
            return w;
        }
        w.addToList(true,"В коллекции нет элементов");
        return w;
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static Writer removeLower(Collection c, Command com) {
        Writer w = new Writer();
        int id = c.getRandId();
        Route newRoute = RouteWithId((Route) com.returnObj(), id);
        int size = c.list.size();
        int i = 0;
        while (i < size) {
            if (c.list.get(i).compareTo(newRoute) < 0) {
                w.addToList(true, "Удален элемент с id: " + c.list.get(i).getId());
                c.list.remove(c.list.get(i));
                size -= 1;
                i -= 1;
            }
            i++;
        }
        Collections.sort(c.list);
        return w;
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static Writer removeGreater(Collection c, Command com)  {
        Writer w = new Writer();
        int id = c.getRandId();
        Route newRoute = RouteWithId((Route) com.returnObj(), id);
        int size = c.list.size();
        int i = 0;
        while (i < size) {
            if (c.list.get(i).compareTo(newRoute) > 0) {
                w.addToList(true, "Удален элемент с id: " + c.list.get(i).getId());
                c.list.remove(c.list.get(i));
                size -= 1;
                i -= 1;
            }
            i++;
        }
        Collections.sort(c.list);
        return w;
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static Writer addIfMin(Collection c, Command com) {
        Writer w = new Writer();
        int id = c.getRandId();
        Route newRoute = RouteWithId((Route) com.returnObj(), id);
        if (newRoute.compareTo(c.list.getFirst()) < 0) {
            c.list.add(newRoute);
            w.addToList(true, "Элемент успешно добавлен");
        } else w.addToList(true,"Элемент не является минимальным в списке");
        Collections.sort(c.list);
        return w;
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public static Writer executeScript(Collection c, Command com) {
        Writer w = new Writer();
        /*boolean programIsWorking = true;
        //program.Reader reader;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                Writer.write("\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                String line = reader.read();
                while (line != null && programIsWorking) {
                    com = AbstractReader.splitter(line);
                    programIsWorking = Commander.switcher(reader, c, com[0], com[1]);
                    Writer.write("\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                    line = reader.read();
                }
                RecursionHandler.removeLast();
            } else
                Writer.writeln("\u001B[31m" + "Найдено повторение" + "\u001B[0m");

        } catch (IncorrectFileNameException e) {
            Writer.writeln("\u001B[31m" + "Неверное имя файла" + "\u001B[0m");
        } catch (EndOfFileException e) {
            Writer.writeln("\u001B[31m" + "Неожиданный конец файла " + s + "\u001B[0m");
            RecursionHandler.removeLast();
        } catch (FileNotFoundException e) {
            Writer.writeln("\u001B[31m" + "Файл не найден" + "\u001B[0m");
        }
        return programIsWorking;*/
        return w;
    }

    /**
     * Сохраняет коллекцию в фаил
     */
    public static Writer save(Collection c) {
        Writer w = new Writer();
        SaveManagement.saveToFile(c);
        w.addToList(true, "Файл сохранен");
        return w;
    }

    /**
     * Удаляет все элементы из коллекции
     */
    public static Writer clear(Collection c) {
        Writer w = new Writer();
        c.list.clear();
        w.addToList(true, "Коллекция очищена");
        return w;
    }

    /**
     * Удаляет все элементы по его id
     */
    public static Writer removeById(Collection c, Command com) {
        Writer w = new Writer();
        Integer id = (Integer) com.returnObj();
        Route r = c.searchById(id);
        if (r == null) {
            w.addToList(true,"Такого элемента нет");
            return w;
        }
        c.list.remove(r);
        w.addToList(true,"Элемент с id: " + id +" успешно удален");
        Collections.sort(c.list);

        return w;
    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static Writer update(Collection c, Command com){
        Writer w = new Writer();
        int id = ((Route) com.returnObj()).getId();
        Route r = c.searchById(id);
        if (r == null) {
            w.addToList(true,"Такого элемента нет");
            return w;
        }
        c.list.set(c.list.indexOf(r), (Route) com.returnObj());
        Collections.sort(c.list);
        w.addToList(true, "Элемент с id: " + id + " успешно изменен");
        return w;
    }

    /**
     * Выводит все элементы списка
     */
    public static Writer show(Collection c) {
        Writer w = new Writer();
        if (c.list.isEmpty()) {
            w.addToList(true,"В коллекции нет элементов");
            return w;
        }
        for (Route r : c.list) {
            w.addToList(true, r.toString());
        }
        return w;
    }

    /**
     * Добавляет элемент в список
     */
    public static Writer add(Collection c, Command com){
        Writer w = new Writer();
        int id = c.getRandId();
        c.list.add(RouteWithId((Route) com.returnObj(), id));
        Collections.sort(c.list);
        w.addToList(true, "Элемент с id: " + id + " успешно добавлен");
        return w;
    }

    public static Route RouteWithId(Route r, int id)
    {
        r.setId(id);
        return r;
    }
}

