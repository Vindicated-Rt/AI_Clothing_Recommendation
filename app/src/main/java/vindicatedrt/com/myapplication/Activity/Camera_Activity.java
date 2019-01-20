package vindicatedrt.com.myapplication.Activity;

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

import vindicatedrt.com.myapplication.R;
import vindicatedrt.com.myapplication.UI.AutoFitTextureView;
import vindicatedrt.com.myapplication.view.CameraView;

public class Camera_Activity extends AppCompatActivity implements CameraView, View.OnClickListener {

    private static final String TAG = "TAG";

    private String filePath;

    private ImageButton close_btn;
    private ImageButton take_btn;
    private ImageButton save_btn;

    private AutoFitTextureView camera_View;
    private ImageView preview_iv;
    private TextView remind_tv;


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

    private void initView() {
        close_btn = (ImageButton) findViewById(R.id.camera_close_btn);
        take_btn = (ImageButton) findViewById(R.id.camera_take_btn);
        save_btn = (ImageButton) findViewById(R.id.camera_save_btn);
        camera_View = (AutoFitTextureView) findViewById(R.id.camera_View);
        preview_iv = (ImageView) findViewById(R.id.camera_preview_iv);
        remind_tv = (TextView) findViewById(R.id.camera_remind_tv);

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewINVISIBLE(remind_tv, take_btn, camera_View);
                        setViewVISIBLE(save_btn, preview_iv, close_btn);
                    }
                });
                break;
            case R.id.camera_save_btn:
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
}
