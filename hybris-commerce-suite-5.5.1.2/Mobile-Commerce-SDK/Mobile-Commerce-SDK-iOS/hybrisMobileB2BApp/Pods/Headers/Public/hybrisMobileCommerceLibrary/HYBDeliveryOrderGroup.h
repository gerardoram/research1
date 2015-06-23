//
//  HYBDeliveryOrderGroup.h
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

@interface HYBDeliveryOrderGroup : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSArray   *entries;
@property (nonatomic) HYBPrice  *totalPriceWithTax;

+ (instancetype)deliveryOrderGroupWithParams:(NSDictionary*)params;

@end
