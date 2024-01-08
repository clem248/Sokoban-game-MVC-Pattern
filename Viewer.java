import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Viewer {

    private JFrame frame;
    private Canvas canvas;

    public Viewer() {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller(this);
            Model model = controller.getModel();
            controller.onBgMusic();
            canvas = new Canvas(model);
            canvas.addCustomListener(controller);
            frame = createFrame(canvas);
            frame.addKeyListener(controller);
        });
    }

    private JFrame createFrame(Canvas canvas) {
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // frame.setSize(700, 500);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        return frame;
    }

    public void repaint() {
        canvas.repaint();
    }

    public void resize() {
        canvas.resize();
    }

    public void showWinSplash() {
        canvas.showWinSplash();
    }

    /**
    *          Wait Enemy Player
    */
    private JDialog waitEnemyDialog;
    private JDialog losePvPDialog;

    public void showWaitEnemyDialog() {
        if (waitEnemyDialog == null) {
            waitEnemyDialog = createWaitEnemyDialog();
        }
        waitEnemyDialog.setVisible(true);
    }

    public void closeWaitEnemyDialog() {
        while (waitEnemyDialog == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }
        waitEnemyDialog.setVisible(false);
    }

    public JDialog createWaitEnemyDialog() {
        JDialog dialog = new JDialog(frame, "Wait", true);
        JLabel label = new JLabel("Wait enemy Player", SwingConstants.CENTER);
        dialog.add(label);
        dialog.setSize(200, 100);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }

    public void showLosePvPDialog() {
        if (losePvPDialog == null) {
            losePvPDialog = createLosePvPDialog();
        }
            losePvPDialog.setVisible(true);
    }

    public JDialog createLosePvPDialog() {
            JDialog dialog = new JDialog(frame, "Lose", true);
            JLabel label = new JLabel("You LOSE!", SwingConstants.CENTER);
            dialog.add(label);
            dialog.setSize(200, 100);
            dialog.setLocationRelativeTo(frame);
            dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            return dialog;
    }
}
