package me.dysche.colorbrawl.game.map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.cantaloupe.player.Player;
import org.cantaloupe.world.World;
import org.joml.Vector3d;

import me.dysche.colorbrawl.ColorBrawl;
import me.dysche.colorbrawl.game.Game;
import me.dysche.colorbrawl.game.team.TeamColor;
import me.dysche.colorbrawl.player.CBWrapper;

@SuppressWarnings("deprecation")
public class Map {
    private final Vector3d c1          = new Vector3d(-334D, 13D, 161D);
    private final Vector3d c2          = new Vector3d(-383D, 13D, 210D);

    private final Vector3d w1c1        = new Vector3d(-383D, 14D, 185D);
    private final Vector3d w1c2        = new Vector3d(-334D, 22D, 186D);
    private final Vector3d w2c1        = new Vector3d(-358D, 14D, 210D);
    private final Vector3d w2c2        = new Vector3d(-359D, 22D, 161D);

    private final Vector3d redP1       = new Vector3d(-334D, 13D, 210D);
    private final Vector3d redP2       = new Vector3d(-357D, 13D, 187D);
    private final Vector3d greenP1     = new Vector3d(-334D, 13D, 161D);
    private final Vector3d greenP2     = new Vector3d(-357D, 13D, 184D);
    private final Vector3d blueP1      = new Vector3d(-383D, 13D, 161D);
    private final Vector3d blueP2      = new Vector3d(-360D, 13D, 184D);
    private final Vector3d yellowP1    = new Vector3d(-383D, 13D, 210D);
    private final Vector3d yellowP2    = new Vector3d(-360D, 13D, 187D);

    private final Vector3d redSpawn    = new Vector3d(-336D, 13D, 208D);
    private final Vector3d greenSpawn  = new Vector3d(-336D, 13D, 163D);
    private final Vector3d blueSpawn   = new Vector3d(-381D, 13D, 163D);
    private final Vector3d yellowSpawn = new Vector3d(-381D, 13D, 208D);

    private final World    world;

    public Map(World world) {
        this.world = world;
    }

    public void setup() {
        // Field
        for (double x = this.c2.x; x <= this.c1.x; x++) {
            for (double z = this.c1.z; z <= this.c2.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);
            }
        }

        // Walls
        for (double x = this.w1c1.x; x <= this.w1c2.x; x++) {
            for (double y = this.w1c1.y; y <= this.w1c2.y; y++) {
                for (double z = this.w1c1.z; z <= this.w1c2.z; z++) {
                    Block block = this.world.getBlock(new Vector3d(x, y, z));
                    block.setType(Material.BARRIER);
                }
            }
        }

        for (double x = this.w2c2.x; x <= this.w2c1.x; x++) {
            for (double y = this.w2c1.y; y <= this.w2c2.y; y++) {
                for (double z = this.w2c2.z; z <= this.w2c1.z; z++) {
                    Block block = this.world.getBlock(new Vector3d(x, y, z));
                    block.setType(Material.BARRIER);
                }
            }
        }

        // Red
        for (double x = this.redP2.x; x <= this.redP1.x; x++) {
            for (double z = this.redP2.z; z <= this.redP1.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);
                block.setData((byte) 14);
            }
        }

        // Green
        for (double x = this.greenP2.x; x <= this.greenP1.x; x++) {
            for (double z = this.greenP1.z; z <= this.greenP2.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);
                block.setData((byte) 13);
            }
        }

        // Blue
        for (double x = this.blueP1.x; x <= this.blueP2.x; x++) {
            for (double z = this.blueP1.z; z <= this.blueP2.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);
                block.setData((byte) 11);
            }
        }

        // Yellow
        for (double x = this.yellowP1.x; x <= this.yellowP2.x; x++) {
            for (double z = this.yellowP2.z; z <= this.yellowP1.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);
                block.setData((byte) 4);
            }
        }
    }

    public void removeWalls() {
        for (double x = this.w1c1.x; x <= this.w1c2.x; x++) {
            for (double y = this.w1c1.y; y <= this.w1c2.y; y++) {
                for (double z = this.w1c1.z; z <= this.w1c2.z; z++) {
                    Block block = this.world.getBlock(new Vector3d(x, y, z));
                    block.setType(Material.AIR);
                }
            }
        }

        for (double x = this.w2c2.x; x <= this.w2c1.x; x++) {
            for (double y = this.w2c1.y; y <= this.w2c2.y; y++) {
                for (double z = this.w2c2.z; z <= this.w2c1.z; z++) {
                    Block block = this.world.getBlock(new Vector3d(x, y, z));
                    block.setType(Material.AIR);
                }
            }
        }
    }

    public void spawn(Player player, int playerNumber) {
        CBWrapper wrapper = player.<CBWrapper>getWrapper(CBWrapper.class).get();
        Game game = ColorBrawl.getInstance().getGame();

        switch (playerNumber) {
            case 0:
                player.teleport(this.world, this.redSpawn);
                wrapper.setTeam(game.getTeam(TeamColor.RED));

                break;
            case 1:
                player.teleport(this.world, this.greenSpawn);
                wrapper.setTeam(game.getTeam(TeamColor.GREEN));

                break;
            case 2:
                player.teleport(this.world, this.blueSpawn);
                wrapper.setTeam(game.getTeam(TeamColor.BLUE));

                break;
            case 3:
                player.teleport(this.world, this.yellowSpawn);
                wrapper.setTeam(game.getTeam(TeamColor.YELLOW));

                break;
        }
    }

    public void updatePlayer(CBWrapper wrapper) {
        switch (wrapper.getTeam().getColor()) {
            case RED:
                this.updateRed(wrapper.getHealth());

                break;

            case GREEN:
                this.updateGreen(wrapper.getHealth());

                break;

            case BLUE:
                this.updateBlue(wrapper.getHealth());

                break;

            case YELLOW:
                this.updateYellow(wrapper.getHealth());

                break;
        }
    }

    private void updateRed(int health) {
        int i = 0;

        for (double x = this.redP2.x; x <= this.redP1.x; x++) {
            for (double z = this.redP2.z; z <= this.redP1.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);

                if (i < health) {
                    block.setData((byte) 14);
                } else {
                    return;
                }

                i++;
            }
        }
    }

    private void updateGreen(int health) {
        int i = 0;

        for (double x = this.greenP2.x; x <= this.greenP1.x; x++) {
            for (double z = this.greenP1.z; z <= this.greenP2.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);

                if (i < health) {
                    block.setData((byte) 13);
                } else {
                    return;
                }

                i++;
            }
        }
    }

    private void updateBlue(int health) {
        int i = 0;

        for (double x = this.blueP1.x; x <= this.blueP2.x; x++) {
            for (double z = this.blueP1.z; z <= this.blueP2.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);

                if (i < health) {
                    block.setData((byte) 11);
                } else {
                    return;
                }

                i++;
            }
        }
    }

    private void updateYellow(int health) {
        int i = 0;

        for (double x = this.yellowP1.x; x <= this.yellowP2.x; x++) {
            for (double z = this.yellowP2.z; z <= this.yellowP1.z; z++) {
                Block block = this.world.getBlock(new Vector3d(x, 13D, z));
                block.setType(Material.WOOL);

                if (i < health) {
                    block.setData((byte) 4);
                } else {
                    return;
                }

                i++;
            }
        }
    }

    public World getWorld() {
        return this.world;
    }
}