//
//  HYBConsumedEntry.m
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


#import "HYBConsumedEntry.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBConsumedEntry


+ (instancetype)consumedEntryWithParams:(NSDictionary *)params {
    
    NSError *error = nil;
    
    HYBConsumedEntry *consumedEntry = [MTLJSONAdapter modelOfClass:[HYBConsumedEntry class]
                                                                    fromJSONDictionary:params
                                                                                 error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBAppliedOrderPromotion models: %@", error);
        return nil;
    }
    
    return consumedEntry;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"adjustedUnitPrice"   : @"adjustedUnitPrice",
             @"code"                : @"code",
             @"orderEntryNumber"    : @"orderEntryNumber",
             @"quantity"            : @"quantity"
             };
}

@end
