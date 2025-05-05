package org.firstinspires.ftc.teamcode.CommandBased;
import org.firstinspires.ftc.teamcode.CommandBased.InstantCommand;

public class PrintCommand extends InstantCommand {
    /**
     * Creates a new a PrintCommand.
     *
     * @param message the message to print
     */
    public PrintCommand(String message) {
        super(() -> System.out.println(message));
    }
}