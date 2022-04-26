package exposed.hydrogen.resources;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileResource;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.metadata.PackMeta;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;


public class ResourcePackHandler {
    public static final Path RESOURCE_PACK_DIR = new File(Resources.getInstance().getDataFolder().getAbsolutePath() + "/pack/").toPath();
    public static final Path DOWNLOADED_RESOURCE_PACK_DIR = new File(Resources.getInstance().getDataFolder().getAbsolutePath() + "/pack/downloadedpack.zip").toPath();
    @Getter @NotNull private ResourcePack resourcePack;
    @Getter @NotNull private final ResourcePack emptyResourcePack;
    @Getter protected boolean isResourcePackDownloaded = false;
    @Getter private byte[] hash;
    @Getter private final LinkedList<FileResource> resources;
    @Getter private final LinkedList<MetadataPart> metadataParts;

    /**
     * Resource pack handler constructor, generates a empty resource pack.
     */
    public ResourcePackHandler() throws IOException, NoSuchAlgorithmException {
        emptyResourcePack = generateEmptyResourcePack();
        setResourcePack(emptyResourcePack);

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
        emptyResourcePack = generateEmptyResourcePack();
        resources = new LinkedList<>();
        metadataParts = new LinkedList<>();
        try {
            downloadResourcePack(url);
        } catch (IOException e) {
            setResourcePack(emptyResourcePack);
            Resources.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Failed to download resource pack", e);
            return;
        }
        hash = Util.getSHA1HashBytes(DOWNLOADED_RESOURCE_PACK_DIR.toFile());
        resourcePack = new ResourcePack(new FileInputStream(DOWNLOADED_RESOURCE_PACK_DIR.toFile()).readAllBytes(), Util.getSHA1Hash(DOWNLOADED_RESOURCE_PACK_DIR.toFile()));
    }

    /**
     * Sets the resource pack to the given resource pack. This will overwrite the current resource pack.
     * @param resourcePack the resource pack to set
     * @throws IOException if the resource pack could not be written to file, or if the resource pack is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not found (should never happen)
     */
    public void setResourcePack(ResourcePack resourcePack) throws IOException, NoSuchAlgorithmException {
        Validate.isTrue(resourcePack != null, "Resource pack cannot be null");
        this.resourcePack = resourcePack;
        Files.copy(new ByteArrayInputStream(resourcePack.bytes()), RESOURCE_PACK_DIR, StandardCopyOption.REPLACE_EXISTING);
        hash = Util.getSHA1HashBytes(RESOURCE_PACK_DIR.toFile());
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

    protected static void downloadResourcePack(URL url) throws IOException {
        Resources.getInstance().getLogger().info("Downloading resource pack...");

        InputStream packStream = url.openStream();
        Files.copy(packStream, RESOURCE_PACK_DIR, StandardCopyOption.REPLACE_EXISTING);
        packStream.close();
    }

    private ResourcePack generateEmptyResourcePack() {
        return ResourcePack.build(tree -> {
            tree.write(Metadata.builder()
                    .add(PackMeta.of(8, ""))
                    .build());
            tree.write("credits.txt", Writable.bytes("""
                       Generated by Resources using Creative by Team Unnamed
                       """.getBytes()));
        });
    }
    private void compileAndSetResourcePack() {
        try {
            setResourcePack(Util.compileResourcePack(resources, metadataParts));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
