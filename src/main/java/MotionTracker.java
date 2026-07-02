import org.bytedeco.opencv.opencv_core.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class MotionTracker {

    private  static  final  int HISTORY_SIZE = 15;
    private  static  final  int MIN_DIRECTION_CHANGES = 4;
    private  static  final  double MIN_MOVEMENT = 15;
    private  static  final  double MIN_AREA = 2000;
    private final List<Integer> yHistory = new ArrayList<>();

    public Rect detectMotion(Mat prevGray, Mat gray) {
        Mat diff = new Mat();
        absdiff(prevGray, gray, diff);
        threshold(diff, diff, 25, 255, THRESH_BINARY);
        dilate(diff, diff, new Mat());

        MatVector contours = new MatVector();
        findContours(diff.clone(), contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        Rect biggest = findBiggestRect(contours);
        if (biggest == null || biggest.area() < MIN_AREA) {
            return null;
        }

        int centerY = biggest.y() + biggest.height() / 2;
        yHistory.add(centerY);
        if (yHistory.size() > HISTORY_SIZE) {
            yHistory.remove(0);
        }

        return biggest;
    }

    public boolean isShaking() {
        if (yHistory.size() < HISTORY_SIZE) return false;

        int directionChanges = 0;
        int prevDirection = 0;

        for (int i = 1; i < yHistory.size(); i++) {
            int delta = yHistory.get(i) - yHistory.get(i - 1);
            if (Math.abs(delta) < MIN_MOVEMENT) continue;

            int direction = delta > 0 ? 1 : -1;
            if (prevDirection != 0 && direction != prevDirection) {
                directionChanges++;
            }
            prevDirection = direction;
        }

        return directionChanges >= MIN_DIRECTION_CHANGES;
    }

    public void reset() {
        yHistory.clear();
    }

    private static Rect findBiggestRect(MatVector contours) {
        double maxArea = 0;
        Rect biggest = null;
        for (long i = 0; i < contours.size(); i++) {
            Rect rect = boundingRect(contours.get(i));
            double area = rect.area();
            if (area > maxArea) {
                maxArea = area;
                biggest = rect;
            }
        }
        return biggest;
    }


}
