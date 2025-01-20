package com.cof.managers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {
    private static Clip clip;

    public static void ShotgunSound() {
        try {
            InputStream file = SoundManager.class.getResourceAsStream("/audio/ShotgunSound.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        clip.start();

    }

    public static void FlipCardSound() {
        try {
            InputStream file = SoundManager.class.getResourceAsStream("/audio/FlipCard.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        clip.start();

    }

    public static void revolverSpin() {
        try {
            InputStream file = SoundManager.class.getResourceAsStream("/audio/RevolverSpin.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip.start();

    }

    public static void dices() {
        try {
            InputStream file = SoundManager.class.getResourceAsStream("/audio/dice.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip.start();

    }

    public static void treasure() {
        try {
            InputStream file = SoundManager.class.getResourceAsStream("/audio/treasure.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip.start();

    }
}