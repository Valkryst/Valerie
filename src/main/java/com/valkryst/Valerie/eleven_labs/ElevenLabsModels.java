package com.valkryst.Valerie.eleven_labs;

import lombok.Getter;
import lombok.NonNull;

public enum ElevenLabsModels {
    ADAM("pNInz6obpgDQGcFmaJgB"),
    ANTONI("ErXwobaYiN019PkySvjV"),
    ARNOLD("VR6AewLTigWG4xSOukaG"),
    BELLA("EXAVITQu4vr4xnSDxMaL"),
    DOMI("AZnzlk1XvdvUeBnXmlld"),
    ELLI("MF3mGyEYCl7XYWbV9V6O"),
    JOSH("TxGEqnHWrfWFTfGW9XjX"),
    RACHEL("21m00Tcm4TlvDq8ikWAM"),
    SAM("yoZ06aMxZJJ28mfd3POQ");

    /** The ID of the model. */
    @Getter private final String voiceId;

    /**
     * Constructs a new {@code ElevenLabsModels}.
     *
     * @param voiceId The ID of the model.
     */
    ElevenLabsModels(final @NonNull String voiceId) {
        this.voiceId = voiceId;
    }
}
