//
//  HYBProductOption.m
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


#import "HYBProductOption.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"
#import "MTLValueTransformer.h"

#import "HYBImage.h"

@implementation HYBProductOption


+ (instancetype)productOptionWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBProductOption *productOption = [MTLJSONAdapter modelOfClass:[HYBProductOption class]
                                fromJSONDictionary:params
                                             error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBProductOption models: %@", error);
        return nil;
    }
    
    return productOption;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"code"        : @"code",
             @"priceData"   : @"priceData",
             @"stock"       : @"stock",
             @"url"         : @"url",
             @"variantOptionQualifiers"     : @"variantOptionQualifiers"
             };
}

//dicts
+ (NSValueTransformer *)priceDataJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)stockJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBStock class]];
}

//special array
+ (NSValueTransformer *)variantOptionQualifiersJSONTransformer {
    return [MTLValueTransformer reversibleTransformerWithForwardBlock:^(id elements) {
        return [self variantOptionQualifiersFromString:elements];
    } reverseBlock:^(id elements) {
        return [self variantOptionQualifiersFromString:elements];
    }];
}

+ (NSArray*)variantOptionQualifiersFromString:(id)elements {
    
    NSMutableArray *tmpArray = [NSMutableArray array];
    
    NSArray *imagesArray = (NSArray*)elements;
    
    if(imagesArray){
        for (NSDictionary *imageData in imagesArray) {
            HYBImage *image = [HYBImage imageWithParams:imageData[@"image"]];
            if (image) [tmpArray addObject:image];
        }
    }
    
    if ([tmpArray count] > 0) {
        return [NSArray arrayWithArray:tmpArray];
    }
    
    return nil;
}

@end

