//
// HYBOrder.m
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


#import <Mantle/NSValueTransformer+MTLPredefinedTransformerAdditions.h>
#import "HYBOrder.h"
#import "HYBAddress.h"
#import "HYBDeliveryMode.h"


@implementation HYBOrder

+ (instancetype)orderWithParams:(NSDictionary*)params {
    HYBOrder *tmpOrder = [MTLJSONAdapter modelOfClass:[HYBOrder class] fromJSONDictionary:params error:nil];
    [tmpOrder buildCartFromData:params];
    return tmpOrder;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
            @"code"             : @"code",
            @"statusDisplay"    : @"statusDisplay",
            @"total"            : @"total.formattedValue",
            @"totalValue"       : @"total.value",
            @"totalWithTax"     : @"totalPriceWithTax.formattedValue",
            @"deliveryAddress"  : @"deliveryAddress",
            @"deliveryMode"     : @"deliveryMode",
    };
}

//dicts
+ (NSValueTransformer *)deliveryAddressJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:HYBAddress .class];
}

+ (NSValueTransformer *)deliveryModeJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:HYBDeliveryMode .class];
}

- (void)buildCartFromData:(NSDictionary*)rawOrderValues {
    HYBCart *cart = [HYBCart cartWithParams:rawOrderValues];
    if (cart) _cart = cart;
}

@end