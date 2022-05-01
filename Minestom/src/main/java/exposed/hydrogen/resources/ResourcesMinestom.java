package exposed.hydrogen.resources;

import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.minestom.MinestomChameleon;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
            String bytes = Util.bytesToHex(Resources.getResourcePackHandler().getHash());
            Resources.getChameleon().getLogger().info("Sending resource pack to " + PlainTextComponentSerializer.plainText().serialize(event.getPlayer().getName()) + " with hash " + bytes);
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
