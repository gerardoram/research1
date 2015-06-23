//
//  HYBVariantMatrixElement.m
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


#import "HYBVariantMatrixElement.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@implementation HYBVariantMatrixElement

+ (instancetype)entryWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBVariantMatrixElement *variantMatrixElement = [MTLJSONAdapter modelOfClass:[HYBVariantMatrixElement class]
                                                              fromJSONDictionary:params
                                                                           error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBVariantMatrixElement models: %@", error);
        return nil;
    }
    
    return variantMatrixElement;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"isLeaf"                          : @"isLeaf",
             @"parentVariantCategoryHasImage"   : @"parentVariantCategory.hasImage",
             @"parentVariantCategoryName"       : @"parentVariantCategory.name",
             @"parentVariantCategoryPriority"   : @"parentVariantCategory.priority",
             @"variantOption"                   : @"variantOption",
             @"variantValueCategoryName"        : @"variantValueCategory.name",
             @"variantValueCategorySequence"    : @"variantValueCategory.sequence",
             @"elements"                        : @"elements"
             };
}

//dict
+ (NSValueTransformer *)variantOptionJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBProductOption class]];
}

//array
+ (NSValueTransformer *)elementsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBVariantMatrixElement class]];
}


@end
