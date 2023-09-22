package com.valkryst.api.whisper;

import lombok.NonNull;
import okhttp3.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Path;

public class WhisperOpenAI extends Whisper {
    /** The API key. */
    private static final String API_KEY = System.getenv("OPEN_AI_API_KEY");

    /** The HTTP client. */
    private static final OkHttpClient CLIENT = new OkHttpClient();

    /**
     * Constructs a new {@code WhisperOpenAI} object.
     *
     * @param filePath The path to the audio file.
     *
     * @throws UnsupportedAudioFileException If the path does not point to a valid audio file supported by the system.
     * @throws IllegalArgumentException If there is an issue with the file or the API key.
     * @throws IOException If an I/O exception occurs.
     */
    public WhisperOpenAI(@NonNull Path filePath) throws UnsupportedAudioFileException, IllegalArgumentException, IOException {
        super(filePath);
        validateAPIKey();
    }

    /**
     * Validates the API key.
     *
     * @throws IllegalArgumentException If there is an issue with the API key.
     */
    private void validateAPIKey() {
        if (API_KEY == null) {
            throw new IllegalArgumentException("The API key is null.");
        }

        if (API_KEY.isEmpty()) {
            throw new IllegalArgumentException("The API key is empty.");
        }
    }

    @Override
    public void validateAudioFile(@NonNull Path filePath) throws UnsupportedAudioFileException, IllegalArgumentException, IOException {
        super.validateAudioFile(filePath);

        if (filePath.toFile().length() > 25000000) {
            throw new IllegalArgumentException("The file is too large. The file must be less than or equal to 25MB.");
        }
    }

    @Override
    public String getTranscription() throws IOException, InterruptedException {
        final var filePath = super.getFilePath();

        final var requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filePath.getFileName().toString(), RequestBody.create(filePath.toFile(), MediaType.parse("audio/wav")))
                .addFormDataPart("model", "whisper-1")
                .addFormDataPart("response_format", "text")
                .addFormDataPart("language", "auto")
                .build();

        final var request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        try (final var response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed with status code " + response.code() + ":" + System.lineSeparator() + response.body().string());
            }

            System.out.println(response);

           super.setTranscription(response.body().string());
        }

        return super.getTranscription();
    }
}
