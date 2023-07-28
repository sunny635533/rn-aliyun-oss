export declare type Configuration = {
    timeoutIntervalForRequest: number;
    maxRetryCount: number;
    timeoutIntervalForResource: number;
};
export declare type DefaultConfigType = {
    configuration: Configuration;
    accessKey: string;
    secretKey: string;
    endPoint: string;
    bucketName: string;
};
export declare type OssType = {
    initWithPlainTextAccessKey(accessKeyId: string, accessKeySecret: string, endPoint: string, configuration: Configuration): void;
    initWithImplementedSigner(signature: string, accessKey: string, endPoint: string, configuration: Configuration): void;
    initWithSecurityToken(securityToken: string, accessKey: string, secretKey: string, endPoint: string, configuration: Configuration): void;
    asyncUpload(bucketName: string, ossFile: string, sourceFile: string): Promise<string>;
    asyncDownload(bucketName: string, ossFile: string, updateDate: string): Promise<string>;
};
export declare type AsyncUploadInput = {
    /**
     * oss储存路径
     */
    objectKey: string;
    /**
     * 源文件路径
     */
    filepath: string;
};
export declare type AsyncDownloadInput = {
    /**
    * oss储存路径
    */
    objectKey: string;
    /**
    * 源文件路径
    */
    filepath: string;
};
