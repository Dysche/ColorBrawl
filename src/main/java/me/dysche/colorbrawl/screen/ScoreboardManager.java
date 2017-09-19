package me.dysche.colorbrawl.screen;

import org.cantaloupe.scoreboard.Objective;
import org.cantaloupe.scoreboard.Objective.DisplaySlot;
import org.cantaloupe.scoreboard.Scoreboard;
import org.cantaloupe.scoreboard.entry.SpaceEntry;
import org.cantaloupe.scoreboard.entry.TextEntry;
import org.cantaloupe.text.Text;

import me.dysche.colorbrawl.player.CBWrapper;

public class ScoreboardManager {
    public static Scoreboard createMainScoreboard(CBWrapper wrapper) {
        Scoreboard scoreboard = Scoreboard.of();
        Objective objective = scoreboard.createObjective("side", "dummy");
        objective.setSlot(DisplaySlot.SIDEBAR);
        objective.setTitle(Text.fromLegacy("&a&lCo&b&llo&c&lr&r&lBrawl"));
        objective.addEntry(0, TextEntry.of(Text.fromLegacy("                      ")));
        objective.addEntry(1, TextEntry.of(Text.fromLegacy("Coins: &6" + wrapper.getCoins())));
        objective.addEntry(2, TextEntry.of(Text.fromLegacy("Wins: &6" + wrapper.getWins())));

        return scoreboard;
    }

    public static Scoreboard createLobbyScoreboard() {
        Scoreboard scoreboard = Scoreboard.of();
        Objective objective = scoreboard.createObjective("side", "dummy");
        objective.setSlot(DisplaySlot.SIDEBAR);
        objective.setTitle(Text.fromLegacy("&a&lCo&b&llo&c&lr&r&lBrawl"));
        objective.addEntry(0, TextEntry.of(Text.fromLegacy("                      ")));
        objective.addEntry(1, TextEntry.of(Text.of("Players: ")));
        objective.addEntry(2, SpaceEntry.of());
        objective.addEntry(3, TextEntry.of(Text.of("Waiting for players...")));

        return scoreboard;
    }

    public static Scoreboard createGameScoreboard(CBWrapper wrapper) {
        Scoreboard scoreboard = Scoreboard.of();
        Objective objective = scoreboard.createObjective("side", "dummy");
        objective.setSlot(DisplaySlot.SIDEBAR);
        objective.setTitle(Text.fromLegacy("&a&lCo&b&llo&c&lr&r&lBrawl"));
        objective.addEntry(0, TextEntry.of(Text.fromLegacy("                      ")));
        objective.addEntry(1, TextEntry.of(Text.fromLegacy("&c&lRED")));
        objective.addEntry(2, TextEntry.of(Text.fromLegacy("576")));
        objective.addEntry(3, SpaceEntry.of());
        objective.addEntry(4, TextEntry.of(Text.fromLegacy("&a&lGREEN")));
        objective.addEntry(5, TextEntry.of(Text.fromLegacy("576 ")));
        objective.addEntry(6, SpaceEntry.of());
        objective.addEntry(7, TextEntry.of(Text.fromLegacy("&9&lBLUE")));
        objective.addEntry(8, TextEntry.of(Text.fromLegacy("576  ")));
        objective.addEntry(9, SpaceEntry.of());
        objective.addEntry(10, TextEntry.of(Text.fromLegacy("&e&lYELLOW")));
        objective.addEntry(11, TextEntry.of(Text.fromLegacy("576   ")));
        
        return scoreboard;
    }
}