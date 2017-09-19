package me.dysche.colorbrawl.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.PlayerInventory;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.data.DataContainer;
import org.cantaloupe.inventory.ItemStack;
import org.cantaloupe.nbt.NBTTagCompound;
import org.cantaloupe.permission.Allowable;
import org.cantaloupe.player.Player;
import org.cantaloupe.scoreboard.Scoreboard;
import org.cantaloupe.scoreboard.entry.TextEntry;
import org.cantaloupe.service.services.ScheduleService;
import org.cantaloupe.service.services.ScreenService;
import org.cantaloupe.text.Text;

import me.dysche.colorbrawl.ColorBrawl;
import me.dysche.colorbrawl.game.map.Map;
import me.dysche.colorbrawl.game.team.Team;
import me.dysche.colorbrawl.game.team.TeamColor;
import me.dysche.colorbrawl.player.CBWrapper;
import me.dysche.colorbrawl.screen.ScoreboardManager;

public class Game {
    private final ArrayList<Player>              players;
    private final DataContainer<TeamColor, Team> teams;
    private final Map                            map;
    private boolean                              started = false;
    private Player                               winner  = null;

    public Game() {
        this.players = new ArrayList<Player>();
        this.map = new Map(Cantaloupe.getWorldManager().getWorld("cbmap"));

        this.teams = DataContainer.of();
        this.teams.put(TeamColor.RED, new Team(TeamColor.RED));
        this.teams.put(TeamColor.GREEN, new Team(TeamColor.GREEN));
        this.teams.put(TeamColor.BLUE, new Team(TeamColor.BLUE));
        this.teams.put(TeamColor.YELLOW, new Team(TeamColor.YELLOW));
    }

    public void start() {
        this.map.setup();
        this.startCountdown();

        this.players.forEach(player -> {
            player.setScoreboard(ScoreboardManager.createGameScoreboard(player.<CBWrapper>getWrapper(CBWrapper.class).get()));

            this.preparePlayer(player);
        });

        this.winner = null;
        this.started = true;
    }

    public void stop(Player winner) {
        this.winner = winner;

        ColorBrawl.getInstance().getLobby().spawnPlayer(winner);
        Cantaloupe.getServiceManager().provide(ScreenService.class).title(Text.fromLegacy("&6&lVICTORY"), 100, winner);

        CBWrapper wrapper = winner.<CBWrapper>getWrapper(CBWrapper.class).get();
        wrapper.setCoins(wrapper.getCoins() + 100);
        wrapper.setWins(wrapper.getWins() + 1);
        wrapper.endGame();

        this.players.clear();
        this.started = false;
    }

    public void updatePlayer(Player player) {
        CBWrapper wrapper = player.<CBWrapper>getWrapper(CBWrapper.class).get();

        this.players.forEach(p -> {
            Scoreboard scoreboard = p.getCurrentScoreboard();
            TextEntry entry = null;

            switch (wrapper.getTeam().getColor()) {
                case RED:
                    entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(2);
                    entry.setText(Text.fromLegacy(wrapper.getHealth() + ""));

                    break;
                case GREEN:
                    entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(5);
                    entry.setText(Text.fromLegacy(wrapper.getHealth() + " "));

                    break;
                case BLUE:
                    entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(8);
                    entry.setText(Text.fromLegacy(wrapper.getHealth() + "  "));

                    break;
                case YELLOW:
                    entry = (TextEntry) scoreboard.getObjective("side").get().getEntry(11);
                    entry.setText(Text.fromLegacy(wrapper.getHealth() + "   "));

                    break;
            }
        });

        this.map.updatePlayer(wrapper);
    }

    public void addPlayer(Player player) {
        player.allow(Allowable.INTERACT_AIR);
        player.allow(Allowable.INTERACT_BLOCK);

        this.players.add(player);
    }

    public void removePlayer(Player player) {
        ColorBrawl.getInstance().getLobby().spawnPlayer(player);

        this.players.remove(player);
        this.players.trimToSize();

        if (this.players.size() == 1) {
            this.stop(this.players.get(0));
        }

        if (player != this.winner) {
            Cantaloupe.getServiceManager().provide(ScreenService.class).title(Text.fromLegacy("&c&lGAME OVER"), 100, player);
        }
    }

    private void preparePlayer(Player player) {
        PlayerInventory inventory = player.toHandle().getInventory();
        inventory.clear();
        inventory.setItem(9, ItemStack.of(Material.ARROW, 1).toHandle());

        ItemStack sword = ItemStack.of(Material.DIAMOND_SWORD);
        sword.setTag(NBTTagCompound.of().setBoolean("Unbreakable", true));
        inventory.addItem(sword.toHandle());

        ItemStack fishingRod = ItemStack.of(Material.FISHING_ROD);
        fishingRod.setTag(NBTTagCompound.of().setBoolean("Unbreakable", true));
        inventory.addItem(fishingRod.toHandle());

        ItemStack healthBoost = ItemStack.of(Material.GOLDEN_APPLE, 3);
        healthBoost.setTag(NBTTagCompound.of().setInt("healthBoost", 50).setBoolean("Unbreakable", true));
        healthBoost.setDisplayName(Text.fromLegacy("&6+50 Health"));
        inventory.setItem(4, healthBoost.toHandle());

        ItemStack bow = ItemStack.of(Material.BOW);
        bow.setTag(NBTTagCompound.of().setBoolean("Unbreakable", true));
        bow.toHandle().addEnchantment(Enchantment.ARROW_INFINITE, 1);
        inventory.addItem(bow.toHandle());
    }

    private void startCountdown() {
        Cantaloupe.getServiceManager().provide(ScheduleService.class).repeat("game:colorbrawl:countdown", new Runnable() {
            private int secondsElapsed = 0;

            @Override
            public void run() {
                if (this.secondsElapsed == 6) {
                    ScreenService service = Cantaloupe.getServiceManager().provide(ScreenService.class);
                    service.title(Text.of("Begin"), 20, players);

                    players.forEach(player -> {
                        player.playSound(Sound.BLOCK_NOTE_PLING, 1f, 2f);
                    });

                    startInternal();
                } else {
                    ScreenService service = Cantaloupe.getServiceManager().provide(ScreenService.class);
                    service.title(Text.of((6 - this.secondsElapsed) + ""), 20, players);

                    players.forEach(player -> {
                        player.playSound(Sound.BLOCK_NOTE_PLING, 1f, 1f);
                    });
                }

                this.secondsElapsed++;
            }
        }, 20L);
    }

    private void startInternal() {
        Cantaloupe.getServiceManager().provide(ScheduleService.class).cancel("game:colorbrawl:countdown");

        this.map.removeWalls();
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean isPlaying(Player player) {
        return this.players.contains(player);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public Team getTeam(TeamColor color) {
        return this.teams.get(color);
    }

    public Team getTeam(Player player) {
        return player.<CBWrapper>getWrapper(CBWrapper.class).get().getTeam();
    }

    public Collection<Team> getTeams() {
        return this.teams.valueSet();
    }

    public Map getMap() {
        return this.map;
    }
}