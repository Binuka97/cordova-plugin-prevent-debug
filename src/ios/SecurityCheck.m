#import <Cordova/CDV.h>
#import <sys/sysctl.h>

@interface SecurityCheck : CDVPlugin
- (void)check:(CDVInvokedUrlCommand*)command;
@end

@implementation SecurityCheck

- (void)check:(CDVInvokedUrlCommand*)command {
    BOOL isDebuggerAttached = [self isDebuggerAttached];
    BOOL isDevelopmentMode = [self isDevelopmentMode];

    NSMutableDictionary* result = [NSMutableDictionary dictionaryWithCapacity:2];
    [result setObject:@(isDebuggerAttached) forKey:@"debuggerAttached"];
    [result setObject:@(isDevelopmentMode) forKey:@"devOptionsEnabled"];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (BOOL)isDebuggerAttached {
    int name[4];
    struct kinfo_proc info;
    size_t info_size = sizeof(info);

    name[0] = CTL_KERN;
    name[1] = KERN_PROC;
    name[2] = KERN_PROC_PID;
    name[3] = getpid();

    if (sysctl(name, 4, &info, &info_size, NULL, 0) == -1) {
        return NO;
    }

    return (info.kp_proc.p_flag & P_TRACED) != 0;
}

- (BOOL)isDevelopmentMode {
    return [[NSUserDefaults standardUserDefaults] boolForKey:@"developer_mode"];
}

@end
