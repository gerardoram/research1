//
//  HYBProductBaseOption.m
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


#import "HYBProductBaseOption.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"


@implementation HYBProductBaseOption


+ (instancetype)productBaseOptionWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBProductBaseOption *productBaseOption = [MTLJSONAdapter modelOfClass:[HYBProductBaseOption class]
                                                        fromJSONDictionary:params
                                                                     error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBProductBaseOption models: %@", error);
        return nil;
    }
    
    return productBaseOption;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"options"     : @"options",
             @"selected"    : @"selected",
             @"variantType" : @"variantType"
             };
}

//dict
+ (NSValueTransformer *)optionsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBProductOption class]];
}

@end