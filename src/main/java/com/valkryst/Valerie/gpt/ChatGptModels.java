package com.valkryst.Valerie.gpt;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.ModelType;
import lombok.Getter;
import lombok.NonNull;

public enum ChatGptModels {
    GPT_3_5_TURBO("gpt-3.5-turbo", 4096, ModelType.GPT_3_5_TURBO),
    GPT_4("gpt-4", 8192, ModelType.GPT_4),
    GPT_4_32K("gpt-4-32k", 32768, ModelType.GPT_4);

    /** The name of the model. */
    @Getter private final String name;

    /** The maximum number of tokens that can be generated. */
    @Getter private final int maxTokens;

    /** The Tiktoken encoding to use when tokenizing text for the model. */
    @Getter private final Encoding encoding;

    /**
     * Constructs a new {@code ChatGptModel}.
     *
     * @param name The name of the model.
     * @param maxTokens The maximum number of tokens that can be generated.
     * @param modelType The Tiktoken model type.
     */
    ChatGptModels(final @NonNull String name, final int maxTokens, final ModelType modelType) {
        this.name = name;
        this.maxTokens = maxTokens;

        encoding = Encodings.newDefaultEncodingRegistry().getEncodingForModel(modelType);
    }
}
