package ui.interfaces;

import ui.command.CommandContext;

public interface Command {
    String getName();
    String getDescription();
    void execute(CommandContext context);
}
