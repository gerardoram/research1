//
// HYBCostCenter.h
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
#import "MTLModel.h"
#import "MTLJSONAdapter.h"


/**
* The class representing a cost center in commerce terms. Commerce centers are usually
* used for payments in company environments.
*
*/
@interface HYBCostCenter : MTLModel <MTLJSONSerializing>

@property(nonatomic,copy)  NSString  *code;
@property(nonatomic,copy)  NSString  *name;
@property(nonatomic,copy)  NSArray   *addresses;

+ (instancetype)costCenterWithParams:(NSDictionary*)params;

@end