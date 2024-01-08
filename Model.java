import java.awt.Point;
import java.util.HashSet;
import java.util.Set;


public class Model {

    public static final byte EMPTY = 0;
    public static final byte PLAYER = 1;
    public static final byte WALL = 2;
    public static final byte BOX = 3;
    public static final byte DESTINATION = 4;

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private Viewer viewer;
    private PlayMusic music;
    private byte[][] level;    
    private Point player;
    private Set<Point> goals;
    private Set<Point> skipDrawing;
    private LevelLoader levelLoader;
    private int lvl;
    private int direction;
    private boolean isPvP;
    private Model enemy;
    private ClientPvP clientPvP;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        music = new PlayMusic();
        levelLoader = new LevelLoader("79.174.80.83", 7777);        
        clientPvP = new ClientPvP("79.174.80.83", 7778, this);
        // levelLoader = new LevelLoader("localhost", 7777);        
        // clientPvP = new ClientPvP("localhost", 7778, this);
        lvl = 1;
        isPvP = false;
        enemy = null;
        initialize();
    }

    private void initialize() {
        clear();
        level = loadLevel(lvl);
        player = findPlayer();
        goals = createGoals();
        skipDrawing = createSkipDrawing();
        direction = DOWN;
    }

    public Set<Point> getSkipDrawing() {
        return skipDrawing;
    }

    public void chooseLevel(int level) {
        this.lvl = level;
        initialize();
        viewer.resize();
    }


    private Set<Point> createSkipDrawing() {
        Set<Point> result = new HashSet<>();
        
        top_edge:
        for (int x = 0; x < level[0].length; x++) {
            for (int y = 0; y < level.length; y++) {
                if (level[y][x] == EMPTY) {
                    result.add(new Point(x, y));
                } else {
                    continue top_edge;
                }
            }
        }

        bottom_edge:
        for (int x = 0; x < level[level.length - 1].length; x++) {
            for (int y = level.length - 1; y >= 0; y--) {
                if (level[y][x] == EMPTY) {
                    result.add(new Point(x, y));
                } else {
                    continue bottom_edge;
                }
            }
        }

        right_edge:
        for (int y = 0; y < level.length; y++) {
            for (int x = level[y].length - 1; x >= 0; x--) {
                if (level[y][x] == EMPTY) {
                    result.add(new Point(x, y));
                } else {
                    continue right_edge;
                }
            }
        }

        left_edge:
        for (int y = 0; y < level.length; y++) {
            for (int x = 0; x < level[y].length; x++) {
                if (level[y][x] == EMPTY) {
                    result.add(new Point(x, y));
                } else {
                    continue left_edge;
                }
            }
        }

        return result;
    }

    public Set<Point> getGoals() {
        return goals;
    }

    private Set<Point> createGoals() {
        Set<Point> result = new HashSet<>();

        for (int row = 0; row < level.length; row++) {
            for (int column = 0; column < level[row].length; column++) {
                if (level[row][column] == DESTINATION) {
                    result.add(new Point(column, row));
                }
            }
        }

        return result;
    }

    private Point findPlayer() {
        int count = 0;
        Point result = new Point();

        for (int row = 0; row < level.length; row++) {
            for (int column = 0; column < level[row].length; column++) {
                if (level[row][column] == PLAYER) {
                    result = new Point(column, row);
                    count++;
                }
            }
        }

        if (count == 1) {
            return result;
        } else {
            System.out.println("There must be only one player!");
            return new Point(0, 0);
        }
    }

    private void clear() {
        if (goals != null) {
            goals.clear();
            goals = null;
        }

        if (skipDrawing != null) {
            skipDrawing.clear();
            skipDrawing = null;
        }

        if (level != null) {
            for (int row = 0; row < level.length; row++) {
                level[row] = null;
            }
            level = null;
        }
    }

    public byte[][] loadLevel(int level) {
        return levelLoader.load(level);
    }

    public void restartLevel() {
        releaseResources();

        initialize();
    }

    public void releaseResources() {
        goals.clear();
        player = null;
        level = null;
        skipDrawing.clear();
    }


    public void moveLeft() {
        direction = LEFT;
        Point next = new Point(player.x - 1, player.y);
        Point further = new Point(player.x - 2, player.y);
        move(next, further);
    }

    public void moveUp() {
        direction = UP;
        Point next = new Point(player.x, player.y - 1);
        Point further = new Point(player.x, player.y - 2);
        move(next, further);
    }

    public void moveRight() {
        direction = RIGHT;
        Point next = new Point(player.x + 1, player.y);
        Point further = new Point(player.x + 2, player.y);
        move(next, further);
    }

    public void moveDown() {
        direction = DOWN;
        Point next = new Point(player.x, player.y + 1);
        Point further = new Point(player.x, player.y + 2);
        move(next, further);
    }

    private void move(Point next, Point further) {
        if (level[next.y][next.x] == EMPTY || level[next.y][next.x] == DESTINATION) {
            level[next.y][next.x] = PLAYER;
            Point current = player;
            player = next;
            if (goals.contains(current)) {
                level[current.y][current.x] = DESTINATION;
            } else {
                level[current.y][current.x] = EMPTY;
            }

        }  else if (level[next.y][next.x] == BOX && (level[further.y][further.x] == EMPTY || level[further.y][further.x] == DESTINATION)) {
            if(level[further.y][further.x] == DESTINATION) {
                music.playGoal();
            }
            level[next.y][next.x] = PLAYER;
            level[further.y][further.x] = BOX;
            Point current = player;
            player = next;
            if (goals.contains(current)) {
                level[current.y][current.x] = DESTINATION;
            } else {
                level[current.y][current.x] = EMPTY;
            }
        }

        viewer.repaint();
        if (isPvP) {
            clientPvP.updateModel(this);
        }

        if (isWin()) {
            if (isPvP) {
                clientPvP.imWin();
                stopPvP();
            }
            viewer.showWinSplash();
        }
    }

    public void nextLevel() {
        lvl++;
        initialize();
        viewer.resize();
    }

    public byte[][] getGameField() {
        return level;
    }

    private boolean isWin() {
        return goals.stream()
            .filter(point -> level[point.y][point.x] != BOX)
            .count() == 0;
    }

    public void waitPvP() {
        if (!isPvP) {
            clientPvP.start();
        }
        if (!isPvP) {
            viewer.showWaitEnemyDialog();
        }
    }

    public void startPvP() {
        lvl = 1;
        initialize();
        
        enemy = new Model(viewer);
        enemy.loadLevel(1);
        enemy.initialize();

        isPvP = true;
        viewer.closeWaitEnemyDialog();
        viewer.resize();
    }
    
    public void losePvP() {
        viewer.showLosePvPDialog();
        stopPvP();
    }
    
    public void winPvP() {
        viewer.showWinSplash();
        stopPvP();
    }

    public void stopPvP() {
        clientPvP.stop();
        isPvP = false;
        viewer.closeWaitEnemyDialog();
    }

    public boolean isPvP() {
        return isPvP;
    }

    public Model getEnemy() {
        if (enemy != null) {
            return enemy;
        }
        return this;
    }

    // only for enemy
    public void updateGameField(byte[][] enemyGameField) {
        level = enemyGameField;
    }

    public void updateEnemy(String enemyModel) {
        byte[][] enemyGameField = levelLoader.parseLevelFromString(enemyModel);
        enemy.updateGameField(enemyGameField);
    }

    public int getDirection() {
        return direction;
    }

    public void loadButtonSound(){
        music.playButtonSound();
    }

    public void playBGMusic(){
        music.playBackground();
    }

    public void loadButtonHoverSound(){
        music.playButtonHoverSound();
    }
    
    public void mute(){
        music.mute();
    }

    public void unMute(){
        music.unMute();
    }

}
