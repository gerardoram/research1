//
//  HYBPriceRange.m
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

#import "HYBPriceRange.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBPriceRange

+ (instancetype)priceRangeWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBPriceRange *priceRange = [MTLJSONAdapter modelOfClass:[HYBPriceRange class]
                                fromJSONDictionary:params
                                             error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBPriceRange models: %@", error);
        return nil;
    }
    
    return priceRange;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"minPrice"  : @"minPrice",
             @"maxPrice"  : @"maxPrice"
             };
}

//dicts
+ (NSValueTransformer *)minPriceJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)maxPriceJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

@end
