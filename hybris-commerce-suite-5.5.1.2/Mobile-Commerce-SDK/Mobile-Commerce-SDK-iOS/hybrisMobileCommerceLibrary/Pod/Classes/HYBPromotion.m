//
//  HYBPromotion.m
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


#import "HYBPromotion.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBPromotion


+ (instancetype)promotionWithParams:(NSDictionary *)params {
    
    NSError *error = nil;
    
    HYBPromotion *promotion = [MTLJSONAdapter modelOfClass:[HYBPromotion class]
                                fromJSONDictionary:params
                                             error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBPromotion models: %@", error);
        return nil;
    }
    
    return promotion;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"code"                    : @"code",
             @"couldFireMessages"       : @"couldFireMessages",
             @"promotionDescription"    : @"description",
             @"endDate"                 : @"endDate",
             @"firedMessages"           : @"firedMessages",
             @"promotionType"           : @"promotionType"
             };
}


@end
