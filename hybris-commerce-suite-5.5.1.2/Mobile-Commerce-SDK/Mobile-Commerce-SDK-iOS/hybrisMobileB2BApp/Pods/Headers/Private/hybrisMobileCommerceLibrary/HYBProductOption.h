//
//  HYBProductOption.h
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


@interface HYBProductOption : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString *code;
@property (nonatomic) HYBPrice *priceData;
@property (nonatomic) HYBStock *stock;
@property (nonatomic) NSString *url;
@property (nonatomic) NSArray *variantOptionQualifiers;

@end
