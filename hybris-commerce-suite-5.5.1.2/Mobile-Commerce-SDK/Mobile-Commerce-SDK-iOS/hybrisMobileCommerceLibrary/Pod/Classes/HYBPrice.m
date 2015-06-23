//
//  HYBPrice.m
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


#import "HYBPrice.h"

@implementation HYBPrice

+ (instancetype)priceWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBPrice *price = [MTLJSONAdapter modelOfClass:[HYBPrice class]
                                fromJSONDictionary:params
                                             error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBPrice models: %@", error);
        return nil;
    }
    
    return price;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"currencyIso"     : @"currencyIso",
             @"formattedValue"  : @"formattedValue",
             @"maxQuantity"     : @"maxQuantity",
             @"minQuantity"     : @"minQuantity",
             @"priceType"       : @"priceType",
             @"value"           : @"value"
             };
}

//legacy
- (NSDictionary*)asDictionary {
    NSMutableDictionary *tmpDict = [NSMutableDictionary dictionary];
    
    if(_currencyIso)       [tmpDict setObject:_currencyIso      forKey:@"currencyIso"];
    if(_formattedValue)    [tmpDict setObject:_formattedValue   forKey:@"formattedValue"];
    if(_maxQuantity)       [tmpDict setObject:_maxQuantity      forKey:@"maxQuantity"];
    if(_minQuantity)       [tmpDict setObject:_minQuantity      forKey:@"minQuantity"];
    if(_priceType)         [tmpDict setObject:_priceType        forKey:@"priceType"];
    if(_value)             [tmpDict setObject:_value            forKey:@"value"];
    
    return [NSDictionary dictionaryWithDictionary:tmpDict];
}

@end
