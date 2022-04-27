package exposed.hydrogen.resources;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackSendListener implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Resources.getInstance().getLogger().info("Sending resource pack to " + event.getPlayer().getName() + " with hash " + Util.bytesToHex(Resources.getResourcePackHandler().getHash()));
        event.getPlayer().setResourcePack("http://" + Resources.getPublicIP() + ":" + Resources.getInstance().getConfig().getString("port"),
                Resources.getResourcePackHandler().getHash(), Bukkit.getResourcePackPrompt(), Bukkit.isResourcePackRequired());
    }
}
