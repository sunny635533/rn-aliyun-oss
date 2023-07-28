#import "Oss.h"
#import <AliyunOSSiOS/OSSService.h>
#import <React/RCTConvert.h>

@import Foundation;

@implementation Oss {
    OSSClient *client;
}
@synthesize bridge = _bridge;

RCT_EXPORT_MODULE(Oss)

RCT_EXPORT_METHOD(initWithAppKey:(NSString*) accessKeyId
        accessKeySecret:(NSString*) accessKeySecret
        endpointVlaue:(NSString*) endpoint
        clientConfiguration:(NSDictionary *)configuration)
{
    NSLog(@"intiWithAppKey:%@,%@,%@",endpoint,accessKeyId,accessKeySecret);
    // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的访问控制章节
    id<OSSCredentialProvider> credential = [[OSSPlainTextAKSKPairCredentialProvider alloc] initWithPlainTextAccessKey:accessKeyId secretKey:accessKeySecret];
    OSSClientConfiguration * conf = [OSSClientConfiguration new];
//    conf.maxRetryCount = 3; // 网络请求遇到异常失败后的重试次数
//    conf.timeoutIntervalForRequest = 30; // 网络请求的超时时间
//    conf.timeoutIntervalForResource = 24 * 60 * 60; // 允许资源传输的最长时间
    conf.maxRetryCount = [RCTConvert int:configuration[@"maxRetryCount"]]; //default 3
    conf.timeoutIntervalForRequest = [RCTConvert double:configuration[@"timeoutIntervalForRequest"]]; //default 30
    conf.timeoutIntervalForResource = [RCTConvert double:configuration[@"timeoutIntervalForResource"]]; //default 24 * 60 * 60
    
    client = [[OSSClient alloc] initWithEndpoint:endpoint credentialProvider:credential clientConfiguration:conf];
}

RCT_EXPORT_METHOD(asyncUpload:(NSString*)bucketName
        objectKey:(NSString*) objectKey
        uploadFilePath:(NSString*) uploadFilePath
        resolver:(RCTPromiseResolveBlock)resolve
        rejecter:(RCTPromiseRejectBlock)reject)
{
    OSSPutObjectRequest * put = [OSSPutObjectRequest new];

    put.bucketName = bucketName;
    put.objectKey = objectKey;

//    NSData *data = [[NSFileManager defaultManager] contentsAtPath:uploadFilePath];
    NSString* path = [NSURL URLWithString:uploadFilePath].path;
    NSData *data = [NSData dataWithContentsOfFile:path];

//    put.uploadingFileURL = [NSURL fileURLWithPath:uploadFilePath];
    put.uploadingData = data; // 直接上传NSData
    
    put.uploadProgress = ^(int64_t bytesSent, int64_t totalByteSent, int64_t totalBytesExpectedToSend) {
        NSLog(@"%lld, %lld, %lld", bytesSent, totalByteSent, totalBytesExpectedToSend);
    };

    OSSTask * putTask = [client putObject:put];

    [putTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            NSLog(@"upload object success!");
            resolve(@"UploadSuccess");
        } else {
            NSLog(@"upload object failed, error: %@" , task.error);
            NSString* msg = [NSString stringWithFormat:@"%@" ,task.error];
            reject(@"UploadFail", msg, nil);
        }
        return nil;
    }];

// 可以等待任务完成
// [putTask waitUntilFinished];
}


//异步下载 参考项目：https://github.com/SpadeGod/react-native-aliyun-oss/blob/master/ios/RCTAliyunOSS/RCTAliyunOSS.m
// https://github.com/aliyun/aliyun-oss-react-native/blob/master/ios/RNAliyunOSS%2BAUTH.m
//RCT_REMAP_METHOD(downloadObjectAsync, bucketName:(NSString *)bucketName objectKey:(NSString *)objectKey updateDate:(NSString *)updateDate resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
//    OSSGetObjectRequest *request = [OSSGetObjectRequest new];
//    // required
//    request.bucketName = bucketName;
//    request.objectKey = objectKey;
//    // optional
//    request.downloadProgress = ^(int64_t bytesWritten, int64_t totalBytesWritten, int64_t totalBytesExpectedToWrite) {
//        NSLog(@"%lld, %lld, %lld", bytesWritten, totalBytesWritten, totalBytesExpectedToWrite);
//        [self sendEventWithName: @"downloadProgress" body:@{@"everySentSize":[NSString stringWithFormat:@"%lld",bytesWritten],
//                                                          @"currentSize": [NSString stringWithFormat:@"%lld",totalBytesWritten],
//                                                          @"totalSize": [NSString stringWithFormat:@"%lld",totalBytesExpectedToWrite]}];
//    };
//    NSString *docDir = [self getDocumentDirectory];
//    NSLog(objectKey);
//    NSURL *url = [NSURL fileURLWithPath:[docDir stringByAppendingPathComponent:objectKey]];
//    request.downloadToFileURL = url;
//    OSSTask *getTask = [client getObject:request];
//    [getTask continueWithBlock:^id(OSSTask *task) {
//        if (!task.error) {
//            NSLog(@"download object success!");
//            OSSGetObjectResult *result = task.result;
//            NSLog(@"download dota length: %lu", [result.downloadedData length]);
//            resolve(url.absoluteString);
//        } else {
//            NSLog(@"download object failed, error: %@" ,task.error);
//            reject(nil, @"download object failed", task.error);
//        }
//        return nil;
//    }];
//}

@end
