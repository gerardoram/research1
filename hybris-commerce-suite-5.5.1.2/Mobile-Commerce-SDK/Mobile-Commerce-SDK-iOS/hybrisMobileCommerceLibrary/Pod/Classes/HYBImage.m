//
//  HYBImage.m
// [y] hybris Platform
//
// Copyright (c) 2000-2015 hybris AG
// All rights reserved.
//
// This software is the confidential and proprietary information of hybris
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with hybris.
//

#import "HYBImage.h"
#import "NSObject+HYBAdditionalMethods.h"

@implementation HYBImage

+ (instancetype)imageWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBImage *image = [MTLJSONAdapter modelOfClass:[HYBImage class]
                                fromJSONDictionary:params
                                             error:&error];
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBImage models: %@", error);
        return nil;
    }
    
    return image;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"altText"     : @"altText",
             @"format"      : @"format",
             @"imageType"   : @"imageType",
             @"url"         : @"url"
             };
}

//legacy
- (NSDictionary*)asDictionnary {
    NSMutableDictionary *tmpDict = [NSMutableDictionary dictionary];
    
    if([_altText hyb_isNotBlank])   [tmpDict setObject:_altText     forKey:@"altText"];
    if([_format hyb_isNotBlank])    [tmpDict setObject:_format      forKey:@"format"];
    if([_imageType hyb_isNotBlank]) [tmpDict setObject:_imageType   forKey:@"imageType"];
    if([_url hyb_isNotBlank])       [tmpDict setObject:_url         forKey:@"url"];
    
    return [NSDictionary dictionaryWithDictionary:tmpDict];
}

@end