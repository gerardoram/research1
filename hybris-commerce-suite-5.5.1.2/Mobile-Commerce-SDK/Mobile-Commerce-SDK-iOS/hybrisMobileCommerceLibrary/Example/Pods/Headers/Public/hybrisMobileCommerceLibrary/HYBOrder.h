//
// HYBOrder.h
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
#import "HYBCart.h"
#import "MTLModel.h"
#import "MTLJSONAdapter.h"

@class HYBDeliveryMode;
@class HYBAddress;

/**
* The class representing an order. Order go through different states and are usually created at the checkout out
* of a present cart.
*/
@interface HYBOrder : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString *code;
@property (nonatomic) NSString *placed;
@property (nonatomic) NSString *created;
@property (nonatomic) NSString *statusDisplay;
@property (nonatomic) NSString *total;
@property (nonatomic) NSString *totalValue;
@property (nonatomic) NSString *totalWithTax;
@property (nonatomic) HYBCart *cart;
@property (nonatomic) HYBAddress *deliveryAddress;
@property (nonatomic) HYBDeliveryMode *deliveryMode;

+ (instancetype)orderWithParams:(NSDictionary*)params;

- (void)buildCartFromData:(NSDictionary*)rawOrderValues;

@end