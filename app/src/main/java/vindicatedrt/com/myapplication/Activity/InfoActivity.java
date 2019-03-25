package vindicatedrt.com.myapplication.Activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import vindicatedrt.com.myapplication.R;
import vindicatedrt.com.myapplication.bean.BodyAnalysisBean;
import vindicatedrt.com.myapplication.bean.FaceV3DetectBean;
import vindicatedrt.com.myapplication.presenter.InfoPresentComply;
import vindicatedrt.com.myapplication.util.AuthService;
import vindicatedrt.com.myapplication.util.FileUtil;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String taoBaoPackage = "com.taobao.taobao";
    public static final String errorInfo = "pic not has face";
    private static final String TAG = "TAG";

    private EditText gender_et;
    private EditText age_et;
    private EditText faceShape_et;
    private EditText height_et;
    private EditText width_et;
    private ImageView info_iv;

    private InfoPresentComply mInfoPresentComply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(InfoActivity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        setContentView(R.layout.info_layout);
        initView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String path = getIntent().getStringExtra("imgPath");
                Bitmap bitmap = FileUtil.getBitmapByFileDescriptor(path, 1024, 1024);
                info_iv.setImageBitmap(bitmap);
            }
        });
    }

    private void initView() {
        mInfoPresentComply = new InfoPresentComply(this);
        gender_et = findViewById(R.id.info_gender_et);
        age_et = findViewById(R.id.info_age_et);
        faceShape_et = findViewById(R.id.info_faceShape_et);
        height_et = findViewById(R.id.info_height_et);
        width_et = findViewById(R.id.info_width_et);
        ImageButton post_ib = findViewById(R.id.info_post_ib);
        ImageButton searchOnTaoBao_ib = findViewById(R.id.info_searchOnTaoBao_ib);
        info_iv = findViewById(R.id.info_iv);
        searchOnTaoBao_ib.setOnClickListener(this);
        post_ib.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_post_ib:
                postThread.start();
                break;
            case R.id.info_searchOnTaoBao_ib:
                Bitmap clothBitmap = mInfoPresentComply.viewToBitmap(info_iv,1024,1024);
                MediaStore.Images.Media.insertImage(getContentResolver(), clothBitmap, "title", "description");
                mInfoPresentComply.launchApp(taoBaoPackage);
                finish();
                break;
            default:
                break;
        }

    }

    private Thread postThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                final String path = getIntent().getStringExtra("imgPath");
                final String AccessToken = AuthService.getAuth();
                String result = InfoPresentComply.faceDetect(path, "1", AccessToken);
                JSON json = JSON.parseObject(result);
                FaceV3DetectBean faceV3Bean = JSONObject.toJavaObject(json, FaceV3DetectBean.class);
                BodyAnalysisBean bodyAnalysisBean = InfoPresentComply.getBodyAnalysisBean(path, AccessToken);
                mInfoPresentComply.setFaceV3Bean(faceV3Bean);
                mInfoPresentComply.setBodyAnalysisBean(bodyAnalysisBean);
                String error = faceV3Bean.getError_msg();
                if (error.equals(errorInfo)) {
                    Log.i(TAG, "未识别人脸");
                    Toast.makeText(InfoActivity.this,"未识别人脸",Toast.LENGTH_LONG).show();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mInfoPresentComply.setTextStr(gender_et,mInfoPresentComply.getGender());
                            mInfoPresentComply.setTextStr(age_et,mInfoPresentComply.getAgeStr());
                            mInfoPresentComply.setTextStr(faceShape_et,mInfoPresentComply.getType());
                            mInfoPresentComply.setTextStr(height_et,mInfoPresentComply.getmHeight());
                            mInfoPresentComply.setTextStr(width_et,mInfoPresentComply.getmWidth());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
}
