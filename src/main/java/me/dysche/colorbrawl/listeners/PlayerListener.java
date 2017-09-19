package me.dysche.colorbrawl.listeners;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.inventory.ItemStack;
import org.cantaloupe.player.Player;
import org.cantaloupe.service.services.ScheduleService;
import org.cantaloupe.text.Text;

import me.dysche.colorbrawl.ColorBrawl;
import me.dysche.colorbrawl.game.Game;
import me.dysche.colorbrawl.lobby.Lobby;
import me.dysche.colorbrawl.player.CBWrapper;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Optional<Player> playerOpt = Cantaloupe.getPlayerManager().getPlayerFromHandle(event.getPlayer());

        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getType() == Material.WALL_SIGN) {
                    Location location = event.getClickedBlock().getLocation();

                    if (location.getWorld().getName().equals("cblobby")) {
                        if (location.getBlockX() == -619 && location.getBlockY() == 29 && location.getBlockZ() == -644) {
                            Lobby lobby = ColorBrawl.getInstance().getLobby();

                            if (player.toHandle().isSneaking()) {
                                if (lobby.isQueued(player)) {
                                    lobby.dequeuePlayer(player);
                                    event.setCancelled(true);

                                    return;
                                } else {
                                    lobby.queuePlayer(player);
                                    event.setCancelled(true);

                                    return;
                                }
                            } else {
                                lobby.queuePlayer(player);
                                event.setCancelled(true);

                                return;
                            }
                        }
                    }
                }
            }

            if (event.getItem() != null) {
                ItemStack stack = ItemStack.of(event.getItem());

                if (stack.hasTag() && stack.getTag().hasKey("healthBoost")) {
                    int healthBoost = stack.getTag().getInt("healthBoost");
                    CBWrapper wrapper = player.<CBWrapper>getWrapper(CBWrapper.class).get();

                    if (wrapper.getHealth() + healthBoost < 576) {
                        ScheduleService service = Cantaloupe.getServiceManager().provide(ScheduleService.class);

                        if (!service.isTaskRunning("game:colorbrawl:healthboost:" + player.getUUID())) {
                            if (stack.getAmount() - 1 > 0) {
                                stack.setAmount(stack.getAmount() - 1);
                            } else {
                                event.getPlayer().getInventory().setItemInMainHand(null);
                            }

                            service.delay("game:colorbrawl:healthboost:" + player.getUUID(), new Runnable() {
                                @Override
                                public void run() {
                                    player.sendMessage(Text.fromLegacy("&aYou can heal yourself again."));
                                }
                            }, 100L);

                            player.sendMessage(Text.fromLegacy("&6Added 50 health."));
                            player.sendMessage(Text.fromLegacy("&6You can use this again in 5s."));
                            wrapper.setHealth(wrapper.getHealth() + healthBoost);
                        }
                    }

                    player.sendMessage(Text.fromLegacy("&cYou can't heal yourself yet."));
                    event.setCancelled(true);

                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        org.bukkit.entity.Player damagerTmp = null;

        if (event.getCause() == DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof org.bukkit.entity.Player) {
                damagerTmp = (org.bukkit.entity.Player) projectile.getShooter();
            }
        } else {
            damagerTmp = (org.bukkit.entity.Player) event.getDamager();
        }

        if (event.getEntity() == damagerTmp) {
            event.setCancelled(true);

            return;
        }

        if (damagerTmp instanceof org.bukkit.entity.Player) {
            Optional<Player> targetOpt = Cantaloupe.getPlayerManager().getPlayerFromHandle((org.bukkit.entity.Player) event.getEntity());
            Optional<Player> damagerOpt = Cantaloupe.getPlayerManager().getPlayerFromHandle(damagerTmp);

            if (targetOpt.isPresent() && damagerOpt.isPresent()) {
                Player target = targetOpt.get();
                Player damager = damagerOpt.get();
                Game game = ColorBrawl.getInstance().getGame();

                if (game.isPlaying(target) && game.isPlaying(damager)) {
                    CBWrapper targetWrapper = target.<CBWrapper>getWrapper(CBWrapper.class).get();
                    CBWrapper damagerWrapper = damager.<CBWrapper>getWrapper(CBWrapper.class).get();

                    if (event.getCause() != DamageCause.FALL) {
                        if (targetWrapper.getHealth() - (event.getDamage() * 3) <= 0) {
                            damagerWrapper.setHealth(damagerWrapper.getHealth() + targetWrapper.getHealth());
                            targetWrapper.setHealth(0);
                            targetWrapper.setCoins(targetWrapper.getCoins() + 25);
                            targetWrapper.endGame();
                        } else if (damagerWrapper.getHealth() + (event.getDamage() * 2) <= 576) {
                            targetWrapper.setHealth((int) (targetWrapper.getHealth() - (event.getDamage() * 3)));
                            damagerWrapper.setHealth((int) (damagerWrapper.getHealth() + (event.getDamage() * 2)));
                        } else {
                            targetWrapper.setHealth((int) (targetWrapper.getHealth() - (event.getDamage() * 3)));
                            damagerWrapper.setHealth(576);
                        }

                        event.setDamage(0);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() != null) {
            ItemStack stack = ItemStack.of(event.getItem());

            if (stack.hasTag() && stack.getTag().hasKey("healthBoost")) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Optional<Player> playerOpt = Cantaloupe.getPlayerManager().getPlayerFromHandle(event.getEntity());

        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            Game game = ColorBrawl.getInstance().getGame();

            if (game.hasStarted() && game.isPlaying(player)) {
                player.<CBWrapper>getWrapper(CBWrapper.class).get().endGame();
            }
        }
    }
}