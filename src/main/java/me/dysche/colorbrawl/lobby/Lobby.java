package me.dysche.colorbrawl.lobby;

import java.util.LinkedList;
import java.util.Queue;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.player.Player;
import org.cantaloupe.scoreboard.Scoreboard;
import org.cantaloupe.scoreboard.entry.TextEntry;
import org.cantaloupe.service.services.ScheduleService;
import org.cantaloupe.text.Text;
import org.cantaloupe.world.World;
import org.cantaloupe.world.location.ImmutableLocation;
import org.joml.Vector2f;
import org.joml.Vector3d;

import me.dysche.colorbrawl.ColorBrawl;
import me.dysche.colorbrawl.game.Game;
import me.dysche.colorbrawl.player.CBWrapper;
import me.dysche.colorbrawl.screen.ScoreboardManager;

public class Lobby {
    private World               world   = null;
    private boolean             started = false, countdown = false;

    private final Queue<Player> playerQueue;

    public Lobby(World world) {
        this.world = world;
        this.playerQueue = new LinkedList<Player>();
    }

    public void init() {
        Cantaloupe.getServiceManager().provide(ScheduleService.class).repeat("game:colorbrawl:lobby:" + this.world.getName() + ":update", new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 5L);
    }

    public void update() {
        if (!ColorBrawl.getInstance().getGame().hasStarted() && !this.countdown && this.started) {
            this.started = false;
        }

        if (this.playerQueue.size() >= 2 && !this.started) {
            this.startCountdown();

            this.started = true;
        }
    }

    public void dequeuePlayer(Player player) {
        if (this.playerQueue.contains(player)) {
            player.sendMessage(Text.fromLegacy("&aYou have left the queue."));
            this.playerQueue.remove(player);

            if (this.playerQueue.size() < 2) {
                this.stopCountdown();

                player.setScoreboard(ScoreboardManager.createMainScoreboard(player.<CBWrapper>getWrapper(CBWrapper.class).get()));
            }
        } else {
            player.sendMessage(Text.fromLegacy("&aYou are not in the queue."));
        }
    }

    public void queuePlayer(Player player) {
        if (!this.playerQueue.contains(player)) {
            if (this.playerQueue.size() < 4) {
                this.playerQueue.add(player);

                player.setScoreboard(ScoreboardManager.createLobbyScoreboard());
                player.sendMessage(Text.fromLegacy("&aYou have joined the queue."));

                this.playerQueue.forEach(p -> {
                    Scoreboard scoreboard = p.getCurrentScoreboard();

                    TextEntry entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(1);
                    entry.setText(Text.fromLegacy("Players: &6" + this.playerQueue.size() + "/4"));
                });
            } else {
                player.sendMessage(Text.fromLegacy("&aThe queue is full."));
            }
        } else {
            player.sendMessage(Text.fromLegacy("&aYou're already in the queue."));
        }
    }

    public boolean isQueued(Player player) {
        return this.playerQueue.contains(player);
    }

    private void startCountdown() {
        this.countdown = true;

        Cantaloupe.getServiceManager().provide(ScheduleService.class).repeat("lobby:colorbrawl:countdown", new Runnable() {
            private int secondsElapsed = 0;

            @Override
            public void run() {
                if (this.secondsElapsed == 10) {
                    Cantaloupe.getServiceManager().provide(ScheduleService.class).cancel("lobby:colorbrawl:countdown");

                    startGame();
                } else {
                    playerQueue.forEach(player -> {
                        Scoreboard scoreboard = player.getCurrentScoreboard();

                        TextEntry entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(3);
                        entry.setText(Text.fromLegacy("Starting in &6" + (10 - this.secondsElapsed) + "s"));
                    });
                }

                this.secondsElapsed++;
            }
        }, 20L);
    }

    private void stopCountdown() {
        Cantaloupe.getServiceManager().provide(ScheduleService.class).cancel("lobby:colorbrawl:countdown");

        this.playerQueue.forEach(player -> {
            Scoreboard scoreboard = ScoreboardManager.createLobbyScoreboard();
            TextEntry entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(1);
            entry.setText(Text.fromLegacy("Players: &6" + this.playerQueue.size() + "/4"));

            player.setScoreboard(scoreboard);
        });

        this.started = false;
    }

    private void startGame() {
        Game game = ColorBrawl.getInstance().getGame();
        int i = 0;

        while (!this.playerQueue.isEmpty()) {
            Player player = this.playerQueue.poll();

            game.addPlayer(player);
            game.getMap().spawn(player, i);

            i++;
        }

        game.start();

        this.countdown = false;
    }

    public void spawnPlayer(Player player) {
        player.teleport(ImmutableLocation.of(Cantaloupe.getWorldManager().getWorld("cblobby"), new Vector3d(-622.5D, 29D, -643.5D), new Vector2f(-90f, 0f)));
    }

    public World getWorld() {
        return this.world;
    }
}