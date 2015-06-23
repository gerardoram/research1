//
//  HYBCart.h
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
#import "HYBPaymentType.h"

#define CART_OK     @"CART_OK"
#define CART_BAD    @"CART_BAD"

#define CART_PAYMENTTYPE_ACCOUNT    @"ACCOUNT"

@interface HYBCart : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString *status;

@property (nonatomic) NSString  *type;
@property (nonatomic) NSArray   *appliedOrderPromotions;
@property (nonatomic) NSArray   *appliedProductPromotions;
@property (nonatomic) BOOL      calculated;
@property (nonatomic) NSString  *code;
@property (nonatomic) NSNumber  *deliveryItemsQuantity;
@property (nonatomic) NSArray   *deliveryOrderGroups;
@property (nonatomic) NSArray   *entries;
@property (nonatomic) NSString  *guid;
@property (nonatomic) BOOL      net;
@property (nonatomic) NSNumber  *pickupItemsQuantity;
@property (nonatomic) HYBPrice  *productDiscounts;
@property (nonatomic) NSString  *site;
@property (nonatomic) NSString  *store;
@property (nonatomic) HYBPrice  *subTotal;
@property (nonatomic) HYBPrice  *totalDiscounts;
@property (nonatomic) NSNumber  *totalItems;
@property (nonatomic) HYBPrice  *totalTax;
@property (nonatomic) HYBPaymentType    *paymentType;
@property (nonatomic) NSArray   *potentialProductPromotions;
@property (nonatomic) NSNumber  *totalUnitCount;

@property (nonatomic) NSDictionary  *deliveryCost;
@property (nonatomic) NSString  *deliveryCode;

//temporary backward compatibility
@property (nonatomic) HYBPrice  *orderDiscountsB;
@property (nonatomic) HYBPrice  *totalPriceB;
@property (nonatomic) HYBPrice  *totalPriceWithTaxB;

/**
 *  init a cart object
 *
 *  @param params       cart as a NSDictionary
 *
 *  @return HYBCart cart
 */
+ (instancetype)cartWithParams:(NSDictionary*)params;


/**
 *  returns BOOL out of cart total item count (count == 0 => empty cart)
 *
 *  @return bool is cart empty
 */
- (BOOL)isEmpty;


//legacy accesssors

- (NSNumber*)totalPrice;
- (NSNumber*)totalPriceWithTax;
- (NSNumber*)orderDiscounts;

- (NSString*)totalTaxFormatted;
- (NSString*)totalPriceWithTaxFormatted;
- (NSString*)totalPriceFormatted;
- (NSString*)subTotalFormatted;
- (NSArray*)items;
- (NSString*)orderDiscountsFormattedValue;
- (NSString*)orderDiscountsMessage;
- (NSString*)paymentTypeCode;
- (NSString*)paymentDisplayName;

@end
