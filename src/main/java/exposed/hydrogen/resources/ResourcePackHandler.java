package exposed.hydrogen.resources;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;


public class ResourcePackHandler {
    public static final Path RESOURCE_PACK_DIR = new File(Resources.getInstance().getDataFolder().getAbsolutePath() + "/temp/downloadedpack.zip").toPath();
    @Getter @NotNull private ResourcePack resourcePack;
    @Getter protected boolean isResourcePackDownloaded = false;
    @Getter private byte[] hash;

    /**
     * Resource pack handler constructor, downloads the resource pack and loads it as ResourcePack.
     * @param url URL of the resource pack
     * @throws IOException if resource pack is not found
     * @throws NoSuchAlgorithmException if algorithm is not found (should never happen)
     */
    public ResourcePackHandler(URL url) throws IOException, NoSuchAlgorithmException {
        downloadResourcePack(url);
        hash = Util.getSHA1HashBytes(RESOURCE_PACK_DIR.toFile());
        resourcePack = new ResourcePack(new FileInputStream(RESOURCE_PACK_DIR.toFile()).readAllBytes(), Util.getSHA1Hash(RESOURCE_PACK_DIR.toFile()));
    }

    public void setResourcePack(ResourcePack resourcePack) {
        Validate.isTrue(resourcePack != null, "Resource pack cannot be null");
        this.resourcePack = resourcePack;
        try {
            Files.copy(new ByteArrayInputStream(resourcePack.bytes()), RESOURCE_PACK_DIR, StandardCopyOption.REPLACE_EXISTING);
            Resources.getResourcePackServerHandler().setPack(resourcePack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void downloadResourcePack(URL url) throws IOException {
        Resources.getInstance().getLogger().info("Downloading resource pack...");

        InputStream packStream = url.openStream();
        Files.copy(packStream, RESOURCE_PACK_DIR, StandardCopyOption.REPLACE_EXISTING);
        packStream.close();
    }

}
