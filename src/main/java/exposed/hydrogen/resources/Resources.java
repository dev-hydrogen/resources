package exposed.hydrogen.resources;

import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public final class Resources extends JavaPlugin {
    public static String RESOURCE_PACK_DOWNLOAD_URL;

    @Getter private static Resources instance;
    @Getter private static ResourcePackHandler resourcePackHandler;
    @Getter private static ResourcePackServerHandler resourcePackServerHandler;
    @Getter private static String publicIP;

    @Override
    public void onLoad() {
        instance = this;

        this.saveDefaultConfig();
        if (new File(this.getDataFolder().getAbsolutePath() + "/pack").mkdirs()) {
            this.getLogger().info("Created pack folder");
        }

        String address = getConfig().getString("address");
        Number port = getConfig().getObject("port", Number.class);
        String resourcePackPath = Bukkit.getResourcePack();

        if (address == null || port == null) {
            getLogger().severe("Invalid configuration! Please check your config.yml!");
            return;
        }

        publicIP = Util.getPublicIP();
        RESOURCE_PACK_DOWNLOAD_URL = "http://" + publicIP + ":" + port;

        if (resourcePackPath.isEmpty()) {
            this.getLogger().info("No server resource pack found");
            try {
                resourcePackHandler = new ResourcePackHandler();
            } catch (IOException | NoSuchAlgorithmException e) {
                getLogger().log(java.util.logging.Level.SEVERE, "Failed to generate empty resource pack.", e);
                return;
            }
            startResourcePackServer(address, port.intValue());
            return;
        }

        // Download resource pack and start resource pack server asynchronously, this would otherwise block the server during startup.
        Thread download = new Thread(() -> {
            try {
                resourcePackHandler = new ResourcePackHandler(new URL(resourcePackPath));

                getLogger().info("Downloaded resource pack successfully.");
                getLogger().info("Bukkit Hash:" + Bukkit.getResourcePackHash() + " " +
                        "| Resource Pack Hash:" + resourcePackHandler.getResourcePack().hash());
                getLogger().info("Resource Pack Size: " + resourcePackHandler.getResourcePack().bytes().length + " bytes");
                getLogger().info("Starting resource pack server...");
            } catch (IOException | NoSuchAlgorithmException e) {
                getLogger().log(java.util.logging.Level.SEVERE, "Failed to download or generate empty resource pack.", e);
                return;
            }
            resourcePackHandler.isResourcePackDownloaded = true;
            startResourcePackServer(address, port.intValue());
        });
        download.start();
    }

    public void onEnable() {
        // register events
        getServer().getPluginManager().registerEvents(new ResourcePackSendListener(), this);
    }

    @Override
    public void onDisable() {
        resourcePackServerHandler.getServer().stop(0);
    }

    private void startResourcePackServer(String address, int port) {
        resourcePackServerHandler = new ResourcePackServerHandler(address, port, resourcePackHandler);

        this.getLogger().info("Resource pack server started. Address: " + resourcePackServerHandler.getServer().httpServer().getAddress());
        // Disable server.properties resource pack
        // MinecraftServer.getServer().a("http://" + publicIP + ":" + port, "");
        MinecraftServer.getServer().a("", "");
    }

}
