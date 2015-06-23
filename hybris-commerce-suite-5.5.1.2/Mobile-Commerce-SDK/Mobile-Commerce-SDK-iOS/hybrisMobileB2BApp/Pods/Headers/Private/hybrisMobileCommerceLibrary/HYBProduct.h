//
//  HYBProduct.h
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

#import <Foundation/Foundation.h>
#import "MTLJSONAdapter.h"
#import "MTLModel.h"

#import "HYBPrice.h"
#import "HYBStock.h"
#import "HYBImage.h"
#import "HYBCategory.h"
#import "HYBPriceRange.h"
#import "HYBProductBaseOption.h"

#define GALLERY @"GALLERY"
#define PRIMARY @"PRIMARY"

@interface HYBProduct : MTLModel <MTLJSONSerializing>

@property (nonatomic) BOOL      availableForPickup;
@property (nonatomic) NSArray   *baseOptions;
@property (nonatomic) NSString  *baseProduct;
@property (nonatomic) NSArray   *categories;
@property (nonatomic) NSString  *code;
@property (nonatomic) NSString  *firstVariantCode;
@property (nonatomic) NSString  *firstVariantImage;
@property (nonatomic) NSString  *productDescription;
@property (nonatomic) NSArray   *images;
@property (nonatomic) NSString  *manufacturer;
@property (nonatomic) BOOL      multidimensional;
@property (nonatomic) NSString  *name;
@property (nonatomic) NSNumber  *numberOfReviews;
@property (nonatomic) HYBPrice      *productPrice;
@property (nonatomic) HYBPriceRange *productPriceRange;
@property (nonatomic) BOOL      purchasable;
@property (nonatomic) HYBStock  *productStock;
@property (nonatomic) NSString  *summary;
@property (nonatomic) NSString  *url;
@property (nonatomic) NSArray   *volumePrices;
@property (nonatomic) NSArray   *variantMatrix;

//not set by json

@property (nonatomic) NSString *baseStoreURL;

//legacy

- (NSString*)desc;
- (NSString*)formattedPrice;
- (NSString*)basePriceFormattedValue;
- (NSString*)currencyIso;
- (NSString*)thumbnailURL;
- (NSString*)imageURL;
- (NSString*)baseStoreURL;
- (NSString*)priceRange;
- (NSString*)currencySign;

- (NSArray*)variants;
- (NSArray*)galleryImagesData;
- (NSArray*)volumePricingData;

- (NSNumber*)price;
- (NSNumber*)stock;

- (BOOL)lowStock;

//end legacy

+ (instancetype)productWithParams:(NSDictionary*)params;

/**
 *  returns product name and formatted price
 *
 *  @return NSString label
 */
- (NSString *)label;

/**
 *  check stock level for product
 *
 *  @return BOOL isInStock;
 */
- (BOOL)isInStock;

/**
 *  returns reviews as string
 *
 *  @return default review //not supported by server yet
 */
- (NSString *)reviews;

/**
 *  returns delivery details as string
 *
 *  @return default delivery details //not supported by server yet
 */
- (NSString *)deliveryDetails;

/**
 *  check if product is using volume pricing
 *
 *  @return BOOL isVolumePricingPresent
 */
- (BOOL)isVolumePricingPresent;

/**
 *  returns product pricing for object at index from volumePricingData array
 *
 *  @param index int
 *
 *  @return NSString formatted price Value
 */
- (NSString *)pricingValueForItemAtIndex:(int)index;

/**
 *  returns product code of first product variant
 *
 *  @return NSString code
 */
- (NSString *)firstVariantCode;

/**
 *  returns quantity value for object at index from volumePricingData array
 *
 *  @param index int
 *
 *  @return NSString quantity
 */
- (NSString *)quantityValueForItemAtIndex:(int)index;

/**
 *  returns number of dimension if product has variants else returns 0
 *
 *  @return int number of dimensions
 */
- (int)variantDimensionsNumber;

/**
 *  returns product as a NSDictionary
 *
 *  @return NSDictionary params
 */
- (NSDictionary *)asDictionary;

@end




