//
//  Config.h
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/24.
//

#ifndef Config_h
#define Config_h

// Api Url 前缀
static const NSString *ApiUrlPrefix = @"http://192.168.235.128:8080";

#define UIColorFromRGB(rgbValue) \
[UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
                green:((float)((rgbValue & 0x00FF00) >>  8))/255.0 \
                 blue:((float)((rgbValue & 0x0000FF) >>  0))/255.0 \
                alpha:1.0]

#endif /* Config_h */
