import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class Main {

    public static void main(String[] args) throws Exception {
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame("hand-shake-67");
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        MotionTracker tracker = new MotionTracker();
        Mat prevGray = null;

        while (canvas.isVisible()) {
            Frame frame = grabber.grab();
            if (frame == null) continue;

            Mat mat = converter.convert(frame);

            Mat gray = new Mat();
            cvtColor(mat, gray, COLOR_BGR2GRAY);
            GaussianBlur(gray, gray, new Size(21, 21), 0);

            if (prevGray == null) {
                prevGray = gray;
                continue;
            }

            Rect motionRect = tracker.detectMotion(prevGray, gray);
            if (motionRect != null) {
                rectangle(mat, motionRect, new Scalar(0, 255, 0, 0));

                if (tracker.isShaking()) {
                    tracker.reset();
                    new Thread(Main::triggerMeme67).start();
                }
            }

            prevGray = gray;
            canvas.showImage(converter.convert(mat));
        }

        grabber.stop();
        canvas.dispose();
    }

    private static void triggerMeme67() {
        playSound();
        showMemeGif();
    }

    private static void playSound() {
        try (InputStream audioSrc = Main.class.getResourceAsStream("/meme67.wav")) {
            if (audioSrc == null) {
                System.err.println("meme67.wav не найден в resources — проверь путь и пересинкай Gradle");
                return;
            }

            byte[] audioBytes = audioSrc.readAllBytes();
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showMemeGif() {
        URL gifUrl = Main.class.getResource("/meme67.gif");
        if (gifUrl == null) {
            System.err.println("meme67.gif не найден в resources — проверь путь и пересинкай Gradle");
            return;
        }

        ImageIcon gifIcon = new ImageIcon(gifUrl);

        JFrame memeFrame = new JFrame("67!");
        memeFrame.getContentPane().add(new JLabel(gifIcon));
        memeFrame.pack();
        memeFrame.setLocationRelativeTo(null);
        memeFrame.setVisible(true);

        new Timer(3000, e -> memeFrame.dispose()).start();
    }
}
