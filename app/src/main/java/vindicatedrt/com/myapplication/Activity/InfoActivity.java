package vindicatedrt.com.myapplication.Activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;

import java.net.URLEncoder;

import vindicatedrt.com.myapplication.R;
import vindicatedrt.com.myapplication.bean.FaceV3DetectBean;
import vindicatedrt.com.myapplication.util.AuthService;
import vindicatedrt.com.myapplication.util.FileUtil;
import vindicatedrt.com.myapplication.util.HttpUtil;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{

    public static String FACE_DETECT_URL = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
    private static final String TAG = "TAG";

    private EditText gender_et;
    private EditText age_et;
    private EditText faceShape_et;
    private ImageView info_iv;

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
                Bitmap bitmap = FileUtil.getBitmapByFileDescriptor(path,1024,1024);
                info_iv.setImageBitmap(bitmap);
            }
        });
    }
    private void initView(){
        gender_et = findViewById(R.id.info_gender_et);
        age_et = findViewById(R.id.info_age_et);
        faceShape_et = findViewById(R.id.info_faceShape_et);
        ImageButton post_ib = findViewById(R.id.info_post_ib);
        info_iv = findViewById(R.id.info_iv);
        post_ib.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    final String path = getIntent().getStringExtra("imgPath");
                    final String AccessToken = AuthService.getAuth();
                    String result = faceDetect(path, "1", AccessToken);
                    JSON json = JSON.parseObject(result);
                    FaceV3DetectBean faceV3Bean = JSONObject.toJavaObject(json, FaceV3DetectBean.class);
                    String error = faceV3Bean.getError_msg();
                    final int age = faceV3Bean.getResult().getFace_list().get(0).getAge();
                    final String type = faceV3Bean.getResult().getFace_list().get(0).getFace_shape().getType();
                    final String gender = faceV3Bean.getResult().getFace_list().get(0).getGender().getType();
                    if(error.equals("pic not has face")){
                        Log.i(TAG, "未识别人脸");
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gender_et.setText(gender);
                                age_et.setText(String.valueOf(age));
                                faceShape_et.setText(type);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     *
     * @param filePath 图片地址
     * @param max_face_num  最多人脸数
     * @param accessToken   AccessToken
     * @return 返回请求结果
     * @throws Exception 抛出未知错误
     */
    public static String faceDetect(String filePath, String max_face_num, String accessToken) throws Exception {
        byte[] imgData = FileUtil.readFileByBytes(filePath);
        String imgStr = Base64Util.encode(imgData);
        String params = "image=" + URLEncoder.encode(imgStr, "UTF-8")
                + "&image_type=BASE64&max_face_num=" + max_face_num
                + "&face_field=gender,age,face_shape";
        return HttpUtil.post(FACE_DETECT_URL, accessToken, params);
    }
}
