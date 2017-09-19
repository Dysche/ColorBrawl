package me.dysche.colorbrawl.player;

import java.util.Optional;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.data.DataContainer;
import org.cantaloupe.database.MongoDB;
import org.cantaloupe.database.mongodb.Collection;
import org.cantaloupe.database.mongodb.Database;
import org.cantaloupe.permission.Allowable;
import org.cantaloupe.player.Player;
import org.cantaloupe.player.PlayerWrapper;
import org.cantaloupe.scoreboard.Scoreboard;
import org.cantaloupe.scoreboard.entry.TextEntry;
import org.cantaloupe.service.services.MongoService;
import org.cantaloupe.text.Text;

import me.dysche.colorbrawl.ColorBrawl;
import me.dysche.colorbrawl.game.Game;
import me.dysche.colorbrawl.game.team.Team;
import me.dysche.colorbrawl.screen.ScoreboardManager;

public class CBWrapper extends PlayerWrapper {
    private Team team   = null;
    private int  health = 576;
    private int  coins  = 0;
    private int  wins   = 0;

    public CBWrapper(Player player) {
        super(player);
    }

    @Override
    public void onLoad() {
        MongoService service = Cantaloupe.getServiceManager().provide(MongoService.class);
        MongoDB connection = service.getConnection("main").get();
        Database database = connection.getDatabase("colorbrawl").get();
        Optional<Collection> collectionOpt = database.getCollection("players");

        if (collectionOpt.isPresent()) {
            for (DataContainer<String, Object> data : collectionOpt.get().retrieve(this.getPlayer().getUUID().toString())) {
                this.coins = data.getGeneric("coins");
                this.wins = data.getGeneric("wins");

                break;
            }
        }
    }

    public void endGame() {
        Game game = ColorBrawl.getInstance().getGame();

        if (this.team != null) {
            game.updatePlayer(this.getPlayer());
        }

        game.removePlayer(this.getPlayer());

        this.getPlayer().setScoreboard(ScoreboardManager.createMainScoreboard(this));
        this.getPlayer().toHandle().getInventory().clear();
        this.getPlayer().disallow(Allowable.INTERACT_AIR);
        this.getPlayer().disallow(Allowable.INTERACT_BLOCK);

        this.team = null;
        this.health = 576;
    }

    private void updatePlayerData() {
        MongoService service = Cantaloupe.getServiceManager().provide(MongoService.class);
        MongoDB connection = service.getConnection("main").get();
        Database database = connection.getDatabase("colorbrawl").get();
        Optional<Collection> collectionOpt = database.getCollection("players");
        Collection collection = null;

        if (!collectionOpt.isPresent()) {
            collection = database.createCollection("players");
        } else {
            collection = collectionOpt.get();
        }

        collection.upsert(this.getPlayer().getUUID().toString(), DataContainer.<String, Object>of().put("coins", this.coins).put("wins", this.wins));
    }

    private void updateScoreboard() {
        Scoreboard scoreboard = this.getPlayer().getCurrentScoreboard();

        TextEntry coinsEntry = (TextEntry) scoreboard.getObjective("side").get().getEntry(1);
        coinsEntry.setText(Text.fromLegacy("Coins: &6" + this.coins));

        TextEntry winsEntry = (TextEntry) scoreboard.getObjective("side").get().getEntry(2);
        winsEntry.setText(Text.fromLegacy("Wins: &6" + this.wins));
    }

    public void resetHealth() {
        this.health = 576;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setHealth(int health) {
        this.health = health;

        ColorBrawl.getInstance().getGame().updatePlayer(this.getPlayer());
    }

    public void setCoins(int coins) {
        this.coins = coins;

        this.updatePlayerData();
        this.updateScoreboard();
    }

    public void setWins(int wins) {
        this.wins = wins;

        this.updatePlayerData();
        this.updateScoreboard();
    }

    public Team getTeam() {
        return this.team;
    }

    public int getHealth() {
        return this.health;
    }

    public int getCoins() {
        return this.coins;
    }

    public int getWins() {
        return this.wins;
    }
}