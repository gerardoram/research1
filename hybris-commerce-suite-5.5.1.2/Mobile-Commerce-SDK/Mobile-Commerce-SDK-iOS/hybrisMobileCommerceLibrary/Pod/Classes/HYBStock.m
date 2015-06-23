//
//  HYBStock.m
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

#import "HYBStock.h"

@implementation HYBStock

+ (instancetype)stockWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBStock *stock = [MTLJSONAdapter modelOfClass:[HYBStock class]
                                fromJSONDictionary:params
                                             error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBStock models: %@", error);
        return nil;
    }
    
    return stock;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"stockLevel"          : @"stockLevel",
             @"stockLevelStatus"    : @"stockLevelStatus"
             };
}

@end
