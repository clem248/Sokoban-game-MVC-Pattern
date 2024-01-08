import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.RenderingHints;

import javax.imageio.ImageIO;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import java.util.Set;

import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;


public class Canvas extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String WIN_IMAGE_PATH = "images/win.jpg";
    private static final String GAMER_UP_IMAGE_PATH = "images/skinUp.png";
    private static final String GAMER_DOWN_IMAGE_PATH = "images/skinDown.png";
    private static final String GAMER_LEFT_IMAGE_PATH = "images/skinLeft.png";
    private static final String GAMER_RIGHT_IMAGE_PATH = "images/skinRight.png";

    private Model model;
    private Image[] imagesGamer;
    private Image imageWall;
    private Image imageBox;
    private Image imageBoxComplete;
    private Image imageGoal;
    private Image imageGoalComplete;
    private Image imagePlat;
    private Image levels;
    private Image restart;
    private Image exit;
    private Image pvp;
    private ImageIcon bgIcon;
    private Image musicButtonOn;
    private Image musicButtonOff;

    private int backgroundOffset = 0;

    private boolean showLevelButtons = false;
    private Image[] levelButtonImages;
    private boolean isMouseOverButton = false;

    private boolean isLevelsButtonPressed = false;
    private boolean isRestartButtonPressed = false;
    private boolean isPvpButtonPressed = false;
    private boolean isExitButtonPressed = false;
    private boolean isMusicButtonPressed = false;
    private boolean[] isLevelButtonPressed = new boolean[9];
    private boolean isMusicOn = false;
    private Point tmpPoint;

    private int screenWidth;
    private int screenHeight;
    private int myImageSize;
    private int enemyImageSize;
    private double myPositionX_Q;
    private double enemyPositionX_Q;
    private double myPositionY_Q;
    private double enemyPositionY_Q;
    private double myWidth_Q;
    private double myHeight_Q;
    private double enemyWidth_Q;
    private double enemyHeight_Q;
    private int myStartX;
    private int myStartY;
    private int enemyStartX;
    private int enemyStartY;
    private double temp;


    public Canvas(Model model) {
        this.model = model;
        tmpPoint = new Point();
        startBackgroundAnimation();

        File fileLevels = new File("images/levels.png");
        File fileRestart = new File("images/RESTART.png");
        File fileExit = new File("images/EXIT.png");
        File filePVP = new File("images/PVP.png");
        File filePlat = new File("images/plat.png");
        File fileWall = new File("images/wall.png");
        File fileBox = new File("images/box.png");
        File fileBoxComplete = new File("images/boxcomplete.png");
        File fileGoal = new File("images/goal.png");
        File fileGoalComplete = new File("images/goalcomplete.png");
        File fileMusicOn = new File("images/musicButtonOff.png");
        File fileMusicOff = new File("images/musicButtonOn.png");

        bgIcon = new ImageIcon("images/bg.jpg");
        levelButtonImages = new Image[9];
        for (int i = 0; i < 9; i++) {
            try {
                levelButtonImages[i] = ImageIO.read(new File("images/lvl" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.out.println("Error loading level button image: " + e);
            }
        }

        try {
            imagesGamer = new Image[4];
            imagesGamer[Model.UP] = ImageIO.read(new File(GAMER_UP_IMAGE_PATH));
            imagesGamer[Model.DOWN] = ImageIO.read(new File(GAMER_DOWN_IMAGE_PATH));
            imagesGamer[Model.LEFT] = ImageIO.read(new File(GAMER_LEFT_IMAGE_PATH));
            imagesGamer[Model.RIGHT] = ImageIO.read(new File(GAMER_RIGHT_IMAGE_PATH));

            imageWall = ImageIO.read(fileWall);
            imageBox = ImageIO.read(fileBox);
            imageBoxComplete = ImageIO.read(fileBoxComplete);
            imageGoal = ImageIO.read(fileGoal);
            imageGoalComplete = ImageIO.read(fileGoalComplete);
            imagePlat = ImageIO.read(filePlat);
            levels = ImageIO.read(fileLevels);
            restart = ImageIO.read(fileRestart);
            exit = ImageIO.read(fileExit);
            pvp = ImageIO.read(filePVP);
            musicButtonOn = ImageIO.read(fileMusicOn);
            musicButtonOff = ImageIO.read(fileMusicOff);

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        if (isWinSplashDrawing) {
            paintWinSplash(g);
        } else {
            drawBackImage(g);
            if (model.isPvP()) {
                drawGameField(g, model, myImageSize, myStartX, myStartY);
                drawGameField(g, model.getEnemy(), enemyImageSize, enemyStartX, enemyStartY);
            } else {
                drawGameField(g, model, myImageSize, myStartX, myStartY);
            }
            drawButtons(g, screenWidth, screenHeight);
        }
    }

    private void drawButtons(Graphics g, int screenWidth, int screenHeight) {
        int buttonWidth = 150;
        int buttonHeight = 60;
        int margin = 20;

        drawButtonWithScale(g, levels, margin, margin, buttonWidth, buttonHeight, isLevelsButtonPressed);

        // Draw RESTART button
        drawButtonWithScale(g, restart, margin, screenHeight - buttonHeight - margin, buttonWidth, buttonHeight, isRestartButtonPressed);

        // Draw PVP button
        drawButtonWithScale(g, pvp, screenWidth - buttonWidth - margin, margin, buttonWidth, buttonHeight, isPvpButtonPressed);

        // Draw EXIT button
        drawButtonWithScale(g, exit, screenWidth - buttonWidth - margin, screenHeight - buttonHeight - margin, buttonWidth, buttonHeight, isExitButtonPressed);

        // Draw MUSIC button
         if (isMusicOn) {
            drawButtonWithScale(g, musicButtonOn, screenWidth / 2 - 64 / 2, screenHeight - 64 - margin, 64, 64, isMusicButtonPressed);
        } else {
            drawButtonWithScale(g, musicButtonOff, screenWidth / 2 - 64 / 2, screenHeight - 64 - margin, 64, 64, isMusicButtonPressed);
        }
        if (showLevelButtons) {
            for (int i = 0; i < 9; i++) {
                int levelButtonX = margin;
                int levelButtonY = margin + buttonHeight + margin + i * (buttonHeight + margin);
                drawButtonWithScale(g, levelButtonImages[i], levelButtonX, levelButtonY, buttonWidth, buttonHeight, isLevelButtonPressed[i]);
            }
        }
    }

    private void drawButtonWithScale(Graphics g, Image image, int x, int y, int width, int height, boolean isButtonPressed) {
        Graphics2D g2d = (Graphics2D) g.create();

        double scaleFactor = isButtonPressed ? 1.05 : 1.0;
        int scaledWidth = (int) (width * scaleFactor);
        int scaledHeight = (int) (height * scaleFactor);

        // Calculate offset to keep the image centered
        int xOffset = (scaledWidth - width) / 2;
        int yOffset = (scaledHeight - height) / 2;

        // Draw the image with the calculated scale and offset
        g2d.drawImage(image, x - xOffset, y - yOffset, scaledWidth, scaledHeight, null);

        g2d.dispose();
    }

        public void resize() {
        byte[][] gameField = model.getGameField();

        Dimension screenSize = getSize();

        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        int a = gameField.length;
        int b = gameField[0].length;

        if(model.isPvP()) {
            // center of field (percent of frame)
            myPositionX_Q = 0.31;
            myPositionY_Q = 0.5;

            enemyPositionX_Q = 0.78;
            enemyPositionY_Q = 0.5;

             // size of field (percent of frame)
            myWidth_Q = 0.55;
            myHeight_Q = 0.95;

            enemyWidth_Q = 0.4;
            enemyHeight_Q = 0.55;

            if(screenWidth*0.9 < screenHeight) {
                temp = 1 - myPositionY_Q;
                myPositionY_Q = 1 - myPositionX_Q;
                myPositionX_Q = temp;

                temp = 1 - enemyPositionY_Q;
                enemyPositionY_Q = 1 - enemyPositionX_Q;
                enemyPositionX_Q = temp;

                temp = myHeight_Q;
                myHeight_Q = myWidth_Q;
                myWidth_Q = temp;

                temp = enemyHeight_Q;
                enemyHeight_Q = enemyWidth_Q;
                enemyWidth_Q = temp;
            }

            enemyImageSize = (int) ((enemyHeight_Q * screenHeight * 10)/(3*(a+b)));

            if(enemyImageSize*(a+b)/2 > screenWidth * enemyWidth_Q) {
                enemyImageSize = (int) ((2*screenWidth * enemyWidth_Q)/(a+b));
            }

            enemyStartX = (int) (screenWidth * enemyPositionX_Q - enemyImageSize*(a-b)/4 - enemyImageSize/2);
            enemyStartY = (int) (screenHeight * enemyPositionY_Q - 3*enemyImageSize*(a+b)/20 - enemyImageSize/5);


        }else if(!model.isPvP()) {
            myPositionX_Q = 0.5;
            myPositionY_Q = 0.5;

            myWidth_Q = 0.9;
            myHeight_Q = 0.75;

        }
        myImageSize = (int) ((myHeight_Q * screenHeight * 10)/(3*(a+b)));

        if(myImageSize*(a+b)/2 > screenWidth * myWidth_Q) {
            myImageSize = (int) ((2*screenWidth * myWidth_Q)/(a+b));
        }

        myStartX = (int) (screenWidth * myPositionX_Q - myImageSize*(a-b)/4 - myImageSize/2);
        myStartY = (int) (screenHeight * myPositionY_Q - 3*myImageSize*(a+b)/20 - myImageSize/5);
    }


    private void drawGameField(Graphics g, Model model, int imageSize, int startX, int startY) {
        byte[][] gameField = model.getGameField();
        Set<Point> goals = model.getGoals();
        Set<Point> skipDrawing = model.getSkipDrawing();

        int shfX = imageSize/2;
        int shfY = imageSize*3/10;

        /**
         * We draw either EMPTY or GOAL as a background
         */
        for (int i = 0; i < gameField.length; i++) {
            for (int j = gameField[i].length - 1; j >= 0; j--) {
                tmpPoint.x = j;
                tmpPoint.y = i;

                // check skip drawing
                if (skipDrawing.contains(tmpPoint)) {
                    continue;
                }

                // paint EMPTY
                Image image = imagePlat;

                // paint GOAL
                if (goals.contains(tmpPoint)) {
                    image = (gameField[tmpPoint.y][tmpPoint.x] == Model.BOX) ? imageGoalComplete : imageGoal;
                }

                int x = startX - shfX * (gameField[i].length - 1 - j - i);
                int y = startY + shfY * (gameField[i].length - 1 - j + i);
                g.drawImage(image, x, y, imageSize, imageSize, null);


                // Draw Player, Box, Wall
                if (gameField[i][j] == Model.PLAYER) {
                    g.drawImage(imagesGamer[model.getDirection()], x, y - imageSize*9/20, imageSize, imageSize, null);
                } else if (gameField[i][j] == Model.WALL) {
                    g.drawImage(imageWall, x, y , imageSize, imageSize, null);
                } else if (gameField[i][j] == Model.BOX) {
                    if(goals.contains(tmpPoint)) {
                        g.drawImage(imageBoxComplete, x, y - imageSize*27/100, imageSize, imageSize, null);
                    }else {
                        g.drawImage(imageBox, x, y - imageSize*27/100, imageSize, imageSize, null);
                    }
                }
            }
        }

    }

    private void drawBackImage(Graphics g) {
        Dimension screenSize = getSize();

        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        int imageWidth = screenWidth;
        int x = backgroundOffset % imageWidth;
        g.drawImage(bgIcon.getImage(), -x, 0, screenWidth, screenHeight, null);

        // Draw the second part of the image to fill the gap
        if (x > 0) {
            g.drawImage(bgIcon.getImage(), screenWidth - x, 0, screenWidth, screenHeight, null);
        } else {
            // Draw the second part to fill the gap when the first part reaches the end
            g.drawImage(bgIcon.getImage(), imageWidth - x, 0, screenWidth, screenHeight, null);
        }
    }

    private void startBackgroundAnimation() {
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundOffset++;
                repaint();

                // Check if the second part of the image reached the end
                int imageWidth = bgIcon.getIconWidth();
                if (backgroundOffset >= imageWidth) {
                    // Reverse the scrolling direction
                    backgroundOffset = 0;
                }
            }
        });
        timer.start();
    }

    /******************************************************
     *                   Win Splash
     ******************************************************/
    private final Image imageWin = loadWinSplashImage(WIN_IMAGE_PATH);
    private boolean isWinSplashDrawing = false;
    private double winSplashScale = 1.0;
    private Timer timer;

    // too JavaScript)))
    private ActionListener timerListener = e -> {
        if (winSplashScale < 1.3) {
            winSplashScale += 0.01;
            repaint();
        } else {
            ((Timer)e.getSource()).stop();
            isWinSplashDrawing = false;
            model.nextLevel();
        }
    };

    public void showWinSplash() {
        isWinSplashDrawing = true;
        winSplashScale = 1.0;

        if (timer == null) {
            timer = new Timer(16, timerListener);
        }

        timer.start();
    }

    private void paintWinSplash(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int width = (int) (winSplashScale * imageWin.getWidth(null));
        int height = (int) (winSplashScale * imageWin.getHeight(null));
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;

        g2d.drawImage(imageWin, x, y, width, height, null);
    }

    private Image loadWinSplashImage(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (IOException ioe) {
            System.out.println("ERROR: Canvas.loadWinSplashImage(): " + ioe);

            // Creating a Blank Image
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public void addCustomListener(Controller controller) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int mouseX = event.getX();
                int mouseY = event.getY();
                int buttonWidth = 150;
                int buttonHeight = 60;
                int margin = 20;

                Rectangle topLeftButtonBounds = new Rectangle(margin, margin, buttonWidth, buttonHeight);
                Rectangle bottomLeftButtonBounds = new Rectangle(margin, screenHeight - buttonHeight - margin, buttonWidth, buttonHeight);
                Rectangle topRightButtonBounds = new Rectangle(screenWidth - buttonWidth - margin, margin, buttonWidth, buttonHeight);
                Rectangle bottomRightButtonBounds = new Rectangle(screenWidth - buttonWidth - margin, screenHeight - buttonHeight - margin, buttonWidth, buttonHeight);
                Rectangle musicButtonBounds = new Rectangle(screenWidth / 2 - 64 / 2, screenHeight - 64 - margin, 64, 64);

                // Check which button is pressed
                if (topLeftButtonBounds.contains(mouseX, mouseY)) {
                    showLevelButtons = !showLevelButtons;
                    controller.onButtonClick();
                } else if (bottomLeftButtonBounds.contains(mouseX, mouseY)) {
                    model.restartLevel();
                    controller.onButtonClick();
                } else if (topRightButtonBounds.contains(mouseX, mouseY)) {
                    controller.onPvP();
                    controller.onButtonClick();
                } else if (bottomRightButtonBounds.contains(mouseX, mouseY)) {
                    controller.onClose();
                    controller.onButtonClick();
                } else if (musicButtonBounds.contains(mouseX, mouseY)) {
                    isMusicOn = !isMusicOn;
                    if (isMusicOn) {
                        controller.onMute();
                    } else {
                        controller.onUnMute();
                    }
                    controller.onButtonClick();
                } else if (showLevelButtons) {
                    // Handle level buttons
                    for (int i = 0; i < 9; i++) {
                        Rectangle levelButtonBounds = new Rectangle(
                                margin,
                                margin + buttonHeight + margin + i * (buttonHeight + margin),
                                buttonWidth,
                                buttonHeight
                        );

                        if (levelButtonBounds.contains(mouseX, mouseY)) {
                            model.chooseLevel(i + 1);
                            System.out.println("Level " + (i + 1) + " Button Pressed");
                            showLevelButtons = false;
                            controller.onButtonClick(); // Add this line to play the sound

                            break; // Exit the loop once a button is clicked
                        }
                    }
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                int mouseX = event.getX();
                int mouseY = event.getY();
                int buttonWidth = 150;
                int buttonHeight = 60;
                int margin = 20;

                Rectangle topLeftButtonBounds = new Rectangle(margin, margin, buttonWidth, buttonHeight);
                Rectangle bottomLeftButtonBounds = new Rectangle(margin, screenHeight - buttonHeight - margin, buttonWidth, buttonHeight);
                Rectangle topRightButtonBounds = new Rectangle(screenWidth - buttonWidth - margin, margin, buttonWidth, buttonHeight);
                Rectangle bottomRightButtonBounds = new Rectangle(screenWidth - buttonWidth - margin, screenHeight - buttonHeight - margin, buttonWidth, buttonHeight);
                Rectangle musicButtonBounds = new Rectangle(screenWidth / 2 - 64 / 2, screenHeight - 64 - margin, 64, 64);

                boolean wasMouseOverButton = isMouseOverButton;

                // Check which area was pressed
                 if (topLeftButtonBounds.contains(mouseX, mouseY)) {
                    isLevelsButtonPressed = true;
                    isMouseOverButton = true;
                } else if (bottomLeftButtonBounds.contains(mouseX, mouseY)) {
                    isRestartButtonPressed = true;
                    isMouseOverButton = true;
                } else if (topRightButtonBounds.contains(mouseX, mouseY)) {
                    isPvpButtonPressed = true;
                    isMouseOverButton = true;
                } else if (bottomRightButtonBounds.contains(mouseX, mouseY)) {
                    isExitButtonPressed = true;
                    isMouseOverButton = true;
                } else if (musicButtonBounds.contains(mouseX, mouseY)) {
                    isMusicButtonPressed  = true;
                    isMouseOverButton = true;
                } else if (showLevelButtons) {
                    // Handle level buttons
                    isMouseOverButton = false;
                    for (int i = 0; i < 9; i++) {
                        isLevelButtonPressed[i] = false;
                    }
                    for (int i = 0; i < 9; i++) {
                        Rectangle levelButtonBounds = new Rectangle(
                            margin,
                            margin + buttonHeight + margin + i * (buttonHeight + margin),
                            buttonWidth,
                            buttonHeight
                        );
                        if (levelButtonBounds.contains(mouseX, mouseY)) {
                            isLevelButtonPressed[i] = true;
                            isMouseOverButton = true;
                        }
                    }
                } else {
                    isMusicButtonPressed = false;
                    isMouseOverButton = false;
                    isLevelsButtonPressed = false;
                    isRestartButtonPressed = false;
                    isPvpButtonPressed = false;
                    isExitButtonPressed = false;
                    for (int i = 0; i < 9; i++) {
                        isLevelButtonPressed[i] = false;
                    }
                }
                // Check if the mouse entered the button area
                if (isMouseOverButton && !wasMouseOverButton) {
                    controller.onButtonHover();
                }

            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });

    }


    /**
     * We explicitly prohibit serialization and deserialization, violating the Liskov principle...
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        throw new NotSerializableException("This class cannot be serialized");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("This class cannot be deserialized");
    }
}
