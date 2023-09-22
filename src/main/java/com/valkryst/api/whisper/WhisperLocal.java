package com.valkryst.api.whisper;

import com.valkryst.display.model.WhisperSettingsModel;
import lombok.Getter;
import lombok.NonNull;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WhisperLocal extends Whisper {
    /** The executable output of the most recent run of Whisper. */
    @Getter private final StringBuilder processOutput = new StringBuilder();

    /**
     * Constructs a new {@code WhisperLocal} object.
     *
     * @param filePath The path to the audio file.
     *
     * @throws UnsupportedAudioFileException If the path does not point to a valid audio file supported by the system.
     * @throws IllegalArgumentException If there is an issue with the file.
     * @throws IOException If an I/O exception occurs.
     */
    public WhisperLocal(@NonNull Path filePath) throws UnsupportedAudioFileException, IllegalArgumentException, IOException {
        super(filePath);
    }

    /**
     * Constructs the command to run Whisper.
     *
     * @param usingWSL Whether to update the command to use WSL.
     *
     * @return The command to run Whisper.
     */
    private List<String> constructProcessCommand(final boolean usingWSL) {
        final var whisperSettings = WhisperSettingsModel.getInstance();

        final var command = new ArrayList<String>();

        if (usingWSL) {
            command.add("wsl");
            command.add("\"" + getWSLPathString(whisperSettings.getExecutablePath()) + "\"");
        } else {
            command.add("'" + whisperSettings.getExecutablePath() + "'");
        }

        command.add("--language");
        command.add(whisperSettings.getLanguage().getLanguage());

        command.add("--processors");
        command.add(String.valueOf(whisperSettings.getProcessors()));

        command.add("--output-txt");

        command.add("--model");
        if (usingWSL) {
            command.add("\"" + getWSLPathString(whisperSettings.getModelPath()) + "\"");
        } else {
            command.add("'" + whisperSettings.getModelPath() + "'");
        }

        command.add("--file");
        if (usingWSL) {
            command.add("\"" + getWSLPathString(super.getFilePath()) + "\"");
        } else {
            command.add("'" + super.getFilePath().toString() + "'");
        }

        return command;
    }

    @Override
    public String getTranscription() throws IOException, InterruptedException {
        // Run Whisper.
        Process process;
        try {
            process = new ProcessBuilder(constructProcessCommand(false)).redirectErrorStream(true).start();
        } catch (final IOException e) {
            process = new ProcessBuilder(constructProcessCommand(true)).redirectErrorStream(true).start();
        }

        processOutput.delete(0, processOutput.length());

        try (
            final var inputStream = process.getInputStream();
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader)
        ){
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processOutput.append(line).append(System.lineSeparator());
                System.out.println(line);
            }
        }

        int exitValue = process.waitFor();
        processOutput.append("Exit Value is ").append(exitValue);

        // Retrieve the transcription.
        final var outputPath = Paths.get(super.getFilePath().toString() + ".txt");
        if (Files.exists(outputPath)) {
            super.setTranscription(Files.readString(outputPath).trim());
            Files.delete(outputPath);
        }

        return super.getTranscription();
    }

    private String getWSLPathString(final @NonNull Path path) {
        final var driveLetter = path.getRoot().toString().toLowerCase().replace(":", "").replace("\\", "");
        final var folderPath = path.toString().replace(path.getRoot().toString(), "").replace("\\", "/");

        return "/mnt/" + driveLetter + "/" + folderPath;
    }
}
