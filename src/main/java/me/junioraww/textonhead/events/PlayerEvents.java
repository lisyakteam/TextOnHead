package me.junioraww.textonhead.events;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.junioraww.textonhead.Main;
import me.junioraww.textonhead.utils.HeadText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerEvents implements Listener {
  @EventHandler
  public void playerJoined(PlayerJoinEvent event) {
    HeadText.initPlayer(event.getPlayer());
  }

  @EventHandler
  public void playerSpawned(PlayerRespawnEvent event) {
    Player player = event.getPlayer();

    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
      HeadText.recreate(player);
    }, 2L);
  }

  @EventHandler
  public void playerQuit(PlayerQuitEvent event) {
    HeadText.removePlayer(event.getPlayer());
  }

  @EventHandler
  public void playerDied(PlayerDeathEvent event) {
    HeadText.removePlayer(event.getPlayer());
  }

  @EventHandler
  public void playerTeleport(PlayerTeleportEvent event) {
    HeadText.recreate(event.getPlayer());
  }

  @EventHandler
  public void playerChangedDimension(PlayerChangedWorldEvent event) {
    HeadText.recreate(event.getPlayer());
  }
}
