package me.junioraww.textonhead.utils;

import io.netty.channel.*;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

public class PacketInterceptor {
  private static Field passengersField;

  static {
    try {
      passengersField = ClientboundSetPassengersPacket.class.getDeclaredField("passengers");
      passengersField.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  public static void inject(Player player) {
    ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
    Channel channel = nmsPlayer.connection.connection.channel;

    if (channel.pipeline().get("text_on_head_handler") != null) return;

    channel.pipeline().addBefore("packet_handler", "text_on_head_handler", new ChannelOutboundHandlerAdapter() {
      @Override
      public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        /*if (msg instanceof ClientboundSetPassengersPacket packet) {
          int vehicleId = packet.getVehicle();

          Set<Integer> extraIds = PassengerRegistry.getForEntityId(vehicleId);

          if (!extraIds.isEmpty()) {
            int[] current = (int[]) passengersField.get(packet);

            int[] updated = current;
            for (int extraId : extraIds) {
              if (!contains(updated, extraId)) {
                updated = Arrays.copyOf(updated, updated.length + 1);
                updated[updated.length - 1] = extraId;
              }
            }
            passengersField.set(packet, updated);
          }
        }*/
        super.write(ctx, msg, promise);
      }
    });
  }

  public static void uninject(Player player) {
    try {
      ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
      Channel channel = nmsPlayer.connection.connection.channel;
      if (channel.pipeline().get("text_on_head_handler") != null) {
        channel.pipeline().remove("text_on_head_handler");
      }
    } catch (Exception ignored) {}
  }

  private static boolean contains(int[] array, int id) {
    for (int i : array) if (i == id) return true;
    return false;
  }
}