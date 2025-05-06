package org.firstinspires.ftc.teamcode.ufpackages.CommandBased;

import java.util.function.Supplier;

public class SelectCommand extends Command{
    private final Supplier<Command> commands;
    private Command selectedCommand;
    public SelectCommand(Supplier<Command> commands){
        this.commands = commands;
        addRequirements(commands.get().getRequirements());
    }

    @Override
    protected void initialize() {
        selectedCommand = commands.get();
        addRequirements(selectedCommand.getRequirements());
        assert selectedCommand != null;
        selectedCommand.initialize();
    }

    @Override
    public void execute() {
        selectedCommand.execute();
    }

    @Override
    public void end(boolean interrupted) {
        selectedCommand.end(false);
    }

    @Override
    protected boolean isFinished() {
        return selectedCommand.isFinished();
    }
}
