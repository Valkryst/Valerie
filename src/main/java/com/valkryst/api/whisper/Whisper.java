package com.valkryst.api.whisper;

import lombok.Getter;
import lombok.NonNull;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Whisper {
    /** The path to the audio file. */
    @Getter private final Path filePath;

    /** The transcribed text. */
    private String transcription;

    /**
     * Constructs a new {@code Whisper} object.
     *
     * @param filePath The path to the audio file.
     *
     * @throws UnsupportedAudioFileException If the path does not point to a valid audio file supported by the system.
     * @throws IllegalArgumentException If there is an issue with the file.
     * @throws IOException If an I/O exception occurs.
     */
    public Whisper(final @NonNull Path filePath) throws UnsupportedAudioFileException, IllegalArgumentException, IOException {
        validateAudioFile(filePath);
        this.filePath = filePath;
    }

    /**
     * Validates the specified audio file.
     *
     * @param filePath The path to the audio file.
     *
     * @throws UnsupportedAudioFileException If the path does not point to a valid audio file supported by the system.
     * @throws IllegalArgumentException If there is an issue with the file.
     * @throws IOException If an I/O exception occurs.
     */
    public void validateAudioFile(final @NonNull Path filePath) throws UnsupportedAudioFileException, IllegalArgumentException, IOException {
        if (!filePath.toFile().exists()) {
            throw new IllegalArgumentException("The file does not exist.");
        }

        if (!filePath.toFile().isFile()) {
            throw new IllegalArgumentException("The path does not point to a file.");
        }

        if (!filePath.toFile().canRead()) {
            throw new IllegalArgumentException("The file cannot be read.");
        }

        if (!filePath.toString().endsWith(".wav")) {
            throw new IllegalArgumentException("The file is not a WAV file.");
        }

        if (filePath.toFile().length() == 0) {
            throw new IllegalArgumentException("The file is empty.");
        }

        // todo Validate whether Whisper actually requires the file to be 16-bit, 16kHz, and mono.
        try (final var inputStream = AudioSystem.getAudioInputStream(filePath.toFile())) {
            final var format = inputStream.getFormat();

            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                throw new UnsupportedAudioFileException("The WAV file is not in signed PCM format.");
            }

            if (format.getSampleSizeInBits() != 16) {
                throw new UnsupportedAudioFileException("The WAV file does not have 16 bits per sample.");
            }

            if (format.getSampleRate() != 16000) {
                throw new UnsupportedAudioFileException("The WAV file is not sampled at 16kHz.");
            }

            if (format.getChannels() != 1) {
                throw new UnsupportedAudioFileException("The WAV file is not mono.");
            }
        }
    }

    /**
     * Transcribes the specified audio file.
     *
     * @return The transcribed text.
     *
     * @throws InterruptedException If a thread is interrupted.
     * @throws IOException If an I/O exception occurs.
     */
    public synchronized String getTranscription() throws IOException, InterruptedException {
        if (transcription != null) {
            return transcription;
        }

        throw new UnsupportedOperationException("This method has not been implemented yet.");
    }

    /**
     * Sets the transcription.
     *
     * @param transcription The transcribed text.
     */
    protected void setTranscription(final @NonNull String transcription) {
        this.transcription = transcription;
    }
}
