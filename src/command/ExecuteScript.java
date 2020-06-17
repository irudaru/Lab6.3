package command;

import program.Route;

public class ExecuteScript extends Command {
    public ExecuteScript(String script) {
        super(Commands.EXECUTE_SCRIPT);
        this.script = script;
    }

    @Override
    public String returnObj() {
        return script;
    }

    String script;
}