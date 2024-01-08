import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Controller implements KeyListener {
    private Model model;

    public Controller(Viewer viewer) {
        model = new Model(viewer);
    }

    @Override
    public void keyPressed(KeyEvent event){
        int keyCode = event.getKeyCode();
        switch(keyCode){
            case 37:
                model.moveLeft();
                break;
            case 38:
                model.moveUp();
                break;
            case 39:
                model.moveRight();
                break;
            case 40:
                model.moveDown();
                break;
            default:
                return;
        }
    }

    public void keyReleased(KeyEvent event) {
    }

    public void keyTyped(KeyEvent event) {
    }

    public void pickLevel(int level) {
        if (level >= 1 && level <= 9) {
            model.loadLevel(level);
        } else {
            System.out.println("Error, wrong level");
        }
    }  

    public Model getModel() {
        return model;
    }

    public void onPvP() {
        model.waitPvP();
    }

    public void onClose() {
        System.exit(0);
    }

    public void onButtonClick(){
        model.loadButtonSound();
    }

    public void onButtonHover(){
        model.loadButtonHoverSound();
    }
    
    public void onBgMusic(){
        model.playBGMusic();
    }

    public void onMute(){
        model.mute();
    }

    public void onUnMute() {
        model.unMute();
    }
}
