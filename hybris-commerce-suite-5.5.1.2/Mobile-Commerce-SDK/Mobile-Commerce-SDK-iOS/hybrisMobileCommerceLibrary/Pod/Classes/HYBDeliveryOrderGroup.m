//
//  HYBDeliveryOrderGroup.m
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


#import "HYBDeliveryOrderGroup.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

#import "HYBEntry.h"

@implementation HYBDeliveryOrderGroup

+ (instancetype)deliveryOrderGroupWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBDeliveryOrderGroup *deliveryOrderGroup = [MTLJSONAdapter modelOfClass:[HYBDeliveryOrderGroup class]
                                                          fromJSONDictionary:params
                                                                       error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBDeliveryOrderGroup models: %@", error);
        return nil;
    }
    
    return deliveryOrderGroup;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"entries"             : @"entries",
             @"totalPriceWithTax"   : @"totalPriceWithTax"
             };
}

//array
+ (NSValueTransformer *)entriesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBEntry class]];
}

//dicts
+ (NSValueTransformer *)totalPriceWithTaxJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

@end
