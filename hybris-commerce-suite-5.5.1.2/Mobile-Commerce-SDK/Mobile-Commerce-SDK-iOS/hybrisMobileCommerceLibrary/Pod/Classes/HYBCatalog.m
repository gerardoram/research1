//
//  HYBCatalog.m
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


#import "HYBCatalog.h"
#import "HYBCatalogVersion.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBCatalog

+ (instancetype)catalogWithParams:(NSDictionary*)params {
    return [MTLJSONAdapter modelOfClass:[HYBCatalog class] fromJSONDictionary:params error:nil];
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"id" : @"id",
             @"name" : @"name",
             @"url" : @"url",
             @"catalogVersions" : @"catalogVersions"
             };
}

+ (NSValueTransformer *)catalogVersionsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBCatalogVersion class]];
}

@end
