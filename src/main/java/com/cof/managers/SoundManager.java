package com.cof.managers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * La classe per gestire i suoni nel gioco
 */
public class SoundManager {
    private static Clip clip;

    /**
     * Il suono della revolver quando spara
     */
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

    /**
     * Il suono della carta quando si gira
     */
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

    /**
     * Il suono del caricamento della revolver
     */
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

    /**
     * Il suono del caricamento della revolver
     */
    public static void revolverMisfire() {
        try {
            InputStream file = SoundManager.class.getResourceAsStream("/audio/Misfire.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip.start();

    }
}