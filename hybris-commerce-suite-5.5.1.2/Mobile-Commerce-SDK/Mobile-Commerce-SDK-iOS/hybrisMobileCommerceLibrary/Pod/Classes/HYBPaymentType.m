//
//  HYBPaymentType.m
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


#import "HYBPaymentType.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBPaymentType

+ (instancetype)paymentType:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBPaymentType *paymentType = [MTLJSONAdapter modelOfClass:[HYBPaymentType class]
                                            fromJSONDictionary:params
                                                         error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBPaymentType models: %@", error);
        return nil;
    }
    
    return paymentType;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"code"        : @"code",
             @"displayName" : @"displayName"
             };
}

@end
