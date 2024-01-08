import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.Socket;

public class LevelLoader {

    public static final byte EMPTY = 0;
    public static final byte PLAYER = 1;
    public static final byte WALL = 2;
    public static final byte BOX = 3;
    public static final byte DESTINATION = 4;

    private final String serverAddress;
    private final int serverPort;

    public LevelLoader(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
    * Loads the data of the specified level from various sources based on the level number.
    *
    * @param level The level number to load.
    * @return A byte matrix representing the level data, or an empty matrix if the data cannot be loaded or parsed.
    */
    public byte[][] load(int level) {
        String textLevel = null;
        switch (level) {
            case 1: case 2: case 3:
                textLevel = loadFromMemory(level);
                break;
            case 4: case 5: case 6:
                textLevel = loadFromFile(level);
                break;
            case 7: case 8: case 9:
                textLevel = loadFromServer(level);
        }

        return (textLevel == null) ? new byte[0][0] : parseLevelFromString(textLevel);
    }

    public String serverLoad(int level) {
        return loadFromFile(level);
    }

    /**
    * Loads the data of the specified level from memory into a string.
    *
    * @param level The specific level number
    * @return The content of the level file as a String.
    */
    private String loadFromMemory(int level) {
        String level1 =
        "22222022222\n" +
        "20002020002\n" +
        "20302220302\n" +
        "20000400002\n" +
        "22200000222\n" +
        "00240104200\n" +
        "22200000222\n" +
        "20000400002\n" +
        "20302220302\n" +
        "20002020002\n" +
        "22222022222\n";

        String level2 =
        "22222222222222\n" +
        "24000000024442\n" +
        "24002030020002\n" +
        "20302000220032\n" +
        "20002000420002\n" +
        "20002030223002\n" +
        "20302000420002\n" +
        "20002000220302\n" +
        "21002000000002\n" +
        "22222222222222\n";

        String level3 =
        "22222222222222222\n" +
        "24000000400000042\n" +
        "20000000000000002\n" +
        "20002003330020002\n" +
        "24002003130020042\n" +
        "20002003330020002\n" +
        "20002000000020002\n" +
        "20000000000000002\n" +
        "24000000400000042\n" +
        "22222222222222222\n";

        String[] levels = { level1, level2, level3 };

        return levels[level - 1];
    }

    /**
    * Loads the data of the specified level from the file into a string.
    * The file is assumed to be located within the "levels" directory of the current working path,
    * and to have a filename pattern of "levelX.sok" where X is the level number.
    *
    * @param level The specific level number for which the file content is to be loaded.
    * @return The content of the level file as a String, or {@code null} if an IOException occurs.
    */
    private String loadFromFile(int level) {
        Path path = Paths.get("levels/level" + level + ".sok");
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException ioe) {
            System.err.println("ERROR LevelLoader.loadFromFile(): " + ioe);
            return null;
        }
    }

    /**
    * Loads the data of the specified level from the server into a string.
    * Sends the level to the server and waits for a single line of response.
    *
    * @param level The level of data to request from the server.
    * @return The line of data received from the server or {@code null} if an I/O error occurs.
    */
    private String loadFromServer(int level) {
        try (Socket socket = new Socket(serverAddress, serverPort);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(level);

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch(IOException ioe) {
            System.out.println("ERROR LevelLoader.loadFromServer(): " + ioe);
            return null;
        }
    }

    /**
    * Parses a given string representation of a level into a 2D byte array.
    * Each line in the input string represents a row in the level,
    * and each character in a line represents a cell in that row.
    *
    * The method assumes that the input string is a valid representation of the level,
    * with each line having the same number of characters, and each character being
    * a digit ('0' to '4').
    *
    * Each character is converted to a byte by subtracting the ASCII value of '0'.
    *
    * @param textLevel The string representation of the level, with rows separated by newline characters.
    * @return A 2D byte array containing the parsed level, where each byte represents the value of a cell.
    */
    public byte[][] parseLevelFromString(String textLevel) {
        String[] rows = textLevel.split("\\r?\n");
        byte[][] result = new byte[rows.length][];

        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            String row = rows[rowIndex];
            result[rowIndex] = new byte[row.length()];

            for (int colIndex = 0; colIndex < row.length(); colIndex++) {
                char symbol = row.charAt(colIndex);
                byte cell = (byte) (symbol - '0');
                if (cell < 0 || cell > 4) {
                    cell = WALL;
                }
                result[rowIndex][colIndex] = cell;
            }
        }

        return result;
    }

    public String parseLevelToString(Model model) {
        byte[][] gameField = model.getGameField();
        StringBuilder builder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < gameField.length; rowIndex++) {
            for (int colIndex = 0; colIndex < gameField[rowIndex].length; colIndex++) {
                byte cell = gameField[rowIndex][colIndex];
                char symbol = (char) (cell + '0');
                builder.append(symbol);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}
