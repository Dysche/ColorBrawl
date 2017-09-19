package me.dysche.colorbrawl.commands;

import java.util.Optional;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.command.CommandResult;
import org.cantaloupe.command.CommandSource;
import org.cantaloupe.command.CommandSpec;
import org.cantaloupe.command.ICommandExecutor;
import org.cantaloupe.command.args.CommandContext;
import org.cantaloupe.player.Player;
import org.cantaloupe.text.Text;

import me.dysche.colorbrawl.ColorBrawl;
import me.dysche.colorbrawl.game.Game;
import me.dysche.colorbrawl.player.CBWrapper;

public class LobbyCommands {
    public static void register() {
        ColorBrawl.getInstance().registerCommand(createLeaveCommand(), "leave", "dequeue");
    }

    private static CommandSpec createLeaveCommand() {
        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("This command is used to leave the queue."))
                .usage(Text.of("&a/leave"))
                .permission("colorbrawl.commands.leave")
                .executor(new ICommandExecutor() {
            @Override
            public CommandResult execute(CommandSource src, CommandContext args) {
                Optional<Player> playerOpt = Cantaloupe.getPlayerManager().getPlayerFromCommandSource(src);
                if (playerOpt.isPresent()) {
                    Player player = playerOpt.get();
                    Game game = ColorBrawl.getInstance().getGame();

                    if (game.hasStarted() && game.isPlaying(player)) {
                        player.<CBWrapper>getWrapper(CBWrapper.class).get().endGame();
                    } else {
                        ColorBrawl.getInstance().getLobby().dequeuePlayer(player);
                    }
                }

                return CommandResult.FAILURE;
            }
        }).build();

        return spec;
    }
}