package vindicatedrt.com.myapplication.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 * 获取AccessToken
 */
public class AuthService {
    private static final String TAG = "TAG";

    public static String getAuth() {
        // API Key
        String clientId = "kzofpBU87Gr9xGta4Q3XACqS";
        // Secret Key
        String clientSecret = "nR8HYfp6C5w3GI1BjAtsmk1eL5AdjFCo";
        return getAuth(clientId, clientSecret);
    }

    private static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        final String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. API Key
                + "&client_id=" + ak
                // 3. Secret Key
                + "&client_secret=" + sk;
        return getAccessToken(getAccessTokenUrl);
    }

    private static String getAccessToken(String getAccessTokenUrl) {
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // POST请求
            connection.setRequestMethod("POST");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                Log.e(TAG, key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            Log.e(TAG, "result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.getString("access_token");
        } catch (Exception e) {
            Log.e(TAG, "获取token失败!");
            e.printStackTrace(System.err);
        }
        return null;
    }
}
