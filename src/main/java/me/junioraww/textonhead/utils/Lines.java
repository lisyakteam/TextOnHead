package me.junioraww.textonhead.utils;

import com.mojang.math.Transformation;
import me.junioraww.textonhead.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class Lines {
  private final Display.TextDisplay statsDisplay;
  private final Deque<Display.TextDisplay> activeMessages = new ConcurrentLinkedDeque<>();

  private final Map<Integer, List<Packet<?>>> spawnPackets = new HashMap<>();
  private final Map<Integer, Packet<?>> removePackets = new HashMap<>();
  private final List<Player> watchers = new ArrayList<>();

  private final float STEP = 0.25f;

  public Lines(Player player) {
    this.statsDisplay = Displays.createDisplay(player, Stats.getLine(player), 0);
    registerDisplay(statsDisplay, player);
  }

  public List<Player> getWatchers() {
    return watchers;
  }

  private void registerDisplay(Display.TextDisplay display, Player player) {
    spawnPackets.put(display.getId(), Displays.getEntityPackets(display, player));
    removePackets.put(display.getId(), Displays.getRemovePacket(display));
  }

  private Component createColoredComponent(String text) {
    return Component.literal(ChatColor.translateAlternateColorCodes('&', text));
  }

  public void addMessage(Player player, String text) {
    Component component = createColoredComponent(text);
    Display.TextDisplay newMessage = Displays.createDisplay(player, component, 1);

    activeMessages.addFirst(newMessage);
    registerDisplay(newMessage, player);
    sendSpawnPackets(newMessage);

    updateAllPositions();

    Bukkit.getAsyncScheduler().runDelayed(Main.getPlugin(), task -> {
      removeMessage(newMessage);
    }, 3, TimeUnit.SECONDS);
  }

  public void removeMessage(Display.TextDisplay display) {
    if (activeMessages.remove(display)) {
      Packet<?> removePacket = Displays.getRemovePacket(display);
      broadcastPacket(removePacket);

      spawnPackets.remove(display.getId());
      removePackets.remove(display.getId());

      updateAllPositions();
    }
  }

  private void updateAllPositions() {
    int i = 1;
    for (Display.TextDisplay display : activeMessages) {
      updateDisplayHeight(display, i);
      i++;
    }
  }

  private void updateDisplayHeight(Display.TextDisplay display, int index) {
    Transformation transformation = new Transformation(
            new Vector3f(0, (1 + index) * STEP, 0),
            null, null, null
    );

    display.setTransformation(transformation);
    display.setTransformationInterpolationDuration(5);
    display.setTransformationInterpolationDelay(0);

    ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
            display.getId(),
            display.getEntityData().getNonDefaultValues()
    );
    broadcastPacket(packet);
  }

  private void sendSpawnPackets(Display.TextDisplay display) {
    List<Packet<?>> packets = spawnPackets.get(display.getId());
    if (packets == null) return;
    for (Player watcher : watchers) {
      var client = ((CraftPlayer) watcher).getHandle().connection;
      packets.forEach(client::send);
    }
  }

  private void broadcastPacket(Packet<?> packet) {
    for (Player watcher : watchers) {
      if (watcher.isOnline()) {
        ((CraftPlayer) watcher).getHandle().connection.send(packet);
      }
    }
  }

  public void sendPackets(Player player, boolean remove) {
    var client = ((CraftPlayer) player).getHandle().connection;
    if (!remove) {
      spawnPackets.values().forEach(list -> list.forEach(client::send));
      if (!watchers.contains(player)) watchers.add(player);
    } else {
      removePackets.values().forEach(client::send);
      watchers.remove(player);
    }
  }
}