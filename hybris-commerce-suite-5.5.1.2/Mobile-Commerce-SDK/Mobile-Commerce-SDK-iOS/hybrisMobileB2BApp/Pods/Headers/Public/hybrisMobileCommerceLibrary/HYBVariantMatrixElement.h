//
//  HYBVariantMatrixElement.h
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

@interface HYBVariantMatrixElement : MTLModel <MTLJSONSerializing>

@property (nonatomic) BOOL              isLeaf;
@property (nonatomic) BOOL              parentVariantCategoryHasImage;
@property (nonatomic) NSString          *parentVariantCategoryName;
@property (nonatomic) NSNumber          *parentVariantCategoryPriority;
@property (nonatomic) HYBProductOption  *variantOption;
@property (nonatomic) NSString          *variantValueCategoryName;
@property (nonatomic) NSNumber          *variantValueCategorySequence;
@property (nonatomic) NSArray           *elements;


@end
