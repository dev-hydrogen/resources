package exposed.hydrogen.resources;

import lombok.Getter;
import team.unnamed.creative.server.ResourcePackServer;

import java.io.IOException;

public class ResourcePackServerHandler {
    @Getter private ResourcePackServer server;
    @Getter private final String address;
    @Getter private final int port;
    private final ResourcePackHandler handler;

    public ResourcePackServerHandler(String address, int port, ResourcePackHandler handler) {
        this.address = address;
        this.port = port;
        this.handler = handler;
        if(!handler.isResourcePackDownloaded) {
            Resources.getChameleon().getLogger().info("Starting server without resource pack downloaded...");
        }
        start();
    }


    public void start() {
        if(server != null) {
            server.stop(0);
        }
        try {
            server = ResourcePackServer.builder()
                    .address(address, port)
                    .pack(handler.getResourcePack())
                    .build();
        } catch (IOException e) {
            Resources.getChameleon().getLogger().error("Failed to start resource pack server.", e);
        }
        server.start();
    }
}
