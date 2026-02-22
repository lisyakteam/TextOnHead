package me.junioraww.textonhead.utils;

import me.junioraww.textonhead.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HeadText {
  private static final Map<UUID, Lines> players = new ConcurrentHashMap<>();

  public static void initPlayer(Player player) {
    if (!players.containsKey(player.getUniqueId())) {
      Lines lines = new Lines(player);
      players.put(player.getUniqueId(), lines);
    }

    Lines lines = players.get(player.getUniqueId());
    lines.sendPackets(player, false);
    lines.getWatchers().forEach(p -> lines.sendPackets(p, false));
  }

  public static void removePlayer(Player player) {
    Lines lines = players.remove(player.getUniqueId());

    if (lines != null) {
      lines.sendPackets(player, true);
      lines.getWatchers().forEach(p -> lines.sendPackets(p, true));
      lines.getWatchers().clear();
    }
  }

  public static void recreate(Player player) {
    Lines lines = players.get(player.getUniqueId());

    if (lines != null) {
      lines.sendPackets(player, true);
      lines.getWatchers().forEach(p -> lines.sendPackets(p, true));
      lines.getWatchers().clear();
      players.remove(player.getUniqueId());
    }

    initPlayer(player);
  }

  public static void start() {
    Main plugin = Main.getPlugin();

    plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> {

      players.entrySet().removeIf(entry -> {
        Player p = Bukkit.getPlayer(entry.getKey());
        return p == null || !p.isOnline();
      });

      for (Player player : Bukkit.getOnlinePlayers()) {
        Lines lines = players.get(player.getUniqueId());
        if (lines == null) continue;

        Collection<Player> nearby = player.getWorld().getPlayersSeeingChunk(player.getChunk());

        for (Player other : nearby) {
          if (!shouldSee(other, player, nearby)) continue;
          if (lines.getWatchers().contains(other)) continue;

          lines.sendPackets(other, false);
          lines.getWatchers().add(other);
        }

        lines.getWatchers().removeIf(other -> {
          if (shouldSee(other, player, nearby)) return false;

          lines.sendPackets(other, true);
          return true;
        });
      }
    }, 1L, 1L, TimeUnit.SECONDS);
  }

  private static boolean shouldSee(Player other, Player player, Collection<Player> nearby) {
    return !other.equals(player)
            && other.canSee(player)
            && nearby.contains(other)
            && !player.getGameMode().equals(GameMode.SPECTATOR)
            && !player.hasPotionEffect(PotionEffectType.INVISIBILITY);
  }

  public static Lines getPlayerLines(Player player) {
    return players.get(player.getUniqueId());
  }
}