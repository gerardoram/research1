//
//  HYBCategory.h
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


@interface HYBCategory : MTLModel <MTLJSONSerializing>

@property (nonatomic) NSString *id;
@property (nonatomic) NSString *code;
@property (nonatomic) NSString *name;
@property (nonatomic) NSString *url;
@property (nonatomic) NSArray *subcategories;

+ (instancetype)categoryWithParams:(NSDictionary*)params;

/**
 *  returns BOOL YES if category has subcategories
 *
 *  @return BOOL hasSubcategories
 */
- (BOOL)hasSubcategories;

/**
 *  returns category's parent category
 *
 *  @return HYBCategory parentCategory
 */
- (HYBCategory *)parentCategory;

/**
 *  check if a category id part of current catgory's children
 *
 *  @param categoryId NSString
 *
 *  @return BOOL category's subcategories contains given category id
 */
- (BOOL)hasChildId:(NSString *)categoryId;

/**
 *  check if category is root
 *
 *  @return BOOL is root category
 */
- (BOOL)isRoot;

/**
 *  check if category is leaf of subcategories tree
 *
 *  @return BOOL is leaf (ie. has no subcategories)
 */
- (BOOL)isLeaf;

/**
 *  returns an array with parent category at index 0 and subcategories following
 *
 *  @return NSArray of categories
 */
- (NSArray *)listItSelfIncludingChildren;

/**
 *  returns a category given its category id
 *
 *  @param categoryId NSString
 *
 *  @return HYBCategory category
 */
- (HYBCategory *)findCategoryByIdInsideTree:(NSString *)categoryId;




@end