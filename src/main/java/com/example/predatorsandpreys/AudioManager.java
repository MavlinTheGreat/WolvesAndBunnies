package com.example.predatorsandpreys;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioManager extends Thread {

    private final BlockingQueue<String> soundQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public AudioManager() {
        setDaemon(true);
        start();
    }

    public void addSound(String resourcePath) {
        URL resourceUrl = getClass().getResource(resourcePath);
        if (resourceUrl == null) {
            System.err.println("Ошибка: файл " + resourcePath + " не найден!");
            return;
        }
        soundQueue.add(resourceUrl.toExternalForm());
    }

    public void stopAudioManager() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                String soundFilePath = soundQueue.take(); // Ждём появление нового звука
                playSound(soundFilePath);
            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }
    }

    private synchronized void playSound(String soundFilePath) {
        try {
            Media sound = new Media(soundFilePath);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);

            // Ждём, пока медиа будет готова, затем воспроизводим
            mediaPlayer.setOnReady(mediaPlayer::play);

            // Ждём окончания воспроизведения
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.dispose();
                synchronized (this) {
                    this.notify(); // Разблокируем поток после завершения звука
                }
            });

            synchronized (this) {
                this.wait(); // Блокируем, пока звук не завершится
            }
        } catch (Exception e) {
            System.err.println("Ошибка воспроизведения: " + e.getMessage());
        }
    }
}
