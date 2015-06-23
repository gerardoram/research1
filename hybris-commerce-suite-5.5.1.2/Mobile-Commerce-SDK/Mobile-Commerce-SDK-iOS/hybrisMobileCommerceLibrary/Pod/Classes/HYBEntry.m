//
//  HYBEntry.m
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


#import "HYBEntry.h"
#import "HYBImage.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBEntry

+ (instancetype)entryWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBEntry *entry = [MTLJSONAdapter modelOfClass:[HYBEntry class]
                                      fromJSONDictionary:params
                                                   error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBEntry models: %@", error);
        return nil;
    }
    
    return entry;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"basePrice"   : @"basePrice",
             @"entryNumber" : @"entryNumber",
             @"product"     : @"product",
             @"quantity"    : @"quantity",
             @"totalPrice"  : @"totalPrice",
             @"updateable"  : @"updateable"
             };
}

//dicts
+ (NSValueTransformer *)basePriceJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)productJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBProduct class]];
}

+ (NSValueTransformer *)totalPriceJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

//legacy
- (NSString*) totalPriceFormattedValue {
    return _totalPrice.formattedValue;
}

- (NSNumber*) price {
    return _basePrice.value;
}

- (NSString*) basePriceFormattedValue {
    return _basePrice.formattedValue;
}

- (NSDictionary *)asDictionary {
    return [NSDictionary dictionaryWithObjectsAndKeys:
            _entryNumber ,@"entryNumber",
            _quantity, @"quantity",
            _product.asDictionary, @"product",
            _basePrice.formattedValue, @"basePriceFormattedValue",
            _totalPrice.formattedValue, @"totalPriceFormattedValue",
            _discountMessage, @"discountMessage",
            nil];
}

@end
