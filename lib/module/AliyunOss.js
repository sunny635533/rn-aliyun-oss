function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { NativeModules } from 'react-native';
import _ from 'lodash';
const Oss = NativeModules.Oss;
const defaultConfig = {
  configuration: {
    maxRetryCount: 3,
    timeoutIntervalForRequest: 30,
    timeoutIntervalForResource: 24 * 60 * 60
  },
  accessKey: '',
  secretKey: '',
  endPoint: '',
  bucketName: ''
};

class AliyunOss {
  constructor(config) {
    _defineProperty(this, "config", defaultConfig);

    this.setConfig(config);
    this.initWithPlainTextAccessKey();
  }

  setConfig(config) {
    this.config = _.merge(this.config, config);
  }

  initWithPlainTextAccessKey() {
    Oss.initWithPlainTextAccessKey(this.config.accessKey, this.config.secretKey, this.config.endPoint, this.config.configuration);
  }
  /**
   * Asynchronously uploading
   */


  asyncUpload(input) {
    return Oss.asyncUpload(this.config.bucketName, input.objectKey, input.filepath);
  }
  /**
  * Asynchronously downloading
  */


  asyncDownload(input) {
    return Oss.asyncDownload(this.config.bucketName, input.objectKey, input.filepath);
  }

}

export default AliyunOss;
//# sourceMappingURL=AliyunOss.js.map