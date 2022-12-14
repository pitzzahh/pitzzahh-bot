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
package io.github.pitzzahh;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import io.github.pitzzahh.listeners.SlashCommandListener;
import io.github.pitzzahh.listeners.MessageListener;
import io.github.pitzzahh.listeners.ButtonListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.ShardManager;
import io.github.pitzzahh.listeners.MemberLogger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.entities.Activity;
import io.github.pitzzahh.utilities.Util;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import java.util.function.Supplier;
import java.io.IOException;

public class Bot {

    private static Dotenv config;
    private static ShardManager shardManager;

    public static void start() throws LoginException, IOException {
        config = Dotenv.configure().load();

        final var TOKEN = config.get("TOKEN");

        var builder = DefaultShardManagerBuilder.createDefault(TOKEN);
        builder.setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.listening("your messages ????"));
        Util.loadSwearWords();
        shardManager = builder.build();
        shardManager.addEventListener(
                new MessageListener(),
                new ButtonListener(),
                new SlashCommandListener(),
                new MemberLogger()
        );
    }

    /**
     * Get the {@code Dotenv} object.
     * returns the {@code Dotenv} object.
     */
    public static Supplier<Dotenv> getConfig = () -> config;

    /**
     * Get the {@code ShardManager} object.
     * returns the {@code ShardManager} object.
     */
    public static Supplier<ShardManager> getShardManager = () -> shardManager;
}
