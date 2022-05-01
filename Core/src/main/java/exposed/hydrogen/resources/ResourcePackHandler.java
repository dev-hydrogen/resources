package exposed.hydrogen.resources;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.file.FileResource;
import team.unnamed.creative.metadata.MetadataPart;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;


public class ResourcePackHandler {
    public static final Path USER_SET_PACK_DIR = new File(Resources.getChameleon().getDataFolder().toFile().getAbsolutePath() + "/pack/pack.zip").toPath();
    public static final Path RESOURCE_PACK_DIR = new File(Resources.getChameleon().getDataFolder().toFile().getAbsolutePath() + "/pack/generatedpack.zip").toPath();
    public static final Path DOWNLOADED_RESOURCE_PACK_DIR = new File(Resources.getChameleon().getDataFolder().toFile().getAbsolutePath() + "/pack/downloadedpack.zip").toPath();
    @Getter @NotNull private ResourcePack resourcePack;
    @Getter @NotNull private static final ResourcePack emptyResourcePack;
    @Getter protected boolean isResourcePackDownloaded = false;
    @Getter private byte[] hash;
    @Getter private final LinkedList<FileResource> resources;
    @Getter private final LinkedList<MetadataPart> metadataParts;
    @Getter private String credits;

    /**
     * Resource pack handler constructor, generates a empty resource pack.
     */
    public ResourcePackHandler() throws IOException, NoSuchAlgorithmException {
        setResourcePack(emptyResourcePack,false);

        hash = Util.getSHA1HashBytes(emptyResourcePack.bytes());
        resources = new LinkedList<>();
        metadataParts = new LinkedList<>();
    }
    /**
     * Resource pack handler constructor, downloads the resource pack and loads it as ResourcePack.
     * @param url URL of the resource pack
     * @throws IOException if resource pack is not found
     * @throws NoSuchAlgorithmException if algorithm is not found (should never happen)
     */
    public ResourcePackHandler(URL url) throws IOException, NoSuchAlgorithmException {
        resources = new LinkedList<>();
        metadataParts = new LinkedList<>();
        try {
            downloadResourcePack(url);
        } catch (IOException e) {
            setResourcePack(emptyResourcePack,false);
            hash = Util.getSHA1HashBytes(emptyResourcePack.bytes());
            Resources.getChameleon().getLogger().error("Failed to download resource pack, resource pack is set as empty", e);
            return;
        }
        hash = Util.getSHA1HashBytes(DOWNLOADED_RESOURCE_PACK_DIR.toFile());
        resourcePack = new ResourcePack(new FileInputStream(DOWNLOADED_RESOURCE_PACK_DIR.toFile()).readAllBytes(), Util.getSHA1Hash(DOWNLOADED_RESOURCE_PACK_DIR.toFile()));
    }

    public ResourcePackHandler(ResourcePack resourcePack) throws IOException, NoSuchAlgorithmException {
        setResourcePack(resourcePack,false);
        hash = Util.getSHA1HashBytes(resourcePack.bytes());
        resources = new LinkedList<>();
        metadataParts = new LinkedList<>();
    }

    /**
     * Sets the resource pack to the given resource pack. This will overwrite the current resource pack.
     * @param resourcePack the resource pack to set
     * @throws IOException if the resource pack could not be written to file, or if the resource pack is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not found (should never happen)
     */
    public void setResourcePack(ResourcePack resourcePack, boolean startServer) throws IOException, NoSuchAlgorithmException {
        Validate.isTrue(resourcePack != null, "Resource pack cannot be null");
        this.resourcePack = resourcePack;
        hash = Util.getSHA1HashBytes(this.resourcePack.bytes());
        Files.copy(new ByteArrayInputStream(this.resourcePack.bytes()), RESOURCE_PACK_DIR, StandardCopyOption.REPLACE_EXISTING);
        if(startServer) {
            Resources.getResourcePackServerHandler().start();
        }
    }

    public void addResource(FileResource resource) {
        addResource(resource, false);
    }
    public void addResource(FileResource resource, boolean compile) {
        addResources(List.of(resource), compile);
    }

    public void addMetadataPart(MetadataPart part) {
        addMetadataPart(part,false);
    }
    public void addMetadataPart(MetadataPart part, boolean compile) {
        addMetadataParts(List.of(part), compile);
    }

    public void addResources(List<FileResource> resources) {
        addResources(resources, false);
    }
    public void addResources(List<FileResource> resources, boolean compile) {
        this.resources.addAll(resources);
        if(compile) compileAndSetResourcePack();
    }

    public void addMetadataParts(List<MetadataPart> parts) {
        addMetadataParts(parts, false);
    }
    public void addMetadataParts(List<MetadataPart> parts, boolean compile) {
        metadataParts.addAll(parts);
        if(compile) compileAndSetResourcePack();
    }
    public void addCredit(String credit) {
        credits = credits.concat(credit).concat(System.lineSeparator());
    }

    protected static void downloadResourcePack(URL url) throws IOException {
        Resources.getChameleon().getLogger().info("Downloading resource pack...");

        InputStream packStream = url.openStream();
        Files.copy(packStream, DOWNLOADED_RESOURCE_PACK_DIR, StandardCopyOption.REPLACE_EXISTING);
        packStream.close();
    }

    private static ResourcePack generateEmptyResourcePack() {
        return Util.compileResourcePack(List.of(), List.of(), "");
    }
    private void compileAndSetResourcePack() {
        try {
            setResourcePack(Util.compileResourcePack(resources, metadataParts, credits),true);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    static {
        emptyResourcePack = generateEmptyResourcePack();
    }
}
