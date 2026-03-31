package me.junioraww.textonhead.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.junioraww.textonhead.Main;
import me.junioraww.textonhead.utils.HeadText;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatEvents implements Listener {
  @EventHandler
  public void chatMessage(AsyncChatEvent event) {
    Player player = event.getPlayer();
    String text = PlainTextComponentSerializer.plainText().serialize(event.message());

    var lines = HeadText.getPlayerLines(player);
    if (lines != null) {
      Bukkit.getScheduler().runTask(Main.getPlugin(), task -> {
        lines.addMessage(player, text);
      });
    }
  }
}
