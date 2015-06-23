//
//  HYBProduct.m
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

#import "HYBProduct.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"
#import "NSObject+HYBAdditionalMethods.h"

#import "HYBVariantMatrixElement.h"
#import "HYBProductVariantOption.h"

@implementation HYBProduct {
    NSNumber *_stockHolder;
    NSNumber *_priceHolder;
    
    NSString *_thumbnailURL;
    NSString *_imageURL;
    
    NSArray *_galleryImagesData;
    NSArray *_volumePricingData;
    NSArray *_variants;
}

+ (instancetype)productWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBProduct *product = [MTLJSONAdapter modelOfClass:[HYBProduct class]
                                fromJSONDictionary:params
                                             error:&error];
    
    [product extractImages];
    
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBProduct models: %@", error);
        return nil;
    }
    
    return product;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"availableForPickup"  : @"availableForPickup",
             @"baseOptions"         : @"baseOptions",
             @"baseProduct"         : @"baseProduct",
             @"categories"          : @"categories",
             @"code"                : @"code",
             @"firstVariantCode"    : @"firstVariantCode",
             @"firstVariantImage"   : @"firstVariantImage",
             @"productDescription"  : @"description",
             @"images"              : @"images",
             @"manufacturer"        : @"manufacturer",
             @"multidimensional"    : @"multidimensional",
             @"name"                : @"name",
             @"numberOfReviews"     : @"numberOfReviews",
             @"productPrice"        : @"price",
             @"productPriceRange"   : @"priceRange",
             @"purchasable"         : @"purchasable",
             @"productStock"        : @"stock",
             @"summary"             : @"summary",
             @"url"                 : @"url",
             @"volumePrices"        : @"volumePrices",
             @"variantMatrix"       : @"variantMatrix"
             };
}

//dicts
+ (NSValueTransformer *)productPriceJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)productPriceRangeJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPriceRange class]];
}

+ (NSValueTransformer *)productStockJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBStock class]];
}

//arrays
+ (NSValueTransformer *)baseOptionsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBProductBaseOption class]];
}

+ (NSValueTransformer *)categoriesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBCategory class]];
}

+ (NSValueTransformer *)imagesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBImage class]];
}

+ (NSValueTransformer *)volumePricesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)variantMatrixJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBVariantMatrixElement class]];
}

//legacy

- (NSString*)desc {
    return _productDescription;
}

- (NSString*)formattedPrice {
    
    if (_productPriceRange) {
        NSString *min = _productPriceRange.minPrice.formattedValue;
        NSString *max = _productPriceRange.maxPrice.formattedValue;
        
        if ([min hyb_isNotBlank] && [max hyb_isNotBlank]) {
            return [NSString stringWithFormat:@"%@-%@", min, max];
        }
    }
    
    return _productPrice.formattedValue;
}

- (NSString*)basePriceFormattedValue {
    return _productPrice.formattedValue;
}

- (NSString*)currencyIso {
     return _productPrice.currencyIso;
}

- (NSString*)thumbnailURL {
    if (!_thumbnailURL) {
        [self extractImages];
    }
    
    return _thumbnailURL;
}

- (NSString*)imageURL {
    if (!_imageURL) {
        [self extractImages];
    }
    
    return _imageURL;
}

- (NSString*)priceRange {
    return [self formattedPrice];
}

- (NSString*)currencySign {
    //TODO: internationalized
   return @"$";
}

- (NSString *)deliveryDetails {
    return @"default_delivery_details";
}

- (NSString *)reviews {
    return @"default_reviews";
}

- (NSArray*)galleryImagesData {
    
    if (!_galleryImagesData) {
        [self extractImages];
    }
    
    return _galleryImagesData;
}

- (NSNumber*)price {
    return _productPrice.value;
}

- (NSNumber*)stock {
    [self lowStock];
    return _stockHolder;
}

- (BOOL)lowStock {
    if (_productStock) {
        NSNumber *stockLevel = _productStock.stockLevel;
        NSString *stockCode = _productStock.stockLevelStatus;
        
        if ([stockCode isEqualToString:@"outOfStock"]) {
            _stockHolder = [NSNumber numberWithInt:0];
        } else if ([stockCode isEqualToString:@"lowStock"]) {
            _stockHolder = stockLevel;
            return YES;
        } else if ([stockCode isEqualToString:@"inStock"]) {
            if (stockLevel) {
                _stockHolder = stockLevel;
            } else {
                // it is in stock but no concrete value provided
                _stockHolder = [NSNumber numberWithInt:-1];
            }
        }
    }
    
    return NO;
}

