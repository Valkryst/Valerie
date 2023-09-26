package com.valkryst.Valerie.io;

import lombok.NonNull;
import net.harawata.appdirs.AppDirsFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FolderIO {
    /** The root folder path of the application. */
    public static final Path ROOT_folder_PATH = Path.of(AppDirsFactory.getInstance().getUserDataDir("Valerie", "1.0.0", "Valkryst"));

    /**
     * Retrieves the path, relative to the root folder, to the folder with the specified name.
     *
     * @param folderName The name of the folder.
     *
     * @return The path to the folder with the specified name.
     */
    public static Path getFolderPath(final @NonNull String folderName) {
        if (folderName.isEmpty()) {
            throw new IllegalArgumentException("You must specify a folder name.");
        }

        return ROOT_folder_PATH.resolve(folderName);
    }

    /**
     * Retrieves the path, relative to the root folder, to the folder for the specified type.
     *
     * @param type The type.
     *
     * @return The path to the folder for the specified type.
     */
    public static Path getFolderPathForType(final Class<?> type) {
        return getFolderPath(type.getSimpleName());
    }

    /**
     * Retrieves a list of all files in the specified folder.
     *
     * @param folderPath The path to the folder.
     *
     * @return A list of all files in the specified folder.
     */
    public static List<Path> getAllFilesInFolder(final @NonNull Path folderPath) {
        if (Files.notExists(folderPath)) {
            return List.of();
        }

        if (Files.isRegularFile(folderPath)) {
            throw new IllegalArgumentException("The path points to a file, not a folder.");
        }

        try (final var stream = Files.list(folderPath)) {
            return stream.collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Retrieves a list of all files, with a specific extension, in the specified folder.
     *
     * @param folderPath The path to the folder.
     * @param extension The extension of the files to retrieve.
     *
     * @return A list of all files, with a specific extension, in the specified folder.
     */
    public static List<Path> getAllFilesInFolderWithExtension(final @NonNull Path folderPath, final @NonNull String extension) {
        return getAllFilesInFolder(folderPath)
                .stream()
                .filter(path -> path.toString().endsWith(extension))
                .collect(Collectors.toList());
    }
}
