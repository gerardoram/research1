//
//  HYBPromotion.h
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

@interface HYBPromotion : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString  *code;
@property (nonatomic) NSString  *couldFireMessages;
@property (nonatomic) NSString  *promotionDescription;
@property (nonatomic) NSString  *endDate;
@property (nonatomic) NSArray   *firedMessages;
@property (nonatomic) NSString  *promotionType;

+ (instancetype)promotionWithParams:(NSDictionary*)params;


@end
