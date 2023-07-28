import type { DefaultConfigType, AsyncUploadInput, AsyncDownloadInput } from './types';
declare class AliyunOss {
    config: DefaultConfigType;
    constructor(config: Partial<DefaultConfigType>);
    setConfig(config: Partial<DefaultConfigType>): void;
    initWithPlainTextAccessKey(): void;
    /**
     * Asynchronously uploading
     */
    asyncUpload(input: AsyncUploadInput): Promise<string>;
    /**
   * Asynchronously downloading
   */
    asyncDownload(input: AsyncDownloadInput): Promise<string>;
}
export default AliyunOss;
