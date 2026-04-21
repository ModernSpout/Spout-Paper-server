package org.fiddlemc.fiddle.impl.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Some utilities for handling JAR files.
 */
public final class JarFileUtil {

    private JarFileUtil() {
        throw new UnsupportedOperationException();
    }

    public static void forEachEntry(Path jarFilePath, JarEntryConsumer consumer) throws IOException {
        forEachEntry(jarFilePath.toFile(), consumer);
    }

    public static void forEachEntry(File jarFile, JarEntryConsumer consumer) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                consumer.accept(entry, jar);
            }
        }
    }

    public static void forEachFileBelowDirectory(Path jarFilePath, String directory, JarFileBelowDirectoryConsumer consumer) throws IOException {
        forEachFileBelowDirectory(jarFilePath.toFile(), directory, consumer);
    }

    public static void forEachFileBelowDirectory(File jarFile, String directory, JarFileBelowDirectoryConsumer consumer) throws IOException {
        forEachEntry(jarFile, (entry, jar) -> {
            String name = entry.getName();
            if ((directory.isEmpty() || name.startsWith(directory + "/")) && !entry.isDirectory()) {
                consumer.accept(entry, jar, directory.isEmpty() ? name : name.substring(directory.length() + 1));
            }
        });
    }

    public interface JarEntryConsumer {

        void accept(JarEntry entry, JarFile jar) throws IOException;

    }

    public interface JarFileBelowDirectoryConsumer {

        /**
         * @param relativePath The relative path below the given directory, without a leading slash.
         */
        void accept(JarEntry entry, JarFile jar, String relativePath) throws IOException;

    }

}
