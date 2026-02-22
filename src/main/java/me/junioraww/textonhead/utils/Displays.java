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

import java.util.List;
import java.util.UUID;

public class Displays {
  public static Display.TextDisplay createDisplay(ServerLevel level, BlockPos pos) {
    return EntityType.TEXT_DISPLAY.create(
            level,
            null,
            pos,
            EntitySpawnReason.COMMAND,
            false,
            false
    );
  }

  public static BlockPos blockPos(Player player) {
    Location center = player.getLocation();
    return new BlockPos(center.getBlockX(), center.getBlockY(), center.getBlockZ());
  }

  public static Display.TextDisplay createDisplay(
          Player player,
          Component component,
          int shift
  ) {
    ServerLevel nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
    ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

    Display.TextDisplay display = createDisplay(nmsWorld, blockPos(player));
    display.startRiding(nmsPlayer, true, false);
    display.setBillboardConstraints(Display.BillboardConstraints.CENTER);
    display.setText(component);
    display.setTransformation(new Transformation(new Matrix4f().translation(0.0f, 0.25f + 0.25f * shift, 0.0f)));
    return display;
  }

  public static ClientboundAddEntityPacket getSpawnPacket(Display display) {
    return new ClientboundAddEntityPacket(
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
  }

  public static List<Packet<?>> getEntityPackets(Display display, Player player) {
    ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
    var dirtyData = display.getEntityData().packDirty();

    if (dirtyData == null) throw new RuntimeException("data is null");

    var spawnPacket = getSpawnPacket(display);
    var dataPacket = new ClientboundSetEntityDataPacket(display.getId(), dirtyData);
    var ridePacket = new ClientboundSetPassengersPacket(nmsPlayer);

    return List.of(
            spawnPacket,
            dataPacket,
            ridePacket
    );
  }

  public static Packet<?> getRemovePacket(Display display) {
    return new ClientboundRemoveEntitiesPacket(display.getId());
  }

}
