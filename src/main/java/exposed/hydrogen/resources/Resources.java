package exposed.hydrogen.resources;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.creative.server.ResourcePackServer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public final class Resources extends JavaPlugin {
    @Getter private static Resources instance;
    @Getter private static ResourcePackHandler resourcePackHandler;
    @Getter private static ResourcePackServer resourcePackServer;

    @Override
    public void onLoad() {
        instance = this;

        String address = getConfig().getString("address");
        Number port = getConfig().getObject("port", Number.class);
        if(address == null || port == null) {
            getLogger().severe("Invalid configuration! Please check your config.yml!");
            return;
        }

        this.saveDefaultConfig();
        if(new File(this.getDataFolder().getAbsolutePath() + "/temp").mkdirs()) {
            this.getLogger().info("Created temp folder");
        }

        String resourcePackPath = Bukkit.getResourcePack();

        if(resourcePackPath.isEmpty()) {
            this.getLogger().info("No server resource pack found");
            return;
        }
        // Download resource pack and start resource pack server asynchronously, this would otherwise block the server during startup.
        Thread thread = new Thread(() -> {
            try {
                resourcePackHandler = new ResourcePackHandler(new URL(resourcePackPath));

                this.getLogger().info("Downloaded resource pack successfully.");
                this.getLogger().info("Bukkit Hash:" + Bukkit.getResourcePackHash() + " " +
                        "| Resource Pack Hash:" + resourcePackHandler.getResourcePack().hash());
                this.getLogger().info("Resource Pack Size: " + resourcePackHandler.getResourcePack().bytes().length + " bytes");
                this.getLogger().info("Starting resource pack server...");

                resourcePackServer = ResourcePackServer.builder()
                        .address(address, port.intValue())
                        .pack(resourcePackHandler.getResourcePack())
                        .build();
                resourcePackServer.start();

                this.getLogger().info("Resource pack server started. Address: " + resourcePackServer.httpServer().getAddress());
            } catch (IOException | NoSuchAlgorithmException e) {
                this.getLogger().log(java.util.logging.Level.SEVERE, "Failed to download resource pack.", e);
            }
            resourcePackHandler.isResourcePackDownloaded = true;
        });
        thread.start();
    }

    @Override
    public void onDisable() {
        resourcePackServer.stop(2);

    }
}
