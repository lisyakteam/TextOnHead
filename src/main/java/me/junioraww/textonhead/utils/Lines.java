package me.junioraww.textonhead.utils;

import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lines {
  private Stats stats = new Stats();
  private List<Component> messages = new ArrayList<>();
  private Packet[] spawnPackets;
  private Packet[] removePackets;
  private List<Player> watchers = new ArrayList<>();
  private int entityId;

  public Lines(Player player) {
    Location center = player.getLocation();
    ServerLevel nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
    ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
    BlockPos blockPos = new BlockPos(center.getBlockX(), center.getBlockY(), center.getBlockZ());

    Display.TextDisplay display = EntityType.TEXT_DISPLAY.create(
            nmsWorld,
            null,
            blockPos,
            EntitySpawnReason.COMMAND,
            false,
            false
    );

    display.startRiding(nmsPlayer, true, false);
    display.setBillboardConstraints(Display.BillboardConstraints.CENTER);
    display.setText(Component.literal("Hello world!"));
    display.setTransformation(new Transformation(new Matrix4f().translation(0.0f, 0.2f, 0.0f)));

    var spawnPacket = new ClientboundAddEntityPacket(
            display.getId(),
            UUID.randomUUID(),
            display.getX(),
            display.getY(),
            display.getZ(),
            0f,
            0f,
            display.getType(),
            0,
            display.getDeltaMovement(),
            0.0
    );
    var dirtyData = display.getEntityData().packDirty();
    if (dirtyData == null) throw new RuntimeException("data is null");

    var ridePacket = new ClientboundSetPassengersPacket(nmsPlayer);
    var dataPacket = new ClientboundSetEntityDataPacket(display.getId(), dirtyData);
    var remPacket = new ClientboundRemoveEntitiesPacket(display.getId());

    spawnPackets = new Packet[] {
            spawnPacket,
            dataPacket,
            ridePacket
    };

    removePackets = new Packet[] {
            remPacket
    };
  }

  public void sendPackets(Player player, boolean remove) {
    var client = ((CraftPlayer) player).getHandle().connection;
    if (!remove) for (var packet : spawnPackets) client.send(packet);
    else for (var packet : removePackets) client.send(packet);
  }

  public List<Player> getWatchers() {
    return watchers;
  }

  public Stats getStats() {
    return stats;
  }

  public List<Component> getMessages() {
    return messages;
  }
}
