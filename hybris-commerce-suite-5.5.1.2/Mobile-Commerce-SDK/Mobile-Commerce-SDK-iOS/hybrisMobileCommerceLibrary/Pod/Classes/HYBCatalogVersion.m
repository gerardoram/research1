//
//  HYBCatalogVersion.m
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


#import "HYBCatalogVersion.h"
#import "HYBCategory.h"
#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBCatalogVersion

+ (instancetype)catalogVersionsWithParams:(NSDictionary*)params {
    return [MTLJSONAdapter modelOfClass:[HYBCatalogVersion class] fromJSONDictionary:params error:nil];
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"id" : @"id",
             @"url" : @"url",
             @"categories" : @"categories"
             };
}

+ (NSValueTransformer *)categoriesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBCategory class]];
}

@end
