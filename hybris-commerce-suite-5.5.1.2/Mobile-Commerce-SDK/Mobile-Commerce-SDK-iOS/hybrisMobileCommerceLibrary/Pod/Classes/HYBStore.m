//
//  HYBStore.m
// [y] hybris Platform
//
// Copyright (c) 2000-2015 hybris AG
// All rights reserved.
//
// This software is the confidential and proprietary information of hybris
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with hybris.
#import "HYBStore.h"

@implementation HYBStore

+ (instancetype)storeWithParams:(NSDictionary*)params {
   return [MTLJSONAdapter modelOfClass:[HYBStore class] fromJSONDictionary:params error:nil];
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"name"                : @"name",
             @"displayName"         : @"displayName",
             @"latitude"            : @"geoPoint.latitude",
             @"longitude"           : @"geoPoint.longitude",
             @"addressId"           : @"address.id",
             @"formattedAddress"    : @"address.formattedAddress",
             @"line1"               : @"address.line1",
             @"line2"               : @"address.line2",
             @"regionName"          : @"address.region.name",
             @"town"                : @"address.town",
             @"postalCode"          : @"address.postalCode",
             @"phone"               : @"address.phone",
             @"countryName"         : @"address.country.name",
             @"openingHours"        : @"openingHours.weekDayOpeningList"
             };
}

//extend for store images

@end
