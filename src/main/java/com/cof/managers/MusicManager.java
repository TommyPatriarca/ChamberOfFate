
package com.cof.managers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Gestisce l'uso dell'audio all'interno del gioco.
 */
public class MusicManager {
    private InputStream is = getClass().getResourceAsStream("/audio/ScreenBackground.wav");
    private static Clip clip;
    private static FloatControl volumeControl;
    private static boolean muted;

    public MusicManager() {
        this.muted = false;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (!muted) {
            clip.setFramePosition(0);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void mute() {
        clip.stop();
        muted = true;
    }

    public static void unmute() {
        muted = false;
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void setVolume(double value) {
        if (volumeControl != null) {
            float dB = (float) (Math.log10(value) * 20);
            volumeControl.setValue(dB);
        }
    }

    public static double getVolume() {
        if (volumeControl != null) {
            float dB = volumeControl.getValue();
            return Math.pow(10, dB / 20.0);
        }
        return 1.0;
    }
}
