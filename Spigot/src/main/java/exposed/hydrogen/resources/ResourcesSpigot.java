package exposed.hydrogen.resources;

import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.spigot.SpigotChameleon;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class ResourcesSpigot extends JavaPlugin {
    @Getter private static ResourcesSpigot instance;
    private SpigotChameleon chameleon;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new ResourcePackSendListener(), this);
        try {
            chameleon = new SpigotChameleon(Resources.class, this, Resources.getPluginData());
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
        }
        Resources.setResourcePackPath(Path.of(Bukkit.getResourcePack()));
        // Download resource pack and start resource pack server asynchronously, this would otherwise block the server during startup.
        Thread download = new Thread(() -> {
            ResourcePackHandler resourcePackHandler;
            try {
                resourcePackHandler = new ResourcePackHandler(new URL(Resources.getResourcePackPath().toString()));

                chameleon.getLogger().info("Downloaded resource pack successfully.");
                chameleon.getLogger().info("Bukkit Hash:" + Bukkit.getResourcePackHash() + " " +
                        "| Resource Pack Hash:" + resourcePackHandler.getResourcePack().hash());
                chameleon.getLogger().info("Resource Pack Size: " + resourcePackHandler.getResourcePack().bytes().length + " bytes");
                chameleon.getLogger().info("Starting resource pack server...");
            } catch (IOException | NoSuchAlgorithmException e) {
                chameleon.getLogger().error("Failed to download or generate empty resource pack.", e);
                return;
            }
            Resources.setResourcePackHandler(resourcePackHandler);
            resourcePackHandler.isResourcePackDownloaded = true;
            Resources.startResourcePackServer();
        });
        download.start();
        MinecraftServer.getServer().a("", "");
        chameleon.onEnable();
    }

    @Override
    public void onDisable() {
        chameleon.onDisable();
    }
}
