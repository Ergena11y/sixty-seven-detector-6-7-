import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;import org.bytedeco.opencv.opencv_core.*;

import javax.swing.*;import java.awt.*;import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Main {

  public static void main(String[] args) throws Exception{

   FrameGrabber grabber  = FrameGrabber.createDefault(0);
   grabber.start();

   OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
   CanvasFrame canvas = new CanvasFrame("hand-shake-67");

   canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

       if ( motionRect != null ) {
           rectangle(mat, motionRect, new Scalar(0, 255, 0, 0));

           if (tracker.isShaking()) {
               triggerMeme67();
               tracker.reset();
           }
       }

       prevGray = gray;
       canvas.showImage(converter.convert(mat));
   }

   grabber.stop();
   canvas.dispose();
  }

  private static void triggerMeme67() {
      System.out.println("67 ПОЙМАЛИ ДВИЖ!");


  }
}
