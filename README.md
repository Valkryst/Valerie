Valerie was intended to be an advanced interface for OpenAI's ChatGPT, which allows users to create named _personalities_
and chat with them. This allows for more personalized and context-aware conversations. All data is saved locally.

This project is not currently being worked on, but I may return to it in the future.

## Features

### Audio Communication

* Speak into your microphone to transcribe audio into text, instead of typing it out.
* Listen to ChatGPT's response, using ElevenLab's text-to-speech API.
  * From what I recall, this was implemented earlier on, but it was removed pending a rewrite. 

### Chat Interface

* Code language detection and syntax highlighting.
* Named chat sessions for better organization. Each personality has its own chat history.

### Personalities

* Create named _personalities_ to guide ChatGPT's responses.
* Each personality has its own set of saved chats, for easy retrieval and continuation.

## Usage

1. Clone the repository.
2. Add `OPEN_AI_API_KEY` to your environment variables.
3. Run the program from `src/main/java/com/valkryst/Main.java`.

## Credits & Inspiration

* https://plugins.jetbrains.com/plugin/12275-dracula-theme