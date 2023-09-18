// export * from './types';

// export { default as AliyunOss } from './AliyunOss'

import {
  NativeModules,
} from 'react-native';

const Oss = NativeModules.Oss;

export default {
  initWithAppKey(accessKey, secretKey, endPoint, configuration) {
    Oss.initWithAppKey(accessKey, secretKey, endPoint, configuration);
  },

  asyncUpload(bucketName, objectKey, filepath) {
    return Oss.asyncUpload(bucketName, objectKey, filepath);
  },

  asyncDownload(bucketName, objectKey, filepath) {
    return Oss.asyncDownload(bucketName, objectKey, filepath);
  }
};