"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _reactNative = require("react-native");

var _lodash = _interopRequireDefault(require("lodash"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

const Oss = _reactNative.NativeModules.Oss;
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
    this.config = _lodash.default.merge(this.config, config);
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

var _default = AliyunOss;
exports.default = _default;
//# sourceMappingURL=AliyunOss.js.map