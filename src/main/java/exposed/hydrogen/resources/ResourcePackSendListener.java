package exposed.hydrogen.resources;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackSendListener implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Resources.getInstance(), () -> {
            Resources.getInstance().getLogger().info("Sending resource pack to " + event.getPlayer().getName() + "...");
            Resources.getInstance().getLogger().info(Resources.getPublicIP() + ":" + Resources.getInstance().getConfig().getString("port")
                    + Bukkit.getResourcePackPrompt() + Bukkit.isResourcePackRequired());
            event.getPlayer().setResourcePack("http://" + Resources.getPublicIP() + ":" + Resources.getInstance().getConfig().getString("port"),
                    Resources.getResourcePackHandler().getHash(), Bukkit.getResourcePackPrompt(), Bukkit.isResourcePackRequired());
        },40);
    }
}
