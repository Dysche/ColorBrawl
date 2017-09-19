package me.dysche.colorbrawl.game.team;

public class Team {
    private final TeamColor color;

    public Team(TeamColor color) {
        this.color = color;
    }

    public TeamColor getColor() {
        return this.color;
    }
}