package command;

import java.io.Serializable;

public class Command implements Serializable {
    public Command(Commands com)
    {
        current = com;
    }

    public Commands getCurrent() {
        return current;
    }

    public Object returnObj()
    {
        return null;
    }

    Commands current;

    @Override
    public String toString() {
        return "Command{" +
                "current=" + current +
                '}';
    }
}