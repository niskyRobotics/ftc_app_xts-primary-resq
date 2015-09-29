package ftc.team6460.javadeck.ftc.vision;

import android.graphics.Canvas;
import org.bytedeco.javacpp.opencv_core;

/**
 * Created by hexafraction on 9/29/15.
 */
public interface MatCallback {

    public void handleMat(opencv_core.Mat mat);

    public void draw(Canvas canvas);
}
