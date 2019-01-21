package vindicatedrt.com.myapplication.Activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import vindicatedrt.com.myapplication.R;
import vindicatedrt.com.myapplication.UI.AutoFitTextureView;
import vindicatedrt.com.myapplication.presenter.CameraPresenterComply;
import vindicatedrt.com.myapplication.view.CameraView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends AppCompatActivity implements CameraView, View.OnClickListener {

    private static final String TAG = "TAG";

    private ImageButton close_btn;
    private ImageButton take_btn;
    private ImageButton save_btn;

    private AutoFitTextureView camera_View;
    private ImageView preview_iv;
    private TextView remind_tv;

    private CameraPresenterComply cameraPresenterComply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.camera_layout);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) {
            Log.e(TAG, "竖屏显示");
            initView();
        } else if (orientation == 2) {
            Log.e(TAG, "横屏显示");
        }
    }

    //初始化视图
    private void initView() {
        close_btn = findViewById(R.id.camera_close_btn);
        take_btn = findViewById(R.id.camera_take_btn);
        save_btn = findViewById(R.id.camera_save_btn);
        camera_View = findViewById(R.id.camera_View);
        preview_iv = findViewById(R.id.camera_preview_iv);
        remind_tv = findViewById(R.id.camera_remind_tv);
        cameraPresenterComply = new CameraPresenterComply(this, camera_View, this, preview_iv);
        camera_View.setSurfaceTextureListener(cameraPresenterComply.getmSurfaceTextureListener());
        close_btn.setOnClickListener(this);
        take_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
    }

    @Override
    public void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setViewINVISIBLE(View... viewINVISIBLE) {
        for (View aViewINVISIBLE : viewINVISIBLE) {
            aViewINVISIBLE.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setViewVISIBLE(View... viewVISIBLE) {
        for (View aViewVISIBLE : viewVISIBLE) {
            aViewVISIBLE.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void saveImg() {
        Bitmap bitmap = preview_iv.getDrawingCache();
        File imgFile = new File(getExternalFilesDir(null), cameraPresenterComply.getTime() + ".jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            showMessage("保存: " + imgFile);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_close_btn:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewINVISIBLE(save_btn, preview_iv, close_btn);
                        setViewVISIBLE(remind_tv, take_btn, camera_View);
                    }
                });
                break;
            case R.id.camera_take_btn:
                cameraPresenterComply.captureStillPicture(getWindowManager());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewINVISIBLE(remind_tv, take_btn, camera_View);
                        setViewVISIBLE(save_btn, preview_iv, close_btn);
                    }
                });
                break;
            case R.id.camera_save_btn:
                saveImg();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewINVISIBLE(save_btn, preview_iv, close_btn);
                        setViewVISIBLE(remind_tv, take_btn, camera_View);
                    }
                });
                break;
            default:
                break;
        }
    }

    // 判断是否支持Camera2方法
    public static boolean hasCamera2(Context mContext) {
        if (mContext == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        try {
            CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            assert manager != null;
            String[] idList = manager.getCameraIdList();
            boolean notFull = true;
            if (idList.length == 0) {
                notFull = false;
            } else {
                for (final String str : idList) {
                    if (str == null || str.trim().isEmpty()) {
                        notFull = false;
                        break;
                    }
                    final CameraCharacteristics characteristics = manager.getCameraCharacteristics(str);

                    final int supportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        notFull = false;
                        break;
                    }
                }
            }
            return notFull;
        } catch (Throwable ignore) {
            return false;
        }
    }
}
