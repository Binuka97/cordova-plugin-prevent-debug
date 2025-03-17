#import <Cordova/CDV.h>
#import <sys/sysctl.h>
#import <dlfcn.h>
#import <sys/types.h>

typedef int (*ptrace_ptr_t)(int _request, pid_t _pid, caddr_t _addr, int _data);

@interface SecurityCheck : CDVPlugin
- (void)check:(CDVInvokedUrlCommand*)command;
@end

@implementation SecurityCheck

- (void)check:(CDVInvokedUrlCommand*)command {
    [self antiDebug]; // Call anti-debugging method
    
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

// 🛡️ Anti-debugging method using ptrace
- (void)antiDebug {
    ptrace_ptr_t ptrace_ptr = (ptrace_ptr_t)dlsym(RTLD_SELF, "ptrace");
    if (ptrace_ptr) {
        ptrace_ptr(31, 0, 0, 0); // PTRACE_DENY_ATTACH = 31
    }
}

@end
