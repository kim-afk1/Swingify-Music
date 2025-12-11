
# Swingify - Music Player Application

A modern, feature-rich music player built with Java Swing, offering an intuitive interface for managing and playing your music collection.

## Project Overview

Swingify is a desktop music player application developed in Java using the Swing framework. The project demonstrates object-oriented programming principles, GUI design patterns, and audio playback functionality. It features a complete user authentication system, music library management, playlist creation, and a polished user interface inspired by Spotify.

# Features

## User Authentication
* Secure login system with username and password
* Create new user accounts with email validation
* Password confirmation during registration
* User session management with logout functionality
* Persistent user data across sessions

## Music Playback
* Play, pause, and stop audio files
* Previous and next track navigation
* Seek functionality with progress slider
* Volume control with visual slider
* Real-time playback progress display
* Formatted time display (current/total duration)
* Repeat functionality 

## Playlist Management
* Create custom playlists
* Add songs to playlists
* View all songs in library
* Delete playlists




# Object-Oriented Programming Principles
The following Object-Oriented Programming principles are applied in this project:

1. **Abstraction**:
   - Shows how AudioPlayer hides complex audio internals
   - Users just call play(), don't need to know about clips and microsecond positioning
     
2. **Encapsulation**:
   - Demonstrates private fields in Song, Playlist, Member
   - Shows controlled access through getters
   - Highlights password protection in Member class
     
3. **Inheritance**:
   - UI dialogs inherit from JDialog → loginWindow, createAccount
   - UI panels inherit from JPanel → MainPanel, SidebarPanel
   - Interface implementation → MainPanel implements PlaybackListener
   - Shows real inheritance in your codebase, not hypothetical examples
     
4. **Polymorphism**:
   - Swing component polymorphism → All components treated as *Component* for layout

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
This program utilizes:
* **Singleton** - AudioPlayer & MusicLibrary with implementation code
* **Observer** - Listener interfaces with notification mechanism

### Core Classes

* **`MusicPlayerApp`**: The entry point of the application. Initializes the member system with a default admin account and launches the login window. Controls the application's startup flow.
* **`Mp3PlayerGUI`**: The main application window (JFrame). Manages the overall layout with top user info panel, sidebar, and main playback panel. Handles user login state and logout functionality. Acts as the central hub connecting all UI components.
* **`Member`**: Represents a user account with username, email, and password. Handles login authentication.
* **`MemberList`**: Manages the collection of all user accounts. Provides methods to add members and validate login credentials.
* **`AudioPlayer (Singleton)`**: The low-level audio engine. Handles actual audio file playback using Java's Sound API. Manages play/pause/stop operations, volume control, track position, and notifies listeners of state changes. Only supports WAV files.
* **`PlaybackController`**: The high-level playback logic layer. Manages playlists, track navigation (next/previous), shuffle/repeat modes, and coordinates between the AudioPlayer and UI. Acts as the "brain" controlling what plays and when.
* **`MusicLibrary (Singleton)`**: The central data repository. Stores all uploaded songs and user-created playlists. Notifies listeners when the library changes (songs added, playlists created/deleted).
* **`Song`**: Simple data model representing a music track with a name and file reference.
* **`Playlist`**: Collection of songs grouped by user. Can add/remove songs and has a name identifier.
