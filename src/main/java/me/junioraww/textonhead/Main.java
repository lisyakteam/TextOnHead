package me.junioraww.textonhead;

import me.junioraww.textonhead.events.ChatEvents;
import me.junioraww.textonhead.events.PlayerEvents;
import me.junioraww.textonhead.utils.HeadText;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
  private static Main plugin;

  public static Main getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {
    plugin = this;
    getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    getServer().getPluginManager().registerEvents(new ChatEvents(), this);
    HeadText.start();
  }

  @Override
  public void onDisable() {
    plugin = null;
  }
}
