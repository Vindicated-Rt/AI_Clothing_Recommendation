package vindicatedrt.com.myapplication.util;

import java.io.*;

/**
 * 工具类
 * 读取文件地址转换为流
 */
public class FileUtil {
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            BufferedInputStream in;

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length())) {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                return bos.toByteArray();
            }
        }
    }
}
