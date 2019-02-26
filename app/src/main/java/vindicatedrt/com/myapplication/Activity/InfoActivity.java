package vindicatedrt.com.myapplication.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
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
import java.util.List;

import vindicatedrt.com.myapplication.R;
import vindicatedrt.com.myapplication.bean.BodyAnalysisBean;
import vindicatedrt.com.myapplication.bean.FaceV3DetectBean;
import vindicatedrt.com.myapplication.util.AuthService;
import vindicatedrt.com.myapplication.util.FileUtil;
import vindicatedrt.com.myapplication.util.HttpUtil;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String taoBaoPackage = "com.taobao.taobao";
    public static final String BODY_ANALYSIS_API = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_analysis";
    public static String FACE_DETECT_URL = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
    private static final String TAG = "TAG";

    private EditText gender_et;
    private EditText age_et;
    private EditText faceShape_et;
    private EditText height_et;
    private EditText width_et;
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
                Bitmap bitmap = FileUtil.getBitmapByFileDescriptor(path, 1024, 1024);
                info_iv.setImageBitmap(bitmap);
            }
        });
    }

    private void initView() {
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
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            final String path = getIntent().getStringExtra("imgPath");
                            final String AccessToken = AuthService.getAuth();
                            String result = faceDetect(path, "1", AccessToken);
                            JSON json = JSON.parseObject(result);
                            FaceV3DetectBean faceV3Bean = JSONObject.toJavaObject(json, FaceV3DetectBean.class);
                            BodyAnalysisBean bodyAnalysisBean = getBodyAnalysisBean(path, AccessToken);
                            String error = faceV3Bean.getError_msg();
                            final String ageStr = faceV3Bean.getResult().getFace_list().get(0).getAgeStr();
                            final String type = faceV3Bean.getResult().getFace_list().get(0).getFace_shape().getType();
                            final String gender = faceV3Bean.getResult().getFace_list().get(0).getGender().getType();
                            final String height = bodyAnalysisBean.getPerson_info().get(0).getLocation().getProbablyHeight();
                            final String width = bodyAnalysisBean.getPerson_info().get(0).getLocation().getProbablyWidth();
                            if (error.equals("pic not has face")) {
                                Log.i(TAG, "未识别人脸");
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        gender_et.setText(gender);
                                        age_et.setText(ageStr);
                                        faceShape_et.setText(type);
                                        height_et.setText(height);
                                        width_et.setText(width);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.info_searchOnTaoBao_ib:
                Bitmap clothBitmap = viewToBitmap(info_iv,1024,1024);
                MediaStore.Images.Media.insertImage(getContentResolver(), clothBitmap, "title", "description");
                launchApp(taoBaoPackage);
                finish();
                break;
            default:
                break;
        }

    }

    /**
     * @param filePath     图片地址
     * @param max_face_num 最多人脸数
     * @param accessToken  AccessToken
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


    /**
     * @param imagePath   图片地址
     * @param accessToken AccessToken
     * @return 返回请求结果对象
     * @throws Exception 抛出未知错误
     */
    public static BodyAnalysisBean getBodyAnalysisBean(String imagePath, String accessToken) throws Exception {
        byte[] imgData = FileUtil.readFileByBytes(imagePath);
        String imgStr = Base64Util.encode(imgData);
        String param = "image=" + URLEncoder.encode(imgStr, "UTF-8");
        String result = HttpUtil.post(BODY_ANALYSIS_API, accessToken, param);
        BodyAnalysisBean bodyAnalysisBean = JSON.parseObject(result, BodyAnalysisBean.class);
        Log.i(TAG, result);
        return bodyAnalysisBean;
    }

    /**
     * 启动外部app 方法
     * @param appPackage 外部app包名
     */
    public void launchApp(String appPackage) {
        PackageManager packageManager = this.getApplicationContext().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String pn = packageInfos.get(i).packageName;
                if (pn.equals(appPackage)) {
                    Intent openTaoBao = packageManager.getLaunchIntentForPackage(appPackage);
                    startActivity(openTaoBao);
                    return;
                }
            }
            Uri uri = Uri.parse("market://details?id=" + appPackage);
            Intent openMarket = new Intent(Intent.ACTION_VIEW, uri);
            openMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openMarket);
        }
    }

    /**
     * view 转 bitmap 方法
     * @param v 视图对象
     * @param width 转换后的宽
     * @param height 转换后的高
     * @return 返回转换好的bitmap
     */
    public Bitmap viewToBitmap(View v, int width, int height) {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bitmap;
    }
}
