package me.junioraww.textonhead.utils;

import me.junioraww.tails.Main;
import me.junioraww.tails.storages.Wallet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.bukkit.entity.Player;

public class Stats {
  public static Component getLine(Player player) {
    Wallet wallet = Main.getPlugin().getStorage().getWallet(player);

    if (wallet == null) return
            Component.literal("0☆")
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555)).withBold(true));
    else {
      int level = wallet.getLevel();
      return Component
              .literal(player.getName())
              .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)))
              .append(Component
                      .literal(" [" + level + "☆]")
                      .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700)))
              );
    }
  }
}
