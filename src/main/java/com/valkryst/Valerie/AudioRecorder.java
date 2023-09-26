package com.valkryst.Valerie;

import com.valkryst.Valerie.io.FileIO;
import com.valkryst.Valerie.io.FolderIO;
import lombok.Getter;
import lombok.NonNull;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AudioRecorder {
    /** The audio format to use for recording. */
    private final static AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, true);

    /** The audio input device to use for recording. */
    private final TargetDataLine lineIn;

    /** The path to the file that the audio is being recorded to. */
    private Path outputFilePath;

    /** The thread that is recording audio. */
    private Thread recordingThread;

    /** Whether the recorder is currently recording. */
    @Getter private boolean isRecording = false;

    public AudioRecorder(final @NonNull String inputSourceName) throws LineUnavailableException {
        if (inputSourceName.isEmpty()) {
            throw new IllegalArgumentException("The input source name cannot be empty.");
        }

        this.lineIn = getLineIn(inputSourceName);
    }

    /**
     * Starts recording audio.
     *
     * @throws IOException If an I/O exception occurs.
     */
    public synchronized void startRecording() throws IOException {
        if (isRecording) {
            throw new IllegalStateException("A recording is already in progress.");
        }

        recordingThread = new Thread(() -> {
            try {
                lineIn.open(AUDIO_FORMAT);
                lineIn.start();
                isRecording = true;

                final var folderPath = FolderIO.getFolderPathForType(AudioRecorder.class);
                outputFilePath = FileIO.getFilePath(folderPath, UUID.randomUUID() + ".wav");

                FileIO.saveAudioToDisk(outputFilePath, lineIn);
            } catch (final LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        });

        recordingThread.start();
    }

    /** Stops recording audio. */
    public synchronized Path stopRecording() throws InterruptedException {
        if (!isRecording) {
            throw new IllegalStateException("No recording is in progress.");
        }

        System.out.println("Stopping recording...");
        lineIn.stop();
        lineIn.close();

        System.out.println("Waiting for recording thread to finish...");
        System.out.println("Recording thread finished.");
        recordingThread.join();

        isRecording = false;

        return outputFilePath;
    }

    /**
     * Returns the line used to record audio.
     *
     * @param inputSourceName The name of the input source to use.
     *
     * @return The line used to record audio.
     *
     * @throws LineUnavailableException If the line is not available due to resource restrictions
     */
    private TargetDataLine getLineIn(final @NonNull String inputSourceName) throws LineUnavailableException {
        final var dataLineInfo = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            throw new IllegalStateException("The input source does not support the following audio format: " + AUDIO_FORMAT);
        }

        Mixer mixer = null;
        for (final var mixerInfo : AudioSystem.getMixerInfo()) {
            if (mixerInfo.getName().equals(inputSourceName)) {
                mixer = AudioSystem.getMixer(mixerInfo);
                break;
            }
        }

        return (TargetDataLine) mixer.getLine(dataLineInfo);
    }

    /**
     * Retrieves the names of all available input sources.
     *
     * @return The names of all available input sources.
     */
    public static List<String> getInputSourceNames() {
        final var sources = new ArrayList<String>();

        for (final var mixerInfo : AudioSystem.getMixerInfo()) {
            final var lineInfos = AudioSystem.getMixer(mixerInfo).getTargetLineInfo();

            for (final var info : lineInfos) {
                if (info.getLineClass().equals(TargetDataLine.class)) {
                    sources.add(mixerInfo.getName());
                    break;
                }
            }
        }

        return sources;
    }
}
