package vindicatedrt.com.myapplication.Activity;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import vindicatedrt.com.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(SplashActivity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        setContentView(R.layout.splash_layout);

    }

    public void openCamera(View view) {
        Intent intent = new Intent(SplashActivity.this,CameraActivity.class);
        startActivity(intent);
        finish();
    }
}
