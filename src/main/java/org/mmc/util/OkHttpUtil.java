package org.mmc.util;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 网络请求，依赖 okhttp 库。
 * 依赖信息：
 * 组ID: com.squareup.okhttp3
 * 工件ID: okhttp
 * 版本: 3.14.9
 * 参考链接：https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
 */
public class OkHttpUtil {

    // 创建OkHttpClient对象
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
            .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间
            .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间
            .build();


    /**
     * GET请求
     *
     * @param url       请求URL
     * @param iCallBack 回调接口
     */
    public static void get(String url, ICallBack iCallBack) {
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();

            if (iCallBack == null) {
                return;
            }
            if (response.isSuccessful()) {
                iCallBack.success(response.body().string());
            } else {
                iCallBack.failed("请求失败,检查网络");
            }
        } catch (IOException e) {
            iCallBack.failed(e.getMessage());
        }
    }

    /**
     * GET请求
     *
     * @param url 请求URL
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String get(String url) throws IOException {
        return get(url, new HashMap<>());
    }

    /**
     * GET请求
     *
     * @param url     请求URL
     * @param headers headers
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String get(String url, HashMap<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }
        return client.newCall(builder.build()).execute().body().string();

    }

    /**
     * GET请求
     *
     * @param url       请求URL
     * @param headers   headers
     * @param iCallBack iCallBack
     */
    public static void get(String url, HashMap<String, String> headers, ICallBack iCallBack) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        headers.forEach(builder::addHeader);
        try {
            Response response = client.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                iCallBack.success(response.body().string());
            } else {
                iCallBack.failed("请求失败,检查网络");
            }
        } catch (IOException e) {
            iCallBack.failed(e.getMessage());
        }
    }

    /**
     * POST请求
     *
     * @param url         请求URL
     * @param requestBody 请求体
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String post(String url, RequestBody requestBody) throws IOException {
        return post(url, requestBody, new HashMap<>());
    }

    /**
     * POST请求
     *
     * @param url         请求URL
     * @param requestBody 请求体
     * @param headers     请求头
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String post(String url, RequestBody requestBody, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url).post(requestBody);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        return client.newCall(request).execute().body().string();
    }

    /**
     *
     * POST请求
     *
     * @param url         请求URL
     * @param requestBody 请求体
     * @param headers     请求头
     * @param iCallBack iCallBack
     */
    public static void post(String url, RequestBody requestBody, Map<String, String> headers, ICallBack iCallBack) {
        Request.Builder builder = new Request.Builder();
        builder.url(url).post(requestBody);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            if (iCallBack == null) {
                return;
            }
            if (response.isSuccessful()) {
                iCallBack.success(response.body().string());
            } else {
                iCallBack.failed("请求失败,检查网络");
            }
        } catch (IOException e) {
            iCallBack.failed(e.getMessage());
        }
    }

    /**
     * 构造JSON请求体
     *
     * @param jsonStr JSON字符串
     * @return JSON请求体
     */
    public static RequestBody buildJsonRequestBody(String jsonStr) {
        return RequestBody.create(MediaType.parse("application/json"), jsonStr);
    }

    /**
     * 构造JSON请求体
     * @param jsonStr jsonStr
     * @return 请求体
     */
    public static RequestBody buildJsonRequestBody(Object jsonStr) {
        return RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(jsonStr));
    }

    /**
     * 构造表单请求体
     *
     * @param formParams 表单参数
     * @return 表单请求体
     */
    public static RequestBody buildFormRequestBody(Map<String, String> formParams) {
        FormBody.Builder builder = new FormBody.Builder();
        if (formParams != null && formParams.size() > 0) {
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 构造Multipart请求体
     *
     * @param multipartParams Multipart参数
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBody(Map<String, Object> multipartParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (multipartParams != null && multipartParams.size() > 0) {
            for (Map.Entry<String, Object> entry : multipartParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof byte[]) {
                    builder.addFormDataPart(key, null,
                            RequestBody.create(MediaType.parse("application/octet-stream"), (byte[]) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(key, null, (RequestBody) value);
                }
            }
        }
        return builder.build();
    }

    /**
     * 构造Multipart请求体，上传文件
     *
     * @param file file
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBodyWithFile(File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
    }

    /**
     * 构造Multipart请求体，支持上传文件
     *
     * @param multipartParams Multipart参数
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBodyWithFiles(Map<String, Object> multipartParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (multipartParams != null && !multipartParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : multipartParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof byte[]) {
                    builder.addFormDataPart(key, null,
                            RequestBody.create(MediaType.parse("application/octet-stream"), (byte[]) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(key, null, (RequestBody) value);
                } else if (value instanceof UploadFile) { // 支持上传文件
                    UploadFile file = (UploadFile) value;
                    builder.addFormDataPart(key, file.getName(),
                            RequestBody.create(MediaType.parse(file.getMimeType()), file.getFile()));
                }
            }
        }
        return builder.build();
    }

    /**
     * 上传文件
     *
     * @param url     请求URL
     * @param file    上传的文件
     * @param headers 请求头
     * @param iCallBack iCallBack
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static void uploadFile(String url, UploadFile file, Map<String, String> headers, ICallBack iCallBack) throws IOException {
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(file.getMimeType()), file.getFile()))
                .build();
        builder.url(url).post(requestBody);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (iCallBack == null) {
            return;
        }
        try {
            if (response.isSuccessful()) {
                iCallBack.success(response.body().string());
            } else {
                iCallBack.failed("请求失败,检查网络");
            }
        } catch (IOException e) {
            iCallBack.failed(e.getMessage());
        }
    }

    public static OkHttpClient getClient() {
        return client;
    }

    public static Request.Builder getBuilder() {
        Request.Builder builder = new Request.Builder();
        return builder;
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         *
         * @param file
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         *
         * @param e
         */
        void onDownloadFailed(Exception e);
    }

    /**
     * https://www.jianshu.com/p/3b269082cbbb
     *
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public static void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                if (listener != null) listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = mkdirs(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, url.substring(url.lastIndexOf("/") + 1));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        if (listener != null) listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    if (listener != null) listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    if (listener != null) listener.onDownloadFailed(e);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir saveDir
     * @return 创建文件夹路径
     * @throws IOException 判断下载目录是否存在
     */
    private static String mkdirs(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 封装文件上传参数
     */
    public static class UploadFile {
        private String name;
        private String mimeType;
        private byte[] file;

        public UploadFile(String filePath) {
            File file = new File(filePath);
            // 读取文件内容
            byte[] fileBytes = new byte[0];
            try {
                fileBytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.name = file.getName();
            this.mimeType = "text/plain";
            this.file = fileBytes;
        }

        public UploadFile(String name, String mimeType, byte[] file) {
            this.name = name;
            this.mimeType = mimeType;
            this.file = file;
        }

        public String getName() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }

        public byte[] getFile() {
            return file;
        }
    }

    /**
     * 多文件上传
     *
     * @param url
     * @param files 多文件
     */
    public static void uploadMultipleFiles(String url, File[] files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (File file : files) {
            builder.addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("Files uploaded successfully");
            } else {
                System.out.println("Upload failed: " + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ICallBack {
        void success(String body);

        void failed(String message);
    }

}