package org.fiddlemc.fiddle.impl.resourcepack.construct;

import com.google.gson.JsonParser;
import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import org.bukkit.NamespacedKey;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackPath;
import org.jspecify.annotations.Nullable;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The implementation for {@link org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent}.
 */
public final class FiddleResourcePackConstructEventImpl implements PaperLifecycleEvent, FiddleResourcePackConstructEvent {

    private final Map<String, FiddleResourcePackPathImpl> paths = new HashMap<>();

    FiddleResourcePackConstructEventImpl() {
        // Add pack.mcmeta
        getPath("pack.mcmeta").setJsonObjectMutable(JsonParser.parseString("""
            {
              "pack": {
                "pack_format": 75,
                "description": "Fiddle server resource pack"
              }
            }
            """).getAsJsonObject());
    }

    @Override
    public FiddleResourcePackPath getPath(String path) {
        return this.paths.computeIfAbsent(path, $ -> new FiddleResourcePackPathImpl(this, path));
    }

    @Override
    public FiddleResourcePackPath getAssetPath(String directoryName, NamespacedKey key, @Nullable String extension) {
        return this.getPath("assets/" + key.getNamespace() + "/" + directoryName + "/" + key.getKey() + (extension != null ? "." + extension : ""));
    }

    private static final Set<String> DONT_COMPRESS_FILE_EXTENSIONS = Set.of(
        ".png", ".jpg", ".webp", ".gif",
        ".ogg", ".mp3",
        ".zip", ".gz",
        ".mp4", ".webm",
        ".ktx", ".ktx2", ".dds",
        ".bin"
    );

    byte[] buildZip() throws Exception {

        // Create the zip archive
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        zip.setLevel(Deflater.BEST_COMPRESSION);

        // Sort the files (for better compression)
        List<Map.Entry<String, FiddleResourcePackPathImpl>> pathEntries = new ArrayList<>(paths.entrySet());
        pathEntries.sort(Map.Entry.comparingByKey());

        // Add the files
        for (Map.Entry<String, FiddleResourcePackPathImpl> pathEntry : pathEntries) {

            // Skip if no file exists at the path
            FiddleResourcePackPathImpl file = pathEntry.getValue();
            if (!file.exists) {
                continue;
            }

            // Determine whether to compress the data
            String path = pathEntry.getKey();
            byte[] data = file.getBytesImmutable();
            boolean compress;
            if (data.length >= 2097152) {
                // Larger than 2 MB: don't compress
                compress = false;
            } else {
                // Check the extension
                int lastDotIndex = path.lastIndexOf('.');
                if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
                    // No extension, we compress just in case
                    compress = true;
                } else {
                    // We compress, unless the extension is a known compressed or potentially uncompressable format
                    String extension = path.substring(lastDotIndex + 1);
                    compress = !DONT_COMPRESS_FILE_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT));
                }
            }

            // Create the zip entry
            ZipEntry zipEntry = new ZipEntry(path);
            if (compress) {
                zipEntry.setMethod(ZipEntry.DEFLATED);
            } else {
                zipEntry.setMethod(ZipEntry.STORED);
                // If we store, we must calculate some values manually
                zipEntry.setSize(data.length);
                zipEntry.setCompressedSize(data.length);
                CRC32 crc = new CRC32();
                crc.update(data);
                zipEntry.setCrc(crc.getValue());
            }
            zip.putNextEntry(zipEntry);
            zip.write(data);
            zip.closeEntry();

        }

        // Return the byte array
        zip.close();
        return outputStream.toByteArray();

    }

}
