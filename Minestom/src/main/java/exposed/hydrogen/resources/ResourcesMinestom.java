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

import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.minestom.MinestomChameleon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.resourcepack.ResourcePack;

import java.nio.file.Path;

public class ResourcesMinestom extends Extension {
    private MinestomChameleon chameleon;

    @Override
    public LoadStatus initialize() {
        Resources.setResourcePackPath(Path.of(""));
        try {
            chameleon = new MinestomChameleon(Resources.class, this, Resources.getPluginData());
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
            return LoadStatus.FAILED;
        }
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, event -> {
            if(Resources.getResourcePackHandler() == null || Resources.getResourcePackServerHandler() == null) return;

            String bytes = Util.bytesToHex(Resources.getResourcePackHandler().getHash());
            Resources.getChameleon().getLogger().info("Sending resource pack to " + event.getPlayer().getUsername() + " with hash " + bytes);
            event.getPlayer().setResourcePack(Resources.isResourcePackRequired() ?
                   ResourcePack.forced(Resources.RESOURCE_PACK_DOWNLOAD_URL,
                           bytes) :
                   ResourcePack.optional(Resources.RESOURCE_PACK_DOWNLOAD_URL, bytes));
        });
        return LoadStatus.SUCCESS;
    }

    @Override
    public void terminate() {
        chameleon.onDisable();
    }
}
