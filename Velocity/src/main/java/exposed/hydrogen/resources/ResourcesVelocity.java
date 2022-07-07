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

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.velocity.VelocityChameleon;
import dev.hypera.chameleon.platforms.velocity.VelocityPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Duration;
import java.util.logging.Logger;

@Plugin(id = "resources", name = "Resources")
public class ResourcesVelocity implements VelocityPlugin {

    private final ProxyServer server;
    private VelocityChameleon chameleon;
    private final Logger logger;
    private final Path dir;

    @Inject
    public ResourcesVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory ) {
        this.server = server;
        this.logger = logger;
        this.dir = dataDirectory;
    }

    @Subscribe
    public void proxyInit(ProxyInitializeEvent event) {
        Resources.setResourcePackPath(Path.of(""));
        try {
            chameleon = new VelocityChameleon(Resources.class, this, Resources.getPluginData());
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void onPlayerJoin(PlayerChooseInitialServerEvent event) {
        server.getScheduler()
                .buildTask(this, () -> {
                    byte[] bytes = Resources.getResourcePackHandler().getHash();
                    ResourcePackInfo builder = server.createResourcePackBuilder(Resources.RESOURCE_PACK_DOWNLOAD_URL)
                            .setShouldForce(Resources.isResourcePackRequired())
                            .setHash(bytes)
                            .build();

                    Resources.getChameleon().getLogger().info("Sending resource pack to " + event.getPlayer().getUsername());

                    event.getPlayer().sendResourcePackOffer(builder);
                }).delay(Duration.ofMillis(2000)).schedule();
    }

    @Override
    public @NotNull ProxyServer getServer() {
        return server;
    }

    @Override
    public @NotNull Path getDataDirectory() {
        return dir;
    }
}