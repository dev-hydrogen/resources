package exposed.hydrogen.resources;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.server.ResourcePackServer;

import java.io.IOException;

public class ResourcePackServerHandler {
    @Getter private ResourcePackServer server;
    @Getter private final String address;
    @Getter private final int port;
    private ResourcePack pack;

    public ResourcePackServerHandler(String address, int port, ResourcePackHandler handler) {
        this.address = address;
        this.port = port;
        pack = handler.getResourcePack();
        if(!handler.isResourcePackDownloaded) {
            Resources.getInstance().getLogger().info("Starting server without resource pack downloaded...");
        }
        start();
    }

    protected void setPack(ResourcePack pack) {
        this.pack = pack;
        start();
    }
    public void start() {
        Validate.isTrue(pack != null, "Resource pack is null");
        if(server != null) {
            server.stop(1);
        }
        try {
            server = ResourcePackServer.builder()
                    .address(address, port)
                    .pack(pack)
                    .build();
        } catch (IOException e) {
            Resources.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Failed to start resource pack server.", e);
        }
        server.start();
    }
}
