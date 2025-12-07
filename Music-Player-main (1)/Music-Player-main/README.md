
# MP3MusicPlayer

## Project Overview

The MP3MusicPlayer is a Java-based application designed to provide users with a simple, efficient, and intuitive interface to play MP3 files. It utilizes object-oriented programming principles and incorporates multiple design patterns and features such as exception handling, file handling, and a graphical user interface (GUI).

### Features
* 🎶 Play an MP3 File (Song).
* 🎙 Display Title of Song.
* 👤 Display Artist of Song.
* ⏳ Display the Length of the Song.
* ⏪ Playback Functionality.
* ⏸ Pause Song.
* ▶ Resume Song.
* ⏭ Go to next Song in a playlist.
* ⏮ Go to previous Song in a playlist.
* 📃 Create a Custom Playlist.
* 🔃 Load a Custom Playlist.

## How It's Made
### Object-Oriented Programming Principles
The following Object-Oriented Programming principles are applied in this project:

1. **Abstraction**: Key details of the music player functionalities are hidden, and only necessary methods are exposed, such as `play()`, `pause()`, and `stop()`.
2. **Encapsulation**: The internal state of each object (like `Song` or `Playlist`) is protected by private fields and is accessed or modified through public getter and setter methods.
3. **Inheritance**: The `Playlist` class inherits from a base `Collection` class (or similar abstract class) that defines common behaviors for collections of items.
4. **Polymorphism**: Different song types (e.g., different audio formats) can be handled polymorphically under a common interface.

## Exception Handling
The application ensures robustness by incorporating exception handling mechanisms. Key features include:

* Throwing and catching exceptions when loading an invalid MP3 file.
* Handling invalid input and error states gracefully.

## File Handling
The **FileManager** class provides functionality to:
* Store and load MP3 playlists from text or binary files.
* Support for saving user preferences for later sessions.

## Graphical User Interface (GUI)
The **GUI** is designed to be user-friendly and intuitive. It provides buttons for basic actions such as play, pause, and stop, as well as a playlist manager. The interface will display the current song and allow users to select different songs from a list.

## Design Pattern
The **MVC (Model-View-Controller)** design pattern is implemented to separate the application's data (Model), user interface (View), and control logic (Controller). This helps make the application more maintainable and scalable. Additionally, the **Singleton** pattern is used in the MusicPlayer class to ensure that only one instance of the player exists at a time.


## Code Structure

### Core Classes

* **`Song`**: Represents a single song with attributes like title, artist, and album. Also handles loading MP3 file details.
* **`Playlist`**: Manages a collection of songs. It supports adding, removing, and listing songs.
* **`MusicPlayer`**: The main class that controls music playback, and follows the Singleton pattern to ensure only one instance is active.
* **`FileManager`**: Handles file operations such as saving and loading playlists and user preferences.
* **`GUI`**: The graphical user interface for interacting with the music player.
(changes mya aplaly)

### Key Methods

* **`play()`**: Starts playback of the current song.
* **`pause()`**: Pauses the current song.
* **`stop()`**: Stops the current song and resets the playback.
* **`addSong()`**: Adds a song to the current playlist.
* **`removeSong()`**: Removes a song from the playlist.
* **(sample, for further changes).
