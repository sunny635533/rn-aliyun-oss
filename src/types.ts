export type Configuration = {
    timeoutIntervalForRequest: number
    maxRetryCount: number,
    timeoutIntervalForResource: number,
}

export type DefaultConfigType = {
    configuration: Configuration
    accessKey: string
    secretKey: string
    endPoint: string
    bucketName: string
}

export type OssType = {
    initWithPlainTextAccessKey(accessKeyId: string, accessKeySecret: string, endPoint: string, configuration: Configuration): void
    initWithImplementedSigner(signature: string, accessKey: string, endPoint: string, configuration: Configuration): void
    initWithSecurityToken(securityToken: string, accessKey: string, secretKey: string, endPoint: string, configuration: Configuration): void
    asyncUpload(bucketName: string, ossFile: string, sourceFile: string): Promise<string>
    asyncDownload(bucketName: string, ossFile: string, updateDate: string): Promise<string>
};

export type AsyncUploadInput = {
    /**
     * oss储存路径
     */
    objectKey: string
    /**
     * 源文件路径
     */
    filepath: string
}

export type AsyncDownloadInput = {
    /**
    * oss储存路径
    */
    objectKey: string
    /**
    * 源文件路径
    */
    filepath: string
}