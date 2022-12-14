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
package io.github.pitzzahh.commands.chat_command.commands;

import static net.dv8tion.jda.api.interactions.components.buttons.Button.primary;
import static net.dv8tion.jda.api.interactions.components.ActionRow.of;
import io.github.pitzzahh.commands.chat_command.CommandContext;
import io.github.pitzzahh.commands.chat_command.Command;
import static io.github.pitzzahh.utilities.Util.*;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static java.awt.Color.RED;
import io.github.pitzzahh.Bot;

public class FormatCommand implements Command {


    /**
     * Contains the process to be handled.
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    public void process(@NotNull CommandContext context) {
        final var ARGS = context.getArgs();
        final var CHANNEL = context.getEvent().getChannel();

        if (ARGS.size() < 2) {
            final var BUTTON = primary("ok", "okay");
            EMBED_BUILDER.clear()
                    .clearFields()
                    .setColor(RED)
                    .setTitle("MISSING CONTENT");
            MESSAGE_BUILDER.clear()
                    .setEmbeds(EMBED_BUILDER.build())
                    .setActionRows(of(BUTTON));
            CHANNEL.sendMessage(MESSAGE_BUILDER.build()).queue();
            return;
        }
        final var LANGUAGE = ARGS.get(0);
        final var MESSAGE = context.getEvent().getMessage().getContentRaw();
        final var INDEX = MESSAGE.indexOf(LANGUAGE) + LANGUAGE.length();
        final var CONTENT = MESSAGE.substring(INDEX).trim();
        context.getEvent().getMessage()
                .reply("```" + LANGUAGE + "\n" + CONTENT + "```")
                .queue(
                        message -> context.event()
                                .getMessage()
                                .delete()
                                .queue()
                );
    }

    /**
     * Handles the chat_command.
     * Accepts a {@code CommandContext}.
     *
     * @see CommandContext
     */
    @Override
    public Consumer<CommandContext> handle() {
        return this::process;
    }

    /**
     * The name of the chat_command.
     *
     * @return the name of the chat_command.
     */
    @Override
    public Supplier<String> name() {
        return () -> "format";
    }

    /**
     * The description of the chat_command.
     *
     * @return the description of the chat_command.
     */
    @Override
    public Supplier<String> description() {
        return () -> "Formats a code.\n" +
                "Usage: ".concat(Bot.getConfig.get().get("PREFIX").concat(name().get())).concat(" [language] [content]");
    }
}
