/*
 * Copyright (c) 2022.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        RESOURCE_PACK_DOWNLOAD_URL = "http://" + (address.equals("0.0.0.0") ? publicIP : address) + ":" + port;

        File userSetPack = ResourcePackHandler.USER_SET_PACK_DIR.toFile();
        if(userSetPack.exists()){
            try {
                resourcePackHandler = new ResourcePackHandler(Util.getPackFromFile(userSetPack));
                startResourcePackServer();
                return;
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            }
        } else {
            chameleon.getLogger().info("No user set resource pack found");
        }
        try {
            resourcePackHandler = new ResourcePackHandler();
            startResourcePackServer();
        } catch (IOException | NoSuchAlgorithmException e) {
            chameleon.getLogger().error("Failed to generate empty resource pack.", e);
            return;
        }
        startResourcePackServer();
        return;
    }

    @Override
    public void onDisable() {
        resourcePackServerHandler.getServer().stop(0);
    }

    public void startResourcePackServer() {
        resourcePackServerHandler = new ResourcePackServerHandler(address, port.intValue(), resourcePackHandler);

        chameleon.getLogger().info("Resource pack server started. Address: " + resourcePackServerHandler.getServer().httpServer().getAddress());
    }

    static {
        pluginData = new PluginData(
                "Resources",
                "1.2.0",
                "Resource pack compiler and server",
                "hydrogen.exposed",
                List.of("hydrogen"),
                "[Resources]",
                List.of(PluginData.Platform.MINESTOM, PluginData.Platform.SPIGOT)
        );
    }

}
