package org.fall.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
import org.fall.constant.CrowdConstant;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 工具类
 */
public class CrowdUtils {

    /**
     * @param host    请求地址 支持http 和 https 及 WEBSOCKET
     * @param path    后缀
     * @param appcode 用来吊第三方API的appcode（购买后可以查看）
     * @param phone   短信接收的手机号码
     * @param sign    签名ID
     * @param skin    模板ID
     * @return
     */
    public static ResultEntity<String> sendShortMessage(
            String host,
            String path,
            String appcode,
            String phone,
            String sign,
            String skin) {
        // 生成验证码
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * 10);
            stringBuilder.append(random);
        }
        String param = stringBuilder.toString();
        String urlSend = host + path + "?param=" + param + "&phone=" + phone + "&sign=" + sign + "&skin=" + skin;   // 【5】拼接请求链接
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                System.out.println("正常请求计费(其他均不计费)");
                System.out.println("获取返回的json:");
                System.out.print(json);
                return ResultEntity.successWithData(param);
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    return ResultEntity.failed("AppCode错误 ");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    return ResultEntity.failed("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    return ResultEntity.failed("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    return ResultEntity.failed("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    return ResultEntity.failed("套餐包次数用完 ");
                } else {
                    return ResultEntity.failed("参数名错误 或 其他错误" + error);
                }
            }

        } catch (MalformedURLException e) {
            return ResultEntity.failed("URL格式错误");
        } catch (UnknownHostException e) {
            return ResultEntity.failed("URL地址错误");
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            e.printStackTrace();
            return ResultEntity.failed("套餐包次数用完");
        }
    }

    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


    /**
     * 对明文字符进行MD5加密
     *
     * @param source 传入明文字符
     * @return
     */
    public static String md5(String source) {
        // 1.判断source是否有效
        if (source == null || source.length() == 0) {
            // 2.如果不是有效字符抛出异常
            throw new RuntimeException(CrowdConstant.MESSAGE_STRING_INVALIDATE);
        }
        try {
            // 3.获取MessageDigest对象
            String algorithm = "md5";
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            // 4.获取字符串解析数组
            byte[] input = source.getBytes();
            // 5.执行加密
            byte[] output = messageDigest.digest(input);
            // 6.创建BigInteger对象
            int signum = 1;
            BigInteger bigInteger = new BigInteger(signum, output);
            // 7.按照16进制将值转化为字符串
            int radix = 16;
            String encoded = bigInteger.toString(radix).toUpperCase();
            // 8.返回加密字符串
            return encoded;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用来判断请求类型的工具(true表示json请求，false表示普通请求)
     *
     * @param httpServletRequest
     * @return
     */
    public static boolean judgeRequestType(HttpServletRequest httpServletRequest) {
        // 获取请求的信息头
        String acceptHeader = httpServletRequest.getHeader("Accept");
        String xRequestHeader = httpServletRequest.getHeader("X-Requested-With");
        // 判断是否为json请求
        if ((acceptHeader != null && acceptHeader.contains("application/json")) ||
                (xRequestHeader != null && acceptHeader.equals("XMLHttpRequest")))
            return true;
        return false;
    }

    public static ResultEntity<String> uploadFileToOSS(
            String endPoint,
            String accessKeyId,
            String accessKeySecret,
            InputStream inputStream,
            String bucketName,
            String bucketDomain,
            String originalName ){

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endPoint,accessKeyId,accessKeySecret);

        // 生成上传文件的目录，按照日期来划分目录
        String folderName = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 生成上传文件在OSS服务器上保存的文件名,通过uuid生成随机uuid，将其中的“-”删去（替换成空字符串）
        String fileMainName = UUID.randomUUID().toString().replace("-", "");

        // 从原始文件名中获取文件扩展名
        String extensionName = originalName.substring(originalName.lastIndexOf("."));

        // 使用目录、文件主体名称、文件扩展名拼接得到对象名称
        String objectName = folderName + "/" + fileMainName + extensionName;


        try {
            // 调用OSS客户端对象的方法上传文件并获取响应结果数据
            PutObjectResult putObjectResult = ossClient.putObject(bucketName,objectName,inputStream);

            // 从响应结果中获取具体的响应消息
            ResponseMessage responseMessage = putObjectResult.getResponse();

            // 根据响应状态判断是否成功
            if (responseMessage == null) {
                // 拼接访问刚刚上传的文件的路径
                String ossFileAccessPath = bucketDomain + "/" + objectName;

                // 返回成功，并带上访问路径
                return ResultEntity.successWithData(ossFileAccessPath);
            }else {
                // 获取响应状态码
                int statusCode = responseMessage.getStatusCode();
                // 没有成功 获取错误消息
                String errorMessage = responseMessage.getErrorResponseAsString();

                return ResultEntity.failed("当前响应状态码=" + statusCode + " 错误消息=" + errorMessage);
            }
        } catch (Exception e){
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }

    }

}
