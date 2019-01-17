package vindicatedrt.com.myapplication.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 * http发送post请求
 */
public class HttpUtil {

    private static final String TAG = "TAG";

    public static String post(String requestUrl, String accessToken, String params) throws Exception {
        Log.e(TAG,params);
        String generalUrl = "";
        generalUrl = requestUrl + "?access_token=" + accessToken;
        Log.e(TAG,"发送的连接为:" + generalUrl);
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Log.e(TAG,"打开链接，开始发送请求" + new Date().getTime() / 1000);
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            Log.e(TAG,key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder result = new StringBuilder();
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result.append(getLine);
        }
        in.close();
        Log.e(TAG,"请求结束" + new Date().getTime() / 1000);
        Log.e(TAG,"result:" + result);
        return result.toString();
    }
}