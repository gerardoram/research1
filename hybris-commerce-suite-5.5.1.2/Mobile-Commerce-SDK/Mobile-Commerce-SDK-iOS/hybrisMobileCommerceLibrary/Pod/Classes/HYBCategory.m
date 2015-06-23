//
//  HYBCategory.m
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

#import "HYBCategory.h"
#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"

@interface HYBCategory ()

@property (nonatomic) HYBCategory *parent;

@end

@implementation HYBCategory

+ (instancetype)categoryWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBCategory *category = [MTLJSONAdapter modelOfClass:[HYBCategory class]
                                        fromJSONDictionary:params
                                                     error:&error];
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBCategory models: %@", error);
        return nil;
    }
    
    if (category) [category assignParent];
    
    return category;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"id"              : @"id",
             @"code"            : @"code",
             @"name"            : @"name",
             @"url"             : @"url",
             @"subcategories"   : @"subcategories"
             };
}

+ (NSValueTransformer *)subcategoriesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBCategory class]];
}

- (void)assignParent {
    if(_subcategories) {
        for(HYBCategory *currentCategory in _subcategories) {
            currentCategory.parent = self;
            [currentCategory assignParent];
        }
    }
}

- (NSString *)description {
    NSMutableString *description = [NSMutableString stringWithFormat:@"%@: ", NSStringFromClass([self class])];
    
    if([self isRoot]){
        [description appendString:@",Root: "];
    }
    [description appendString:self.name];
    if ([self hasSubcategories]) {
        NSString *subCats = [[NSString alloc] initWithFormat:@", Subcategories:%lu", (unsigned long)[_subcategories count]];
        [description appendString:subCats];
    }
    return description;
}

- (BOOL)hasSubcategories {
    return [_subcategories count] > 0;
}

- (HYBCategory *)parentCategory {
    return _parent;
}

- (BOOL)isRoot {
    return (self.parentCategory == nil);
}

- (BOOL)isLeaf {
    return ![self hasSubcategories];
}

- (NSArray *)listItSelfIncludingChildren {
    NSMutableArray *categoriesContainer = [[NSMutableArray alloc] initWithObjects:self, nil];
    [categoriesContainer addObjectsFromArray:_subcategories];
    return [NSArray arrayWithArray:categoriesContainer];
}

- (HYBCategory *)findCategoryByIdInsideTree:(NSString *)categoryId {
    HYBCategory *result = nil;
    NSString *ownId = self.id;
    if ([categoryId isEqualToString:ownId]) {
        result = self;
    } else {
        if ([self hasSubcategories]) {
            for (HYBCategory *currentCat in _subcategories) {
                result = [currentCat findCategoryByIdInsideTree:categoryId];
                if (result != nil) {
                    break;
                }
            }
        }
    }
    return result;
}

- (BOOL)hasChildId:(NSString *)categoryId {
    return ([self findCategoryByIdInsideTree:categoryId] != nil);
}

@end


