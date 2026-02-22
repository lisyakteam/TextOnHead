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
import org.bukkit.scoreboard.Team;

public class PlayerEvents implements Listener {


  @EventHandler
  public void playerJoined(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    HeadText.initPlayer(player);
    init(player);
  }

  public static void init(Player player) {
    var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    var team = scoreboard.getTeam("hideTag");
    if (team == null) {
      team = scoreboard.registerNewTeam("hideTag");
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    team.addPlayer(player);
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
    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
      HeadText.recreate(event.getPlayer());
    }, 2L);
  }

  @EventHandler
  public void playerVanished(PlayerShowEntityEvent event) {
    if (event.getEntity() instanceof Player other && other.equals(event.getPlayer())) {
      Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
        HeadText.recreate(event.getPlayer());
      }, 2L);
    }
  }

  @EventHandler
  public void playerChangedDimension(PlayerChangedWorldEvent event) {
    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
      HeadText.recreate(event.getPlayer());
    }, 2L);
  }
}
