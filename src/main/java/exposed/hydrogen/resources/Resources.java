package exposed.hydrogen.resources;

import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public final class Resources extends JavaPlugin {
    @Getter private static Resources instance;
    @Getter private static ResourcePackHandler resourcePackHandler;
    @Getter private static ResourcePackServerHandler resourcePackServerHandler;
    @Getter private static String publicIP;

    @Override
    public void onLoad() {
        instance = this;

        this.saveDefaultConfig();
        if (new File(this.getDataFolder().getAbsolutePath() + "/temp").mkdirs()) {
            this.getLogger().info("Created temp folder");
        }

        String address = getConfig().getString("address");
        Number port = getConfig().getObject("port", Number.class);
        String resourcePackPath = Bukkit.getResourcePack();

        if (address == null || port == null) {
            getLogger().severe("Invalid configuration! Please check your config.yml!");
            return;
        }

        // https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            publicIP = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance.getLogger().info("Public IP: " + publicIP);

        Thread server = new Thread(() -> {
            resourcePackServerHandler = new ResourcePackServerHandler(address, port.intValue(), resourcePackHandler);

            this.getLogger().info("Resource pack server started. Address: " + resourcePackServerHandler.getServer().httpServer().getAddress());
            // Disable server.properties resource pack
            MinecraftServer.getServer().a("http://" + publicIP + ":" + port, "");
        });

        if (resourcePackPath.isEmpty()) {
            this.getLogger().info("No server resource pack found");
            server.start();
            return;
        }

        // Download resource pack and start resource pack server asynchronously, this would otherwise block the server during startup.
        Thread download = new Thread(() -> {
            try {
                resourcePackHandler = new ResourcePackHandler(new URL(resourcePackPath));

                this.getLogger().info("Downloaded resource pack successfully.");
                this.getLogger().info("Bukkit Hash:" + Bukkit.getResourcePackHash() + " " +
                        "| Resource Pack Hash:" + resourcePackHandler.getResourcePack().hash());
                this.getLogger().info("Resource Pack Size: " + resourcePackHandler.getResourcePack().bytes().length + " bytes");
                this.getLogger().info("Starting resource pack server...");
            } catch (IOException | NoSuchAlgorithmException e) {
                this.getLogger().log(java.util.logging.Level.SEVERE, "Failed to download resource pack.", e);
            }
            resourcePackHandler.isResourcePackDownloaded = true;
            server.start();
        });
        download.start();
    }

    public void onEnable() {
        // register events
        getServer().getPluginManager().registerEvents(new ResourcePackSendListener(), this);
    }

    @Override
    public void onDisable() {
        Thread shutdownserver = new Thread(() -> {
            resourcePackServerHandler.getServer().stop(2);
        });
        shutdownserver.start();
    }

}
