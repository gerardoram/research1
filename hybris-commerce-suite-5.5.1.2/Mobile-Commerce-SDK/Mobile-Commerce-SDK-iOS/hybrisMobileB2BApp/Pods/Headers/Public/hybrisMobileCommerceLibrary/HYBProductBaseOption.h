//
//  HYBProductBaseOption.h
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

#import "HYBProductOption.h"


@interface HYBProductBaseOption : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSArray *options;
@property (nonatomic) HYBProductOption *selected;
@property (nonatomic) NSString *variantType;

@end
