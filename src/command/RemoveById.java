package command;

import program.Route;

public class RemoveById extends Command {
    public RemoveById(Integer id) {
        super(Commands.REMOVE_BY_ID);
        this.id = id;
    }

    @Override
    public Integer returnObj() {
        return id;
    }

    Integer id;
}