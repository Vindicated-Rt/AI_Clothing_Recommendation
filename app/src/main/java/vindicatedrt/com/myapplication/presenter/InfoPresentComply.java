package vindicatedrt.com.myapplication.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.util.Base64Util;

import java.net.URLEncoder;
import java.util.List;

import vindicatedrt.com.myapplication.bean.BodyAnalysisBean;
import vindicatedrt.com.myapplication.bean.FaceV3DetectBean;
import vindicatedrt.com.myapplication.util.FileUtil;
import vindicatedrt.com.myapplication.util.HttpUtil;
import vindicatedrt.com.myapplication.view.InfoVIew;

public class InfoPresentComply implements InfoPresent, InfoVIew {

    private static final String BODY_ANALYSIS_API = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_analysis";
    private static final String FACE_DETECT_URL = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
    private static final String TAG = "TAG";

    private FaceV3DetectBean mFaceV3Bean;
    private BodyAnalysisBean mBodyAnalysisBean;

    private Context mContext;

    public InfoPresentComply(Context context){
        this.mContext = context;
    }

    public void setFaceV3Bean(FaceV3DetectBean mFaceV3Bean) {
        this.mFaceV3Bean = mFaceV3Bean;
    }

    public void setBodyAnalysisBean(BodyAnalysisBean mBodyAnalysisBean) {
        this.mBodyAnalysisBean = mBodyAnalysisBean;
    }

    public String getAgeStr() {
        return mFaceV3Bean.getResult().getFace_list().get(0).getAgeStr();
    }

    public String getType() {
        return mFaceV3Bean.getResult().getFace_list().get(0).getFace_shape().getType();
    }

    public String getGender() {
        return mFaceV3Bean.getResult().getFace_list().get(0).getGender().getType();
    }

    public String getmHeight() {
        return mBodyAnalysisBean.getPerson_info().get(0).getLocation().getProbablyHeight();
    }

    public String getmWidth() {
        return mBodyAnalysisBean.getPerson_info().get(0).getLocation().getProbablyWidth();
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
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        if (packageInfo != null) {
            for (int i = 0; i < packageInfo.size(); i++) {
                String pn = packageInfo.get(i).packageName;
                if (pn.equals(appPackage)) {
                    Intent openTaoBao = packageManager.getLaunchIntentForPackage(appPackage);
                    mContext.startActivity(openTaoBao);
                    return;
                }
            }
            Uri uri = Uri.parse("market://details?id=" + appPackage);
            Intent openMarket = new Intent(Intent.ACTION_VIEW, uri);
            openMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(openMarket);
        }
    }

    /**
     * view 转 bitmap 方法
     * @param v 视图对象
     * @param width 转换后的宽
     * @param height 转换后的高
     * @return 返回转换好的bitmap
     */
    @Override
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

    @Override
    public void setTextStr(EditText editText, String text) {
        editText.setText(text);
    }
}
