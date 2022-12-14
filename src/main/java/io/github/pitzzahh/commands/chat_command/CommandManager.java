/*
 * MIT License
 *
 * Copyright (c) 2022 pitzzahh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.pitzzahh.commands.chat_command;

import io.github.pitzzahh.commands.chat_command.commands.FormatCommand;
import io.github.pitzzahh.commands.chat_command.commands.HelpCommand;
import io.github.pitzzahh.commands.chat_command.commands.PingCommand;
import io.github.pitzzahh.exceptions.CommandAlreadyExistException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import static io.github.pitzzahh.Bot.getConfig;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private final List<Command> COMMANDS = new ArrayList<>();

    public CommandManager() {
        addCommands(
                new PingCommand(),
                new FormatCommand(),
                new HelpCommand(this)
        );
    }

    /**
     * Adds a chat_command.
     * @param command the chat_command to add.
     */
    private void addCommand(Command command) {
        var found = this.COMMANDS.stream()
                .anyMatch(c -> c.name().get().equalsIgnoreCase(command.name().get()));
        if (found) throw new CommandAlreadyExistException("A Command With this name is already present!");
        this.COMMANDS.add(command);
    }

    private void addCommands(@NotNull Command... command) {
        Arrays.stream(command).forEach(this::addCommand);
    }

    /**
     * Gets a chat_command from the list.
     * @param s the name of the chat_command
     * @return a {@code Command}.
     */
    public Optional<Command> getCommand(@NotNull final String s) {
        final var COMMAND = s.toLowerCase();
        return this.COMMANDS
                .stream()
                .filter(c -> c.name().get().equalsIgnoreCase(COMMAND) || c.aliases().get().contains(COMMAND))
                .findAny();
    }

    /**
     * Handles commands.
     * @param event the event that happened.
     */
    public void handle(@NotNull MessageReceivedEvent event) {
        final var SPLIT = event.getMessage().getContentRaw()
                .replaceFirst("(?i)".concat(Pattern.quote(getConfig.get().get("PREFIX"))), "")
                .split("\\s+");
        final var INVOKED = SPLIT[0].toLowerCase();
        final var COMMAND = this.getCommand(INVOKED);
        event.getChannel().sendTyping().queue();
        final var ARGS = Arrays.asList(SPLIT).subList(1, SPLIT.length);
        final var COMMAND_CONTEXT = new CommandContext(event, ARGS);
        COMMAND.ifPresentOrElse(command -> command.handle().accept(COMMAND_CONTEXT),
                () -> event.getMessage().reply(String.format("%s is not a chat_command", INVOKED)).queue(
                e -> e.getChannel().sendMessage(";help").queue(m -> m.delete().queue())
        ));
    }

    /**
     * Gets all the commands.
     * @return a {@code List<Command>}.
     */
    public List<Command> getCOMMANDS() {
        return this.COMMANDS;
    }
}
