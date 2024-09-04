import fi.iki.elonen.NanoHTTPD;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.List;

public class NumberGuessingGameWeb extends NanoHTTPD {

    private int targetNumber;
    private int numberOfGuesses;
    private final int maxGuesses = 5; // Maximum number of guesses allowed
    
    public NumberGuessingGameWeb() throws IOException {
        super(8080);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started, navigate to http://localhost:8080/");
        openBrowser();
        resetGame();
    }

    private void resetGame() {
        targetNumber = (int) (Math.random() * 100) + 1;
        numberOfGuesses = 0;
        System.out.println("New target value is: " + targetNumber);
    }

    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><head><style>"
                   + "body { font-family: Arial, sans-serif; background: linear-gradient(to right, #ff7e5f, #feb47b); color: #fff; text-align: center; padding: 50px; }"
                   + "h1 { color: #fff; }"
                   + "form { margin: 20px auto; width: 300px; padding: 20px; background-color: rgba(255, 255, 255, 0.2); box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); border-radius: 10px; }"
                   + "input[type='text'] { width: 100%; padding: 10px; margin: 10px 0; box-sizing: border-box; border-radius: 5px; border: none; }"
                   + "input[type='submit'] { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; cursor: pointer; border-radius: 5px; }"
                   + "input[type='submit']:hover { background-color: #45a049; }"
                   + "p { font-size: 18px; }"
                   + "</style></head><body>";
        msg += "<h1>Number Guessing Game</h1>";
        Map<String, List<String>> parameters = session.getParameters();
        String guessParam = parameters.get("guess") != null ? parameters.get("guess").get(0) : null;
        String targetParam = parameters.get("target") != null ? parameters.get("target").get(0) : null;

        if (targetParam != null) {
            try {
                int newTarget = Integer.parseInt(targetParam);
                targetNumber = newTarget;
                numberOfGuesses = 0; // Reset guesses when target is set
                msg += "<p>Target number has been set to: " + targetNumber + "</p>";
            } catch (NumberFormatException e) {
                msg += "<p>Invalid target number! It must be an integer.</p>";
            }
        }

        if (guessParam == null) {
            msg += "<p>I'm thinking of a number between 1 and 100. Can you guess it? You have " + (maxGuesses - numberOfGuesses) + " guesses left.</p>";
        } else {
            numberOfGuesses++;
            if (numberOfGuesses > maxGuesses) {
                msg += "<p>You've reached the maximum number of guesses! The correct number was: " + targetNumber + "</p>";
                resetGame(); // Reset the game after reaching the limit
            } else {
                try {
                    int guess = Integer.parseInt(guessParam);
                    if (guess < targetNumber) {
                        msg += "<p>Your guess is too low! You have " + (maxGuesses - numberOfGuesses) + " guesses left.</p>";
                    } else if (guess > targetNumber) {
                        msg += "<p>Your guess is too high! You have " + (maxGuesses - numberOfGuesses) + " guesses left.</p>";
                    } else {
                        msg += "<p>Congratulations! You guessed the number: " + targetNumber + "</p>";
                        resetGame(); // Reset the game after a correct guess
                    }
                } catch (NumberFormatException e) {
                    msg += "<p>That's not a valid number! You have " + (maxGuesses - numberOfGuesses) + " guesses left.</p>";
                }
            }
        }

        msg += "<form action='?' method='get'>";
        msg += "Guess: <input type='text' name='guess' placeholder='Enter your guess'><br>";
        msg += "Set Target (optional): <input type='text' name='target' placeholder='Set a new target'><br>";
        msg += "<input type='submit' value='Submit'>";
        msg += "</form>";
        msg += "</body></html>";

        return newFixedLengthResponse(msg);
    }

    public static void main(String[] args) {
        try {
            new NumberGuessingGameWeb();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}
