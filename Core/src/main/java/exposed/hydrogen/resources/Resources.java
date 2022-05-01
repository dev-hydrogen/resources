package exposed.hydrogen.resources;

import dev.hypera.chameleon.core.Chameleon;
import dev.hypera.chameleon.core.ChameleonPlugin;
import dev.hypera.chameleon.core.data.PluginData;
import dev.hypera.chameleon.features.configuration.impl.YamlConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public final class Resources extends ChameleonPlugin {
    public static String RESOURCE_PACK_DOWNLOAD_URL;

    @Getter private static Resources instance;
    @Getter @Setter private static ResourcePackHandler resourcePackHandler;
    @Getter private static ResourcePackServerHandler resourcePackServerHandler;
    @Getter private static String publicIP;
    @Getter private static final PluginData pluginData;
    @Getter private static YamlConfiguration config;
    @Getter private static Chameleon chameleon;
    @Getter @Setter private static Path resourcePackPath;
    @Getter @Setter private static String resourcePackPrompt;
    @Getter @Setter private static boolean resourcePackRequired;

    @Getter private static String address;
    @Getter private static Number port;

    public Resources(@NotNull Chameleon chamel) {
        super(chamel);
        chameleon = chamel;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (new File(chameleon.getDataFolder().toFile().getAbsolutePath() + "/pack").mkdirs()) {
            chameleon.getLogger().info("Created pack folder");
        }

        config = new YamlConfiguration(chameleon.getDataFolder(), "config.yml", true);
        address = getConfig().getString("address");
        port = getConfig().get("port", Number.class);
        resourcePackRequired = getConfig().getBoolean("forced");

        if (address == null || port == null) {
            chameleon.getLogger().error("Invalid configuration! Please check your config.yml!");
            return;
        }

        publicIP = Util.getPublicIP();
        RESOURCE_PACK_DOWNLOAD_URL = "http://" + publicIP + ":" + port;

        if (resourcePackPath.toString().isEmpty()) {
            chameleon.getLogger().info("No server resource pack found");
            File userSetPack = ResourcePackHandler.USER_SET_PACK_DIR.toFile();
            if(userSetPack.exists()){
                try {
                    resourcePackHandler = new ResourcePackHandler(Util.getPackFromFile(userSetPack));
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            } else{
                chameleon.getLogger().info("No user set resource pack found");
            }
            try {
                resourcePackHandler = new ResourcePackHandler();
            } catch (IOException | NoSuchAlgorithmException e) {
                chameleon.getLogger().error("Failed to generate empty resource pack.", e);
                return;
            }
            startResourcePackServer();
            return;
        }
    }

    @Override
    public void onDisable() {
        resourcePackServerHandler.getServer().stop(0);
    }

    public static void startResourcePackServer() {
        resourcePackServerHandler = new ResourcePackServerHandler(address, port.intValue(), resourcePackHandler);

        chameleon.getLogger().info("Resource pack server started. Address: " + resourcePackServerHandler.getServer().httpServer().getAddress());
        // Disable server.properties resource pack
        // MinecraftServer.getServer().a("http://" + publicIP + ":" + port, "");
    }

    static {
        pluginData = new PluginData(
                "Resources",
                "1.0.0",
                "Resource pack compiler and server",
                "hydrogen.exposed",
                List.of("hydrogen"),
                "[Resources]",
                List.of(PluginData.Platform.MINESTOM, PluginData.Platform.SPIGOT)
        );
    }

}