//

- (NSString *)firstVariantCode {
    if([_firstVariantCode hyb_isNotBlank]) {
        return _firstVariantCode;
    }
    
    return _code;
}

- (NSString *)label {
    return [NSString stringWithFormat:@"%@ for %@", _name, [self formattedPrice]];
}

- (NSString *)description {
    NSMutableString *description = [NSMutableString stringWithFormat:@"<%@ - %@>", _code, _name];
    
    [description appendString:@">"];
    return description;
}

- (BOOL)isInStock {
    
    BOOL isInStock = NO;
    
    if (_productStock) {
        int intStock = [_productStock.stockLevel intValue];
        if(intStock > 0 || intStock == -1) {
            isInStock = YES;
        }
        
        if([_productStock.stockLevelStatus isEqualToString:@"inStock"]) {
            isInStock = YES;
        }
    }
    
    return isInStock;
}

- (BOOL)isVolumePricingPresent {
    return [_volumePrices hyb_isNotBlank];
}

- (NSString *)pricingValueForItemAtIndex:(int)index {
    NSString *result = nil;
    
    if (self.isVolumePricingPresent) {
        HYBPrice *price = [_volumePrices objectAtIndex:index];
        result = price.formattedValue;
    }
    
    return result;
}

- (void)extractImages {
    
    NSMutableArray *tmpArray = [NSMutableArray array];
    
    if (!_thumbnailURL && _firstVariantImage) {
        _thumbnailURL = _firstVariantImage;
    }
    
    for (HYBImage *image in _images) {
        
        //check format
        if([image.format isEqualToString:@"thumbnail"]) {
            _thumbnailURL = image.url;
        }
        
        else if([image.format isEqualToString:@"product"]) {
            _imageURL = image.url;
        }
      
        //check type
        if([image.imageType isEqualToString:GALLERY]) {
            
            NSDictionary *imageDict = [image asDictionnary];            
            [tmpArray addObject:imageDict];
        }
    }
    
    if ([tmpArray hyb_isNotBlank]) {
        _galleryImagesData = [NSArray arrayWithArray:tmpArray];
    } else {
        _galleryImagesData = [NSArray array];
    }
}

- (NSDictionary *)asDictionary {
    NSMutableDictionary *result = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                   _name,@"name",
                                   _code,@"code",
                                   nil];
    
    if ([_thumbnailURL hyb_isNotBlank]) {
        [result setObject:self.thumbnailURL forKey:@"thumbnailURL"];
    }
    if ([[self formattedPrice] hyb_isNotBlank]) {
        [result setObject:self.formattedPrice forKey:@"formattedPrice"];
    }
    if (_multidimensional) {
        [result setObject:[NSNumber numberWithBool:_multidimensional] forKey:@"multidimensional"];
    }
    if (_productPrice) {
        [result setObject:_productPrice.value forKey:@"price"];
    }
    return [NSDictionary dictionaryWithDictionary:result];
}

- (NSArray*)volumePricingData {
    
    if (!_volumePricingData) {
        
        NSMutableArray *tmpArray = [NSMutableArray array];
        
        for (HYBPrice *price in _volumePrices) {
           [tmpArray addObject:[price asDictionary]];
        }
        
        if ([tmpArray hyb_isNotBlank]) {
            _volumePricingData = [NSArray arrayWithArray:tmpArray];
        }
    }
    
    return _volumePricingData;
}

- (NSString *)quantityValueForItemAtIndex:(int)index {
    NSString *result = @"";
    if ([self isVolumePricingPresent]) {
        HYBPrice *price = _volumePrices[index];
        result = [NSString stringWithFormat:@"%@-%@", price.minQuantity, price.maxQuantity];
    }
    return result;
}

- (int)variantDimensionsNumber {
    if ([self.variants hyb_isNotBlank]) {
        return [[self.variants firstObject] variantDimensionsNumber];
    } else {
        return 0;
    }
}

- (NSArray*)variants {
    
    if (!_variants) {
        
        NSMutableArray *tmpArray = [NSMutableArray array];
        
        for(HYBVariantMatrixElement *element in _variantMatrix) {
            
            HYBProductVariantOption *variantOption = [[HYBProductVariantOption alloc] initWithElement:element];
            [tmpArray addObject:variantOption];
        }
        
        _variants = [NSArray arrayWithArray:tmpArray];
    }
    
    return _variants;
}

@end
