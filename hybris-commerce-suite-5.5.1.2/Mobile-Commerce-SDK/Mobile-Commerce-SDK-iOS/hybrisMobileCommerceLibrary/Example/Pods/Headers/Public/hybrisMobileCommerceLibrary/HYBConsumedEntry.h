//
//  HYBConsumedEntry.h
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

@interface HYBConsumedEntry : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSNumber  *adjustedUnitPrice;
@property (nonatomic) NSString  *code;
@property (nonatomic) NSNumber  *orderEntryNumber;
@property (nonatomic) NSNumber  *quantity;

+ (instancetype)consumedEntryWithParams:(NSDictionary *)params;

@end
