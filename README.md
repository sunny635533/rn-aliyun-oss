# react-native-oss

for alioss

## Installation

```sh
npm install react-native-oss
```

## Usage

```js
import { NativeModules } from 'react-native';
import { Tools } from '../utils/All';
import md5 from 'js-md5';

// const OssModule = NativeModules.Oss;

import AliyunOss from "rn-aliyun-oss";

const defaultConfig = {
  configuration: {
    maxRetryCount: 1,//请求最大次数
    timeoutIntervalForRequest: 30,//请求超时时间，单位秒
    timeoutIntervalForResource: 24 * 60 * 60,//传输最大时间，单位秒
  },
  accessKey: 'xxxxxxx',
  secretKey: 'xxxxxxxxxxxxxxxxxxxxx',
  bucketName: 'ibooming',
  endPoint: "https://xxxx.aliyuncs.com",
};

/**
 * 阿里云的服务的文件处理
 */
export default class OssBusiness {
  config = defaultConfig;

  constructor(props) {
    this.initWithAppKey();
  }

  initWithAppKey() {
    AliyunOss.initWithAppKey(this.config.accessKey, this.config.secretKey, this.config.endPoint, this.config.configuration);
  }

  getOssFileUrl(path) {
    return "https://oss.xxxx.yun/" + path;
  }

  createOssPath(type = 0, filePath, ossBasePath = "app/") {
    const nowDate = Tools.getCurrentTimeStr("yyyyMMDD");
    let dirPath = ""
    if (type == 0) {
      dirPath = "images/community/";
    } else {
      dirPath = "videos/community/";
    }
    let fileType = "";
    const lastIndex = filePath.lastIndexOf(".");
    if (lastIndex > -1) {
      fileType = filePath.substring(lastIndex);
    }
    //文件名称，用文件完整路径+时间戳
    const dateStr = new Date().getTime();
    const fileName = md5(filePath + dateStr) + fileType;
    const path = ossBasePath + dirPath + nowDate + "/" + fileName
    console.log("==== createOssPath ====filePath: " + filePath + "    path: " + path);
    return path;
  }

  /**
   * Asynchronously uploading
   */
  asyncUpload(input) {
    return AliyunOss.asyncUpload(this.config.bucketName, input.objectKey, input.filepath);
  }

}

```
上传进度：
```
subscription = null;//上传进度的事件

//上传进度
  addProgressListener() {
    if (!this.subscription) {//如果没有监听过，需要注册
      const eventEmitter = new NativeEventEmitter(NativeModules.Oss);
      this.subscription = eventEmitter.addListener(
        'uploadProgress',
        (res) => {//{"currentSize": "153600", "totalSize": "155416","filePath":""}
})
}
 componentWillUnmount() {
    this.subscription && this.subscription.remove();
    this.subscription = null;
  }
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
