package me.dysche.colorbrawl;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.player.PlayerManager;
import org.cantaloupe.plugin.CantaloupePlugin;
import org.cantaloupe.service.services.MongoService;

import me.dysche.colorbrawl.commands.LobbyCommands;
import me.dysche.colorbrawl.game.Game;
import me.dysche.colorbrawl.listeners.PlayerListener;
import me.dysche.colorbrawl.lobby.Lobby;
import me.dysche.colorbrawl.player.CBWrapper;
import me.dysche.colorbrawl.screen.ScoreboardManager;

public class ColorBrawl extends CantaloupePlugin {
    private static ColorBrawl instance = null;
    private Lobby             lobby    = null;
    private Game              game     = null;

    @Override
    public void onPreInit() {
        instance = this;

        Cantaloupe.getWorldManager().loadWorld("cblobby");
        Cantaloupe.getWorldManager().loadWorld("cbmap");

        Cantaloupe.registerListener(new PlayerListener());
        Cantaloupe.getServiceManager().provide(MongoService.class).connect("main");
        Cantaloupe.getPlayerManager().registerWrapper(CBWrapper.class);
        Cantaloupe.getPlayerManager().inject(PlayerManager.Scopes.LOAD, player -> {
            this.lobby.spawnPlayer(player);

            player.setScoreboard(ScoreboardManager.createMainScoreboard(player.<CBWrapper>getWrapper(CBWrapper.class).get()));
        });

        Cantaloupe.getPlayerManager().inject(PlayerManager.Scopes.LEAVE, player -> {
            if (this.game.hasStarted() && this.game.isPlaying(player)) {
                player.<CBWrapper>getWrapper(CBWrapper.class).get().endGame();
            } else {
                this.lobby.dequeuePlayer(player);
            }
        });

        this.lobby = new Lobby(Cantaloupe.getWorldManager().getWorld("cblobby"));
        this.lobby.init();

        LobbyCommands.register();
    }

    @Override
    public void onInit() {
        this.game = new Game();
    }

    @Override
    public void onDeinit() {

    }

    public static ColorBrawl getInstance() {
        return instance;
    }

    public Lobby getLobby() {
        return this.lobby;
    }

    public Game getGame() {
        return this.game;
    }
}