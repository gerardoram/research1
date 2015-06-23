//
// HYBProductVariantOption.m
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


#import "HYBProductVariantOption.h"
#import "NSObject+HYBAdditionalMethods.h"

#import "HYBImage.h"

@implementation HYBProductVariantOption

- (id)initWithParams:(NSDictionary *)params {
    if (self = [super init]) {
        
        _code = [params valueForKeyPath:@"variantOption.code"];
        _categoryName = [params valueForKeyPath:@"parentVariantCategory.name"];
        _categoryValue = [params valueForKeyPath:@"variantValueCategory.name"];
        
        BOOL isLeaf = [[params valueForKeyPath:@"isLeaf"] boolValue];
        
        if (!isLeaf) {
            NSArray *elements = [params valueForKeyPath:@"elements"];
            NSMutableArray *subVariants = [[NSMutableArray alloc] initWithCapacity:[elements count]];
            for (NSDictionary *params in elements) {
                HYBProductVariantOption *var = [[HYBProductVariantOption alloc] initWithParams:params];
                [subVariants addObject:var];
            }
            _variants = [[NSArray alloc] initWithArray:subVariants];
        }
        
        NSArray *imageData = [params valueForKeyPath:@"variantOption.variantOptionQualifiers"];
        if(imageData){
            NSMutableArray *images = [[NSMutableArray alloc] initWithCapacity:[imageData count]];
                for (NSDictionary *data in imageData) {
                [images addObject:[data valueForKeyPath:@"image.url"]];
            }
            _images = [[NSArray alloc] initWithArray:images];
        }
    }
    return self;
}

- (instancetype)initWithElement:(HYBVariantMatrixElement *)element {
    
    if (self = [super init]) {
      
        _code           = element.variantOption.code;
        _categoryName   = element.parentVariantCategoryName;
        _categoryValue  = element.variantValueCategoryName;
        
        BOOL isLeaf     = element.isLeaf;

        if (!isLeaf) {
            NSArray *elements = element.elements;
            NSMutableArray *subVariants = [NSMutableArray array];
            
            for (HYBVariantMatrixElement *subElement in elements) {
                HYBProductVariantOption *var = [[HYBProductVariantOption alloc] initWithElement:subElement];
                [subVariants addObject:var];
            }
            _variants = [NSArray arrayWithArray:subVariants];
        }
        
        NSArray *imageData = element.variantOption.variantOptionQualifiers;
        if([imageData hyb_isNotBlank]){
            NSMutableArray *images = [NSMutableArray array];
            
            for (HYBImage *image in imageData) {
                [images addObject:image.url];
            }
            _images = [[NSArray alloc] initWithArray:images];
        }
        
    }
    
    return self;
}

- (int)variantDimensionsNumber {
    if ([self.variants hyb_isNotBlank]) {
        return 1 + [[self.variants firstObject] variantDimensionsNumber];
    } else {
        return 1;
    }
}

@end