//
//  HYBEntry.h
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
#import "HYBProduct.h"

@interface HYBEntry : MTLModel <MTLJSONSerializing>

@property (nonatomic) HYBPrice *basePrice;
@property (nonatomic) NSNumber *entryNumber;
@property (nonatomic) HYBProduct *product;
@property (nonatomic) NSNumber *quantity;
@property (nonatomic) HYBPrice *totalPrice;
@property (nonatomic) BOOL updateable;

//check this one
@property (nonatomic) NSString *discountMessage;

+ (instancetype)entryWithParams:(NSDictionary*)params;

//legacy
- (NSString *)totalPriceFormattedValue;
- (NSNumber *)price;
- (NSString *)basePriceFormattedValue;
- (NSString *)discountMessage;


- (NSDictionary *)asDictionary;
@end
