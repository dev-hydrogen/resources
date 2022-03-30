package exposed.hydrogen.resources;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.creative.ResourcePack;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

public final class Resources extends JavaPlugin {
    @Getter private static Resources instance;
    @Getter private static ResourcePackHandler resourcePackHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        if(new File(this.getDataFolder().getAbsolutePath() + "/temp").mkdirs()) {
            this.getLogger().info("Created temp folder");
        }

        String resourcePackPath = Bukkit.getResourcePack();

        if(resourcePackPath.isEmpty()) {
            this.getLogger().info("No server resource pack found");
            return;
        }
        try {
            resourcePackHandler = new ResourcePackHandler(new URL(resourcePackPath));
        } catch (IOException | NoSuchAlgorithmException e) {
            this.getLogger().log(java.util.logging.Level.SEVERE, "Failed to download resource pack.", e);
            return;
        }
        this.getLogger().info("Downloaded resource pack successfully.");
        this.getLogger().info("Bukkit Hash:" + Bukkit.getResourcePackHash() + " | Resource Pack Hash:" + resourcePackHandler.getResourcePack().hash());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
