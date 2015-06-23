//
//  HYBStore.h
// [y] hybris Platform
//
// Copyright (c) 2000-2015 hybris AG
// All rights reserved.
//
// This software is the confidential and proprietary information of hybris
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with hybris.

#import <Foundation/Foundation.h>
#import "MTLModel.h"
#import "MTLJSONAdapter.h"

//key definitions for openingHours
#define storeKeyClosingTime     @"closingTime"
#define storeKeyOpeningTime     @"openingTime"
#define storeKeyFormattedHour   @"formattedHour"
#define storeKeyHour            @"hour"
#define storeKeyMinute          @"minute"
#define storeKeyClosed          @"closed"
#define storeKeyWeekDay         @"weekDay"

/**
 * The class representing a store.
 */

@interface HYBStore : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString *name;
@property (nonatomic) NSString *displayName;
@property (nonatomic) NSString *latitude;
@property (nonatomic) NSString *longitude;

//TODO:refactor to use HYBAddress
@property (nonatomic) NSString *addressId;
@property (nonatomic) NSString *formattedAddress;
@property (nonatomic) NSString *line1;
@property (nonatomic) NSString *line2;
@property (nonatomic) NSString *regionName;
@property (nonatomic) NSString *town;
@property (nonatomic) NSString *postalCode;

@property (nonatomic) NSString *phone;
@property (nonatomic) NSString *countryName;

@property (nonatomic) NSArray *openingHours;

+ (instancetype)storeWithParams:(NSDictionary*)params;

@end
