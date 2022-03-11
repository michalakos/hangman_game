# hangman_game
Hangman game written in Java.

## Requirements:
* Java Runtime Environment (JRE) version 11
* A \*nix Operating System

## Usage
Use `./run.sh` in the project's directory to start the application.

## Instructions
### Before you play:
* **Create a dictionary**:<br>
To create a dictionary select `Application->Create`.<br>
In the `DICTIONARY_ID` provide an arbitrary ID for the created dictionary.<br>
In the `OpenLibraryID` provide the Open Library ID of a book.<br>
Click `Create`.
* **Load a dictionary**:<br>
To load a dictionary select `Application->Load` and from the provided list of available dictionaries' ids insert one in 
* the `DICTIONARY_ID` field.
Click `Load`.<br>
In order to load a dictionary you must first create it.
* **Start a game**:<br>
To start a game select `Application->Start`.<br>
In order to start a game you must first load a dictionary.
* **Dictionary stats**:<br>
To see a dictionary's stats select `Details->Dictionary`.<br>
To do this you must first load the dictionary.
* **Previous results**:<br>
To see the last 5 games' results select `Details->Rounds`.
* **Surrender**:<br>
To surrender select `Details->Solution`.<br>
In order to surrender a game must be in session.
* **Exiting**:<br>
To exit at any point choose `Appication->Exit`.

### Game instructions:
For each of the word's missing letters a list of possible answers is provided. The earlier a letter appears in the list 
the higher the probability of it being the missing letter. The list of missing letters updates after each guess. To 
guess a letter simply type the letter in the `Insert character` field and the position to which you think it belongs in 
the field `Select position` and then click `Submit`. You have 6 tries before you lose. You get 5 points if you make a 
correct guess for a letter with a probability greater or equal to 60%, 10 points for a letter with a probability between
 40% and 60%, 15 points for probabilities between 25% and 40% and 30 points for letters with a probability less than 25%.
You lose 15 points for each incorrect guess. The minimum number of points you can have is 0.