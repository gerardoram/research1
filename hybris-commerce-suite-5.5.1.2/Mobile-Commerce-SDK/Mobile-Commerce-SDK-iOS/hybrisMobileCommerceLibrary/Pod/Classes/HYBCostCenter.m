//
// HYBCostCenter.m
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

#import "HYBCostCenter.h"
#import "HYBProductVariantOption.h"
#import "HYBAddress.h"
#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"


@implementation HYBCostCenter

+ (instancetype)costCenterWithParams:(NSDictionary*)params {
    return [MTLJSONAdapter modelOfClass:[HYBCostCenter class] fromJSONDictionary:params error:nil];
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
            @"code"      : @"code",
            @"name"      : @"name",
            @"addresses" : @"unit.addresses",
    };
}

+ (NSValueTransformer *)addressesJSONTransformer
{
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBAddress class]];
}

@end