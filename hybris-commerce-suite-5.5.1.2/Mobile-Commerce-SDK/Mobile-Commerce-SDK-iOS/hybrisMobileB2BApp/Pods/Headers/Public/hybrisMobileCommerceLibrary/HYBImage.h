//
//  HYBImage.h
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

@interface HYBImage : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString *altText;
@property (nonatomic) NSString *format;
@property (nonatomic) NSString *imageType;
@property (nonatomic) NSString *url;

+ (instancetype)imageWithParams:(NSDictionary*)params;

- (NSDictionary*)asDictionnary;

@end
