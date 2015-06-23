//
// HYBCart.h
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
#import "AFNetworking.h"
#import "HYBProduct.h"
#import "HYBBackEndFacade.h"

#define STORAGE_CURRENTLY_SHOWN_CATEGORY_KEY @"currently_shown_cat"
#define BUYERGROUP                  @"buyergroup"
#define HYB2B_ACCESS_TOKEN_KEY      @"access_token"
#define HYB2B_REFRESH_TOKEN_KEY     @"refresh_token"
#define HYB2B_EXPIRE_VALUE_KEY      @"expires_in"
#define HYB2B_EXPIRATION_TIME_KEY   @"issued_on"
#define HYB2BImageDummy             @"HYB2BImageDummy.png"
#define CURRENT_CART_KEY            @"current_cart"
#define CURRENT_COST_CENTERS_KEY    @"current_cost_centers"
#define HYB2B_ERROR_CODE_TECHNICAL -57

@class HYBCart;

/**
 * The class that implements the HYBBackEndFacade. In this case it is the specific b2b implementation.
 */
@interface HYB2BService : NSObject  <HYBBackEndFacade>

@property(nonatomic) AFHTTPRequestOperationManager *restEngine;

@property(nonatomic) int pageOffset;
@property(nonatomic) int pageSize;
@property(nonatomic) int currentPage;
@property(nonatomic) int totalSearchResults;

@property(nonatomic) NSString *rootCategoryId;
@property(nonatomic) NSString *baseStoreUrl;
@property(nonatomic) NSString *restUrlPrefix;

@property(nonatomic) NSString *currentStoreId;
@property(nonatomic) NSString *currentCatalogId;
@property(nonatomic) NSString *currentCatalogVersion;
@property(nonatomic) NSString *currentUserId;
@property(nonatomic) NSString *currentUserEmail;
@property(nonatomic) NSString *backEndPort;
@property(nonatomic) NSString *backEndHost;

@property(nonatomic) NSUserDefaults *userDefaults;

- (id)initWithDefaults;

- (BOOL)isExpiredToken:(NSDictionary *)userTokenData;

- (NSString *)productDetailsURLForProduct:(NSString *)productId insideStore:(NSString *)catalogId;

- (void)retrieveAllCartsForUser:(NSString *)userId withBlock:(void (^)(NSArray *, NSError *))executeWith;

- (void)findOrderByCode:(NSString *)code andExecute:(void (^)(HYBOrder *, NSError *))execute;

/* for testing only */


- (void)callHTTPMethod:HTTPMethod
                   url:(NSString *)url
                params:(NSDictionary *)params
               headers:(NSDictionary *)headers
               success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
               failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure;


@end