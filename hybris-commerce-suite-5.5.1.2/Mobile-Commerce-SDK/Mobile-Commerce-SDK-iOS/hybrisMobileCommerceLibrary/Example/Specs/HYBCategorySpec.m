//
// HYBCategorySpec.m
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

#define EXP_SHORTHAND
#import <Expecta/Expecta.h>
#import <Specta/Specta.h>
#import "HYBCategory.h"

SpecBegin(HYBCategory)
    describe(@"HYBCategory", ^{
        __block NSDictionary *json;
        __block HYBCategory *category;

        beforeAll(^{
            NSString *filePath = [[NSBundle bundleForClass:[self class]] pathForResource:@"categoriesSampleResponse" ofType:@"json"];
            NSData *data = [NSData dataWithContentsOfFile:filePath];
            json = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
            
            category = [HYBCategory categoryWithParams:json];
            
            expect(category).to.beTruthy();
            expect([category hasSubcategories]).to.beTruthy();
            
        });
       
        it(@"should create a category tree from json", ^{
            NSDictionary *attr = [[json objectForKey:@"subcategories"] firstObject];

            HYBCategory *secondCategory = [HYBCategory categoryWithParams:attr];
            
            expect(secondCategory.name).to.beTruthy();
            expect(secondCategory.id).to.beTruthy();
            expect(secondCategory.url).to.beTruthy();
        });
        
        it(@"should create and recognize the root category", ^{
            expect(category.name).to.equal([json objectForKey:@"name"]);
            expect(category.id).to.equal([json objectForKey:@"id"]);
            expect([category.subcategories count] > 0).to.beTruthy();
            expect([category isRoot]).to.beTruthy();
            expect([category isLeaf]).to.beFalsy();
        });
        
        it(@"should create subcategories", ^{
            HYBCategory *firstSubCat  = [category.subcategories firstObject];
            HYBCategory *lastSubCat   = [category.subcategories lastObject];

            expect(firstSubCat).to.beTruthy();
            expect([firstSubCat isLeaf]).to.beFalsy();
            expect(lastSubCat).to.beTruthy();
            expect([lastSubCat isLeaf]).to.beFalsy();
        });
        
        it(@"should find a category in tree by id", ^{
            HYBCategory *firstSubCat = [category.subcategories firstObject];
            HYBCategory *firstChildSubcat = [firstSubCat.subcategories firstObject];
            expect(firstChildSubcat).to.beTruthy();
            HYBCategory *foundCat = [category findCategoryByIdInsideTree:firstChildSubcat.id];
            expect(foundCat).to.equal(firstChildSubcat);
        });

        it(@"should link any category to its parent", ^{
            HYBCategory *firstSubCat = [category.subcategories firstObject];
            HYBCategory *firstChildSubcat = [firstSubCat.subcategories firstObject];
            expect([firstChildSubcat parentCategory]).to.equal(firstSubCat);
            expect([firstSubCat parentCategory]).to.equal(category);
        });
        
    });
SpecEnd
