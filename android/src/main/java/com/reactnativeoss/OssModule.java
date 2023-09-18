package com.reactnativeoss;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 参考网址：
 * https://github.com/aliyun/aliyun-oss-react-native/tree/master
 * https://github.com/SpadeGod/react-native-aliyun-oss/tree/master
 */
@ReactModule(name = OssModule.NAME)
public class OssModule extends ReactContextBaseJavaModule {
    public static final String NAME = "Oss";

  private OSS oss;

    public OssModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

  @ReactMethod
  public void initWithSigner(final String signature, final String accessKey, String endPoint,
                             ReadableMap configuration) {

    OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
      @Override
      public String signContent(String content) {
        return "OSS " + accessKey + ":" + signature;
      }
    };

    ClientConfiguration conf = new ClientConfiguration();
    conf.setConnectionTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
    conf.setSocketTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
    conf.setMaxConcurrentRequest(configuration.getInt("maxRetryCount"));
    conf.setMaxErrorRetry(configuration.getInt("maxRetryCount"));

    oss = new OSSClient(getReactApplicationContext().getApplicationContext(), endPoint, credentialProvider, conf);

    Log.d("AliyunOSS", "OSS initWithSigner ok!");
  }

  @ReactMethod
  public void initWithAppKey(String accessKeyId, String accessKeySecret, String endPoint,
                                         ReadableMap configuration) {

    OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
    ClientConfiguration conf = new ClientConfiguration();
    conf.setConnectionTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
    conf.setSocketTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
    conf.setMaxConcurrentRequest(configuration.getInt("maxRetryCount"));
    conf.setMaxErrorRetry(configuration.getInt("maxRetryCount"));

    oss = new OSSClient(getReactApplicationContext().getApplicationContext(), endPoint, credentialProvider, conf);

    Log.d("AliyunOSS", "OSS initWithKey ok!");
  }

  @ReactMethod
  public void initWithSecurityToken(String securityToken, String accessKeyId, String accessKeySecret, String endPoint,
                                    ReadableMap configuration) {
    OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret,
      securityToken);

    ClientConfiguration conf = new ClientConfiguration();
    conf.setConnectionTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
    conf.setSocketTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
    conf.setMaxConcurrentRequest(configuration.getInt("maxRetryCount"));
    conf.setMaxErrorRetry(configuration.getInt("maxRetryCount"));

    oss = new OSSClient(getReactApplicationContext().getApplicationContext(), endPoint, credentialProvider, conf);

    Log.d("AliyunOSS", "OSS initWithKey ok!");
  }

  @ReactMethod
  public void asyncUpload(String bucketName, String ossFile, String sourceFile, final Promise promise) {
    String fileSourcePath = sourceFile;
    if (sourceFile != null) {
      sourceFile = sourceFile.replace("file://", "");
    }
    // init upload request
    PutObjectRequest put = new PutObjectRequest(bucketName, ossFile, sourceFile);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("application/octet-stream");
    put.setMetadata(metadata);
    // set callback
    put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
      @Override
      public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
        Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
        String str_currentSize = Long.toString(currentSize);
        String str_totalSize = Long.toString(totalSize);
        WritableMap onProgressValueData = Arguments.createMap();
        onProgressValueData.putString("currentSize", str_currentSize);
        onProgressValueData.putString("totalSize", str_totalSize);
        onProgressValueData.putString("filePath",fileSourcePath);
        sendEvent(getReactApplicationContext(),"uploadProgress",onProgressValueData);
      }
    });

    OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
      @Override
      public void onSuccess(PutObjectRequest request, PutObjectResult result) {
        Log.d("PutObject", "UploadSuccess");
        Log.d("ETag", result.getETag());
        Log.d("RequestId", result.getRequestId());
        promise.resolve("UploadSuccess");
      }

      @Override
      public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
        promise.reject("UploadFail", serviceException.getLocalizedMessage());
      }
    });
    Log.d("AliyunOSS", "OSS uploadObjectAsync ok!");

  }


  @ReactMethod
  public void addListener(String type) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  public void removeListeners(int type) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  public void asyncDownload(String bucketName, String ossFile, String updateDate, final Promise promise) {
    // 构造下载文件请求
    GetObjectRequest get = new GetObjectRequest(bucketName, ossFile);

    OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
      @Override
      public void onSuccess(GetObjectRequest request, GetObjectResult result) {
        // 请求成功
        Log.d("Content-Length", "" + result.getContentLength());

        InputStream inputStream = result.getObjectContent();
        long resultLength = result.getContentLength();

        byte[] buffer = new byte[2048];
        int len;

        FileOutputStream outputStream = null;
        String localImgURL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImgCache/"
          + System.currentTimeMillis() + ".jpg";
        Log.d("localImgURL", localImgURL);
        File cacheFile = new File(localImgURL);
        if (!cacheFile.exists()) {
          cacheFile.getParentFile().mkdirs();
          try {
            cacheFile.createNewFile();
          } catch (IOException e) {
            e.printStackTrace();
            promise.reject("DownloadFaile", e);
          }
        }
        long readSize = cacheFile.length();
        try {
          outputStream = new FileOutputStream(cacheFile, true);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
          promise.reject("DownloadFaile", e);
        }
        if (resultLength == -1) {
          promise.reject("DownloadFaile", "message:lengtherror");
        }

        try {
          while ((len = inputStream.read(buffer)) != -1) {
            // 处理下载的数据
            try {
              outputStream.write(buffer, 0, len);
              readSize += len;

              String str_currentSize = Long.toString(readSize);
              String str_totalSize = Long.toString(resultLength);
              WritableMap onProgressValueData = Arguments.createMap();
              onProgressValueData.putString("currentSize", str_currentSize);
              onProgressValueData.putString("totalSize", str_totalSize);
              getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("downloadProgress", onProgressValueData);

            } catch (IOException e) {
              e.printStackTrace();
              promise.reject("DownloadFaile", e);
            }
          }
          outputStream.flush();
        } catch (IOException e) {
          e.printStackTrace();
          promise.reject("DownloadFaile", e);
        } finally {
          if (outputStream != null) {
            try {
              outputStream.close();
            } catch (IOException e) {
              promise.reject("DownloadFaile", e);
            }
          }
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              promise.reject("DownloadFaile", e);
            }
          }
          promise.resolve(localImgURL);
        }
      }

      @Override
      public void onFailure(GetObjectRequest request, ClientException clientExcepion,
                            ServiceException serviceException) {
        // 请求异常
        if (clientExcepion != null) {
          // 本地异常如网络异常等
          clientExcepion.printStackTrace();
        }
        if (serviceException != null) {
          // 服务异常
          Log.e("ErrorCode", serviceException.getErrorCode());
          Log.e("RequestId", serviceException.getRequestId());
          Log.e("HostId", serviceException.getHostId());
          Log.e("RawMessage", serviceException.getRawMessage());
        }
        promise.reject("DownloadFaile", "message:networkerror");
      }
    });
  }


  /**
   * 原生发送事件到JS中
   * @param reactContext
   * @param eventName
   * @param params
   */
  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }
}
