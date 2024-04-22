//
//  NFCModule.m
//  passportNFC
//
//  Created by Sviatoslav Ivanov on 4/18/24.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(NFCModule, NSObject)

// Expose the read method to JavaScript with the correct parameters
RCT_EXTERN_METHOD(read:(NSString *)mrzKey
                withResolver:(RCTPromiseResolveBlock)resolve
                withRejecter:(RCTPromiseRejectBlock)reject)

@end
