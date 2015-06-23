//
// ProductListSpecificProductSpec.m
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

#import <Specta/Specta.h>
#define EXP_SHORTHAND
#import <Expecta/Expecta.h>
#import <CocoaLumberjack/DDTTYLogger.h>
#import <CocoaLumberjack/DDASLLogger.h>
#import "HYBProduct.h"
#import "HYB2BService.h"
#import "HYBAppDelegate.h"
#import "NSObject+HYBAdditionalMethods.h"
#import "HYBProductVariantOption.h"
#import "HYBCart.h"
#import "HYBEntry.h"
#import "HYBFileToJsonConverter.h"

SpecBegin(HYB2BProduct)
describe(@"ProductListSpecificProduct", ^{
    
    __block HYBProduct *product;
    __block HYBProduct *productLowStock;
    __block HYBProduct *productInStock;
    
    beforeAll(^{
        HYBFileToJsonConverter *jsonConverter = [[HYBFileToJsonConverter alloc] init];
        NSArray *products = [jsonConverter sampleProductList];
        
        product         = [products firstObject];
        productLowStock = [products lastObject];
        productInStock  = [products objectAtIndex:1];
        
        expect(product).to.beTruthy();
        expect(productLowStock).to.beTruthy();
        expect(productInStock).to.beTruthy();
    });
    it(@"should init a product from product list json", ^{
        expect(product.code).to.beTruthy();
        expect(product.summary).to.beTruthy();
        expect(product.desc).to.beTruthy();
        expect(product.thumbnailURL).to.beTruthy();
    });
    it(@"should retrieve the stock information no stock", ^{
        expect(product.isInStock).to.beFalsy();
    });
    it(@"should retrieve the stock information low stock", ^{
        expect(productLowStock.isInStock).to.beTruthy();
        expect(productLowStock.lowStock).to.beTruthy();
    });
    it(@"should retrieve the stock information in stock", ^{
        expect(productInStock.isInStock).to.beTruthy();
        // in stock but no stock data since in the list view
        int stock = productInStock.stock.intValue;
        expect(stock).to.equal(-1);
    });
    it(@"should recognize a multi-d product", ^{
        expect(product.multidimensional).to.beTruthy();
    });
    
    it(@"should have the firstVariantCode", ^{
        expect(product.firstVariantCode).to.beTruthy();
    });
    it(@"should have the firstVariantImage", ^{
        expect(product.firstVariantImage).to.beTruthy();
    });
    it(@"should have the currency data", ^{ //DEPRECATED use HYBPrice productPrice
        expect(product.currencyIso).to.equal(@"USD");
        expect(product.currencySign).to.equal(@"$");
    });
    it(@"should retrieve the productPrice", ^{
        HYBPrice *productPrice = product.productPrice;
        expect(productPrice).to.beTruthy();
        expect(productPrice.currencyIso).to.equal(@"USD");
        expect(productPrice.formattedValue).to.equal(@"$85.00");
        expect(productPrice.priceType).to.equal(@"BUY");
        expect(productPrice.value).to.equal(85);
    });
    it(@"should give a price range if multi-d product", ^{
        expect(product.price).to.equal(85);
        expect(product.formattedPrice).to.equal(@"$85.00-$97.00");
        expect(product.priceRange).to.equal(@"$85.00-$97.00");
    });
});

