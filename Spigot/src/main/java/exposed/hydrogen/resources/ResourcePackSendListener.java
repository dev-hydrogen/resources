package exposed.hydrogen.resources;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackSendListener implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Resources.getChameleon().getLogger().info("Sending resource pack to " + event.getPlayer().getName() + " with hash " + Util.bytesToHex(Resources.getResourcePackHandler().getHash()));
        event.getPlayer().setResourcePack(Resources.RESOURCE_PACK_DOWNLOAD_URL,
                Resources.getResourcePackHandler().getHash(), Resources.getResourcePackPrompt(), Resources.isResourcePackRequired());
    }
}
