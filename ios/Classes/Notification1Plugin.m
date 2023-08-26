#import "Notification1Plugin.h"
#if __has_include(<notification1/notification1-Swift.h>)
#import <notification1/notification1-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "notification1-Swift.h"
#endif

@implementation Notification1Plugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNotification1Plugin registerWithRegistrar:registrar];
}
@end
