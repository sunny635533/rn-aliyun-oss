import {
    NativeModules,
} from 'react-native';
import _ from 'lodash'
import type { DefaultConfigType, OssType, AsyncUploadInput, AsyncDownloadInput } from './types'


const Oss: OssType = NativeModules.Oss;

const defaultConfig: DefaultConfigType = {
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

    public config: DefaultConfigType = defaultConfig;

    constructor(config: Partial<DefaultConfigType>) {
        this.setConfig(config);
        this.initWithPlainTextAccessKey();
    }

    setConfig(config: Partial<DefaultConfigType>) {
        this.config = _.merge(this.config, config);
    }

    initWithPlainTextAccessKey() {
        Oss.initWithPlainTextAccessKey(this.config.accessKey, this.config.secretKey, this.config.endPoint, this.config.configuration);
    }

    /**
     * Asynchronously uploading
     */
    asyncUpload(input: AsyncUploadInput) {
        return Oss.asyncUpload(this.config.bucketName, input.objectKey, input.filepath);
    }

    /**
   * Asynchronously downloading
   */
    asyncDownload(input: AsyncDownloadInput) {
        return Oss.asyncDownload(this.config.bucketName, input.objectKey, input.filepath);
    }

}

export default AliyunOss;