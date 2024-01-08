import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;

public class PlayMusic {
    private final String backgroundPath = "audio/phon.wav";
    private final String goalPath = "audio/goal.wav";
    private final String menu1Path = "audio/menu1.wav";
    private final String menu2Path = "audio/menu2.wav";
    private Clip backgroundClip;
    private Clip goalClip;
    private Clip menu1Clip;
    private Clip menu2Clip;
    private boolean isMuted = false;

    public PlayMusic() {
        backgroundClip = loadMusic(backgroundPath);
        goalClip = loadMusic(goalPath);
        menu1Clip = loadMusic(menu1Path);
        menu2Clip = loadMusic(menu2Path);
    }

    private Clip loadMusic(String path) {
        Clip musicClip = null;
        try {
            File musicFile = new File(path);
            AudioInputStream musicStream = AudioSystem.getAudioInputStream(musicFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(musicStream);
            FloatControl volumeControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-25.0f);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return musicClip;
    }

    public void playBackground() {
        if (backgroundClip == null) {
            backgroundClip = loadMusic(backgroundPath);
        }

        if (backgroundClip != null && !isMuted) {
            if (!backgroundClip.isRunning()) {
                backgroundClip.setFramePosition(0);
                backgroundClip.start();
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void stopBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public void playGoal() {
        if(goalClip == null) {
            goalClip = loadMusic(goalPath);
        }

        if (goalClip != null) {
            goalClip.setFramePosition(0);
            goalClip.start();
        }
    }

    public void stopGoal() {
        if (goalClip != null && goalClip.isRunning()) {
            goalClip.stop();
        }
    }

    public void playButtonSound(){
        if (menu1Clip == null) {
            menu1Clip = loadMusic(menu1Path);
        }

        if (menu1Clip != null) {
            menu1Clip.setFramePosition(0);
            menu1Clip.start();
        }
    }
    
    public void playButtonHoverSound() {
        if (menu2Clip == null) {
            menu2Clip = loadMusic(menu2Path); 
        }

        if (menu2Clip != null) {
            menu2Clip.setFramePosition(0);
            menu2Clip.start();
        }
    }

    public void close() {
        if (backgroundClip != null) {
            backgroundClip.close();
        }
        if (goalClip != null) {
            goalClip.close();
        }
        if (menu1Clip != null) {
            menu1Clip.close();
        }
        if (menu2Clip != null) {
            menu2Clip.close();
        }
    }

    // Set to a very low value to mute
    public void mute() {
        setVolume(-80.0f); 
    }

    // Set back to the original volume
    public void unMute() {
        setVolume(-25.0f); 
    }

    private void setVolume(float volume) {
        // Exclude background music from volume adjustments when muting/unmuting
        if (backgroundClip != null) {
            setClipVolume(backgroundClip, volume);
        }
        if (goalClip != null) {
            setClipVolume(goalClip, volume);
        }
        if (menu1Clip != null) {
            setClipVolume(menu1Clip, volume);
        }
        if (menu2Clip != null) {
            setClipVolume(menu2Clip, volume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(volume);
    }
}