describe(@"ProductDetailsSpecificProduct", ^{
    __block HYBProduct *singleProduct;
    beforeAll(^{
        HYBFileToJsonConverter *jsonConverter = [[HYBFileToJsonConverter alloc] init];
        singleProduct = [jsonConverter sampleProduct];
    });
    
    beforeEach(^{
        expect(singleProduct).to.beTruthy();
    });
    
    it(@"should init a product from details page json", ^{
        expect(singleProduct.productDescription).to.beTruthy();
        expect(singleProduct.price).to.beTruthy();
        expect(singleProduct.thumbnailURL).to.beTruthy();
    });
    it(@"should list all images", ^{
        expect(singleProduct.imageURL).to.beTruthy();
        expect(singleProduct.galleryImagesData).to.beTruthy();
        expect([singleProduct.galleryImagesData count] > 0);
        
        int numberOfGalleryImages = 3;
        expect(singleProduct.galleryImagesData).to.haveCountOf(numberOfGalleryImages);
        
        expect([[singleProduct.galleryImagesData firstObject] objectForKey:@"imageType"]).to.equal(GALLERY);
    });
    it(@"should retrieve the stock", ^{
        expect([singleProduct stock] > 0).to.beTruthy();
        expect([singleProduct isInStock]).to.beTruthy();
    });
    it(@"should retrieve the summary", ^{
        NSString *summary = singleProduct.summary;
        expect(summary).to.beTruthy();
    });
    it(@"should retrieve the manufacturer", ^{
        NSString *manufacturer = singleProduct.manufacturer;
        expect(manufacturer).to.beTruthy();
    });
    it(@"should retrieve the productPrice", ^{
        HYBPrice *productPrice = singleProduct.productPrice;
        expect(productPrice).to.beTruthy();
        expect(productPrice.currencyIso).to.equal(@"USD");
        expect(productPrice.formattedValue).to.equal(@"$85.00");
        expect(productPrice.priceType).to.equal(@"BUY");
        expect(productPrice.value).to.equal(85);
    });
    it(@"should retrieve the productPriceRange", ^{
        HYBPriceRange *productPriceRange = singleProduct.productPriceRange;
        expect(productPriceRange.minPrice).to.beTruthy();
        expect(productPriceRange.maxPrice).to.beTruthy();
        
        expect(productPriceRange.minPrice.currencyIso).to.equal(@"USD");
        expect(productPriceRange.minPrice.formattedValue).to.equal(@"$85.00");
        expect(productPriceRange.minPrice.priceType).to.equal(@"FROM");
        expect(productPriceRange.minPrice.value).to.equal(85);
        
        expect(productPriceRange.maxPrice.currencyIso).to.equal(@"USD");
        expect(productPriceRange.maxPrice.formattedValue).to.equal(@"$97.00");
        expect(productPriceRange.maxPrice.priceType).to.equal(@"FROM");
        expect(productPriceRange.maxPrice.value).to.equal(97);
    });
    it(@"should retrieve the numberOfReviews", ^{
        NSNumber *numberOfReviews = singleProduct.numberOfReviews;
        expect(numberOfReviews).to.equal(0);
    });
    it(@"should retrieve the purchasable", ^{
        BOOL purchasable = singleProduct.purchasable;
        expect(purchasable).to.beTruthy();
    });
    it(@"should retrieve the baseOptions", ^{
        NSArray *baseOptions = singleProduct.baseOptions;
        expect(baseOptions).to.beTruthy();
        expect([singleProduct.baseOptions count] > 0);
    });
    it(@"should retrieve the categories", ^{
        NSArray *categories = singleProduct.categories;
        expect(categories).to.beTruthy();
        expect([singleProduct.categories count] == 3);
        
        //check category
        HYBCategory *firstCategory = [categories firstObject];
        expect(firstCategory).to.beTruthy();
        
        expect(firstCategory.code).to.equal(@"B2B_5");
        expect(firstCategory.url).to.equal(@"/Color/Fit/Size/5/c/B2B_5");
        
    });
    it(@"should retrieve the price", ^{
        expect([[singleProduct price] floatValue]).to.beTruthy();
    });
    
    it(@"should retrieve formatted price", ^{
        NSString *formattedPrice = singleProduct.formattedPrice;
        expect(formattedPrice).to.beTruthy();
    });
    it(@"should retrieve currency iso", ^{ //DEPRECATED
        NSString *currency = singleProduct.currencyIso;
        expect(currency).to.beTruthy();
    });
    it(@"should provide volume pricing data", ^{
        NSArray *volumePrices = singleProduct.volumePrices;
        expect(volumePrices).to.beTruthy();
        expect([volumePrices count] == 5).to.beTruthy();
        
        HYBPrice *firstItemPrice = [volumePrices firstObject];
        expect(firstItemPrice).to.beTruthy();
        expect(firstItemPrice.currencyIso).to.equal(@"USD");
        expect(firstItemPrice.maxQuantity).to.equal(9);
        expect(firstItemPrice.minQuantity).to.equal(1);
        expect(firstItemPrice.priceType).to.equal(@"BUY");
        expect(firstItemPrice.value).to.equal(16);
        expect(firstItemPrice.formattedValue).to.equal(@"$16.00");
    });
    
    it(@"should provide volume pricing data", ^{  //DEPRECATED use HYBProduct's *volumePrices (NSArray of HYBPrice objects)
        NSArray *volumePrices = [singleProduct volumePricingData];
        expect(volumePrices).to.beTruthy();
        expect([volumePrices count] == 5).to.beTruthy();
        NSDictionary *firstPricingItem = [volumePrices firstObject];
        expect(firstPricingItem).to.beTruthy();
        expect([firstPricingItem objectForKey:@"currencyIso"]).to.equal(@"USD");
        expect([firstPricingItem objectForKey:@"maxQuantity"]).to.beKindOf([NSNumber class]);
        expect([firstPricingItem objectForKey:@"minQuantity"]).to.beKindOf([NSNumber class]);
    });
    
    
    it(@"should provide pricing and quantity for item at given index", ^{
        NSString *pricing = [singleProduct pricingValueForItemAtIndex:0];
        NSString *qty = [singleProduct quantityValueForItemAtIndex:0];
        
        expect(pricing).to.equal(@"$16.00");
        expect(qty).to.equal(@"1-9");
    });
    it(@"should create multi-d variants tree", ^{  //DEPRECATED use HYBProduct's *variantMatrix (NSArray of HYBVariantMatrixElement objects)
        NSArray *variants = [singleProduct variants];
        expect(variants).to.beTruthy();
        expect([variants count]).to.equal(3);
    });
    it(@"should retrieve variantMatrix", ^{
        NSArray *variantMatrix = singleProduct.variantMatrix;
        expect(variantMatrix).to.beTruthy();
        expect([variantMatrix count]).to.equal(3);
        
        //check HYBVariantMatrixElement
        HYBVariantMatrixElement *firstElement = [variantMatrix firstObject];
        expect(firstElement).to.beTruthy();
        expect(firstElement.isLeaf).to.beFalsy();
        expect(firstElement.parentVariantCategoryHasImage).to.beTruthy();
        expect(firstElement.parentVariantCategoryName).to.equal(@"Color");
        expect(firstElement.parentVariantCategoryPriority).to.equal(0);
        
        expect(firstElement.variantValueCategoryName).to.equal(@"Black");
        expect(firstElement.variantValueCategorySequence).to.equal(1);
        
        expect(firstElement.elements).to.beTruthy();
        expect([firstElement.elements count]).to.equal(2);
        
        expect(firstElement.variantOption).to.beTruthy();
        
        //check HYBProductOption
        HYBProductOption *variantOption = firstElement.variantOption;
        expect(variantOption.code).to.equal(@"72399000_53");
        expect(variantOption.priceData).to.beFalsy();
        expect(variantOption.stock).to.beFalsy();
        expect(variantOption.url).to.beTruthy();
        expect(variantOption.variantOptionQualifiers).to.beTruthy();
        expect([variantOption.variantOptionQualifiers count]).to.equal(5);
        
        //check variantOptionQualifiers aka HYBImage array
        HYBImage *firstImage = [variantOption.variantOptionQualifiers firstObject];
        expect(firstImage.altText).to.beFalsy();
        expect(firstImage.format).to.equal(@"styleSwatch");
        expect(firstImage.imageType).to.beFalsy();
        expect(firstImage.url).to.beTruthy();
    });
    
    it(@"should calculate number of dimensions", ^{
        int actual = singleProduct.variantDimensionsNumber;
        expect(actual).to.equal(3);
        
        NSArray *variants = [singleProduct variants];
        HYBProductVariantOption *variant = [variants firstObject];
        int childDimNumber = variant.variantDimensionsNumber;
        expect(childDimNumber).to.equal(3);
        
        HYBProductVariantOption *secondDimVariant = [[variant variants] firstObject];
        int secDimNumber = secondDimVariant.variantDimensionsNumber;
        expect(secDimNumber).to.equal(2);
    });
    it(@"should create a proper product variant option in the tree", ^{
        NSArray *variants = [singleProduct variants];
        
        HYBProductVariantOption *variant = [variants firstObject];
        
        NSString *code = variant.code;
        NSString *categoryName = variant.categoryName;
        NSString *categoryValue = variant.categoryValue;
        
        expect(code).to.beTruthy();
        expect(categoryName).to.beTruthy();
        expect(categoryValue).to.beTruthy();
        
        NSArray *subVariants = variant.variants;
        expect(subVariants).to.beTruthy();
        expect([subVariants count]).to.equal(2);
        
        NSArray *images = variant.images;
        expect(images).to.beTruthy();
        expect([images count]).to.equal(5);
        
        HYBProductVariantOption *subVariant = [subVariants firstObject];
        expect(subVariant.code).to.beTruthy();
        expect(subVariant.categoryName).to.beTruthy();
        expect(subVariant.categoryValue).to.beTruthy();
    });
});
describe(@"CartItemProduct", ^{
    //__block NSDictionary *json;
    __block HYBCart *cart;
    
    beforeAll(^{
        HYBFileToJsonConverter *jsonConverter = [[HYBFileToJsonConverter alloc] init];
        cart = [jsonConverter sampleCart];
    });
    it(@"should create the product on the cart item", ^{
        HYBEntry *item = cart.items.firstObject;
        HYBProduct *p = item.product;
        expect(p).to.beTruthy();
        expect(p.code).to.beTruthy();
        expect(p.name).to.beTruthy();
        expect(p.thumbnailURL).to.beTruthy();
    });
    it(@"should create the product on the cart item", ^{
        HYBEntry *item = cart.items.firstObject;
        HYBProduct *p = item.product;
        expect(p.code).to.beTruthy();
        expect(p.name).to.beTruthy();
        
        expect(p.asDictionary).to.beTruthy();
    });
    it(@"should save and retrieve the whole cart to archive", ^{
        HYBEntry *item = cart.items.firstObject;
        HYBProduct *product = item.product;
        expect(product.asDictionary).to.beTruthy();
        NSString *productKey = @"mySavedProduct";
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        
        NSData *encodedObject = [NSKeyedArchiver archivedDataWithRootObject:product];
        [defaults setObject:encodedObject forKey:productKey];
        [defaults synchronize];
        
        NSData *loadedObject = [defaults objectForKey:productKey];
        HYBProduct *retrievedProduct =  [NSKeyedUnarchiver unarchiveObjectWithData:loadedObject];
        
        expect(retrievedProduct).to.beTruthy();
        expect(retrievedProduct.name).to.beTruthy();
        expect(retrievedProduct.code).to.beTruthy();
        expect(retrievedProduct.thumbnailURL).to.beTruthy();
        [defaults removeObjectForKey:productKey];
    });
});
SpecEnd


