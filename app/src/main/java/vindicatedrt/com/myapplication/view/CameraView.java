package vindicatedrt.com.myapplication.view;

import android.graphics.Bitmap;
import android.view.View;

public interface CameraView {
    void setFullScreen();

    void showMessage(String message);

    void setViewINVISIBLE(View... viewINVISIBLE);

    void setViewVISIBLE(View... viewVISIBLE);

    String saveImg();
}
