//
// HYB2BService.m
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

#import <MyEnvironmentConfig/MYEnvironmentConfig.h>
#import "HYB2BService.h"
#import "NSError+HYErrorUtils.h"
#import "NSString+HYStringUtils.h"
#import "HYBCategory.h"
#import "NSObject+HYBAdditionalMethods.h"
#import "HYBCart.h"
#import "HYBCostCenter.h"
#import "HYBDeliveryMode.h"
#import "HYBOrder.h"
#import "HYBStore.h"
#import "DDLog.h"
#import "HYBConstants.h"



@interface HYB2BService ()

@property(nonatomic) NSDictionary *sharedConfig;


@property(nonatomic) NSString *restUrlPrefixHolder;
@property(nonatomic) NSString *currentStoreIdHolder;
@property(nonatomic) NSString *currentCatalogIdHolder;
@property(nonatomic) NSString *currentCatalogVersionHolder;
@property(nonatomic) NSString *currentUserIdHolder;
@property(nonatomic) NSString *currentUserEmailHolder;
@property(nonatomic) NSString *backEndPortHolder;
@property(nonatomic) NSString *backEndHostHolder;

@end

@implementation HYB2BService

@synthesize pageOffset, pageSize;

- (id)initWithDefaults {
    
    self.userDefaults = [NSUserDefaults standardUserDefaults];
    self.sharedConfig = [[MYEnvironmentConfig sharedConfig] configValues];
    
    pageOffset = 0;
    pageSize = 20;
    
    return self;
}

#pragma mark - Current properties and state variables


- (BOOL)useSSL {
    return YES;
}

//currentStoreId
- (void)setCurrentStoreId:(NSString*)currentStoreId {
    _currentStoreIdHolder = currentStoreId;
    [self cacheObject:_currentStoreIdHolder forKey:CURRENT_STORE_ATTRIBUTE_KEY];
}

- (NSString *)currentStoreId {
    if(!_currentStoreIdHolder) {
        _currentStoreIdHolder = [self storedObjectForKey:CURRENT_STORE_ATTRIBUTE_KEY];
    }
    return _currentStoreIdHolder;
}

//currentCatalogId
- (void)setCurrentCatalogId:(NSString*)currentCatalogId {
    _currentCatalogIdHolder = currentCatalogId;
    [self cacheObject:_currentCatalogIdHolder forKey:CURRENT_CATALOG_ATTRIBUTE_KEY];
}

- (NSString *)currentCatalogId {
    if(!_currentCatalogIdHolder) {
        _currentCatalogIdHolder = [self storedObjectForKey:CURRENT_CATALOG_ATTRIBUTE_KEY];
    }
    return _currentCatalogIdHolder;
}

//currentCatalogVersion
- (void)setCurrentCatalogVersion:(NSString*)currentCatalogVersion {
    _currentCatalogVersionHolder = currentCatalogVersion;
    [self cacheObject:_currentCatalogVersionHolder forKey:CURRENT_CATALOG_VERSION_ATTRIBUTE_KEY];
}

- (NSString *)currentCatalogVersion {
    if(!_currentCatalogVersionHolder) {
        _currentCatalogVersionHolder = [self storedObjectForKey:CURRENT_CATALOG_VERSION_ATTRIBUTE_KEY];
    }
    return _currentCatalogVersionHolder;
}

- (NSString *)rootCategoryId {
    return @"1";
}

//currentUserId
- (void)setCurrentUserId:(NSString *)currentUserId {
    _currentUserIdHolder = currentUserId;
    [self cacheObject:_currentUserIdHolder forKey:LAST_AUTHENTICATED_USER_KEY];
}

- (NSString *)currentUserId {
    if(!_currentUserIdHolder) {
        _currentUserIdHolder = [self storedObjectForKey:LAST_AUTHENTICATED_USER_KEY];
    }
    return _currentUserIdHolder;
}

//currentUserEmail
- (void)setCurrentUserEmail:(NSString *)currentUserEmail {
    _currentUserEmailHolder = currentUserEmail;
    [self cacheObject:_currentUserEmailHolder forKey:LAST_AUTHENTICATED_USER_KEY];
}

- (NSString *)currentUserEmail {
    if(!_currentUserEmailHolder) {
        _currentUserEmailHolder = [self storedObjectForKey:LAST_AUTHENTICATED_USER_KEY];
    }
    return _currentUserEmailHolder;
}

- (BOOL)isUsingCache {
    return [((NSNumber *) [self storedObjectForKey:USE_CACHE_ATTRIBUTE_KEY]) boolValue];
}

//restUrlPrefix
- (void)setRestUrlPrefix:(NSString *)restUrlPrefix {
    _restUrlPrefixHolder = restUrlPrefix;
    [self cacheObject:_restUrlPrefixHolder forKey:REST_URL_ATTRIBUTE_KEY];
}

- (NSString *)restUrlPrefix {
    if(!_restUrlPrefixHolder) {
        _restUrlPrefixHolder = [self storedObjectForKey:REST_URL_ATTRIBUTE_KEY];
    }
    return _restUrlPrefixHolder;
}

//backEndPort
- (void)setBackEndPort:(NSString *)port {
    _backEndPortHolder = port;
    [self cacheObject:_backEndPortHolder forKey:PORT_ATTRIBUTE_KEY];
}

- (NSString *)backEndPort {
    if(!_backEndPortHolder) {
        _backEndPortHolder = [self storedObjectForKey:PORT_ATTRIBUTE_KEY];
    }
    return _backEndPortHolder;
}

//backEndPort
- (void)setBackEndHost:(NSString *)host {
    _backEndHostHolder = host;
    [self cacheObject:_backEndHostHolder forKey:HOST_ATTRIBUTE_KEY];
}

- (NSString *)backEndHost {
    if(!_backEndHostHolder) {
        _backEndHostHolder = [self storedObjectForKey:HOST_ATTRIBUTE_KEY];
    }
    return _backEndHostHolder;
}

- (NSString *)baseStoreUrl {
    NSString *protocol = @"http";
    if ([self useSSL]) protocol = @"https";
    
    NSString *host = [self backEndHost];
    NSString *port = [self backEndPort];
    
    NSString *result = nil;
    
    if([port hyb_isNotBlank]){
        result = [[NSString alloc] initWithFormat:@"%@://%@:%@", protocol, host, port];
    } else {
        result = [[NSString alloc] initWithFormat:@"%@://%@", protocol, host];
    }
    
    _baseStoreUrl = result;
    
    return result;
}

- (id)storedObjectForKey:(NSString*)key {
    
    //bypass config file if needed
    id object = [self cacheObjectForKey:key];
    
    //fallback, load from config file
    if(!object) object = [self.sharedConfig objectForKey:key];
    
    //clean up nil placeholder;
    if ([object isEqualToString:@"nil"]) {
        object = nil;
    }
    
    return object;
}

- (void)cacheObject:(id)object forKey:(NSString*)key {
    if (!object) {
        object = @"nil";
    }
    
    [self.userDefaults setObject:object forKey:key];
    [self.userDefaults synchronize];
}

- (id)cacheObjectForKey:(NSString*)key {
    return [self.userDefaults objectForKey:key];
}

#pragma mark - Pagination Utilities

- (void)resetPagination {
    pageOffset = 0;
}

- (void)nextPage {
    pageOffset++;
}

#pragma mark - WS Engine Creation


- (AFHTTPRequestOperationManager*)createAFRestEngineWithToken:(NSString *)token
                                                         host:(NSString *)host
                                                         port:(NSString *)port {
    
    if (host == nil) {
        @throw([NSException exceptionWithName:@"InitException"
                                       reason:@"the host value was not provided, please provide a url to the back end server"
                                     userInfo:nil]);
    }
    
    if (token == nil) {
        @throw([NSException exceptionWithName:@"InitException"
                                       reason:@"the Authorization token was not provided"
                                     userInfo:nil]);
    }
    
    NSString *baseURL = [self baseStoreUrl];
    
    AFHTTPRequestOperationManager *result = [[AFHTTPRequestOperationManager alloc] initWithBaseURL:[NSURL URLWithString:baseURL]];
    
    [result.requestSerializer setValue:token forHTTPHeaderField:@"Authorization"];
    
    AFSecurityPolicy *policy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
    policy.allowInvalidCertificates = YES;
    result.securityPolicy = policy;
    
    return result;
}

- (void)insertAuthTokenToEngine:(NSString *)authToken {
    NSString *authorizationHeaderValue = [NSString stringWithFormat:@"Bearer %@", authToken];
    DDLogDebug(@"Setting the new token to the authorize header.");
    
    NSString *host = [self backEndHost];
    NSString *port = [self backEndPort];
    
    _restEngine = [self createAFRestEngineWithToken:authorizationHeaderValue host:host port:port];
}

- (AFHTTPRequestOperationManager*)restEngine {
    
    if (!_restEngine) {
        NSString *host = [self backEndHost];
        NSString *port = [self backEndPort];
        
        NSString *authorizationHeaderValue = [NSString stringWithFormat:@"Basic %@", @"bW9iaWxlX2FuZHJvaWQ6c2VjcmV0"];
        
        _restEngine = [self createAFRestEngineWithToken:authorizationHeaderValue host:host port:port];
    }
    
    return _restEngine;
    
    
}



#pragma mark - WS Utility Methods

__strong typedef void(^Block)();

- (Block)processHTTPMethod:HTTPMethod
                       url:(NSString *)url
                    params:(NSDictionary *)params
                   headers:(NSDictionary *)headers
                   success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
                   failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure {
    
    Block block = ^() {};
    
    if ([HTTPMethod isEqualToString:@"GET"]) {
        block = ^() {
            [_restEngine GET:url
                  parameters:params
                     success:success
                     failure:failure];
        };
    }
    
    else if ([HTTPMethod isEqualToString:@"POST"]) {
        block = ^() {
            [_restEngine POST:url
                   parameters:params
                      success:success
                      failure:failure];
        };
    }
    
    else if ([HTTPMethod isEqualToString:@"DELETE"]) {
        block = ^() {
            [_restEngine DELETE:url
                     parameters:params
                        success:success
                        failure:failure];
        };
    }
    
    else if ([HTTPMethod isEqualToString:@"PUT"]) {
        block = ^() {
            [_restEngine PUT:url
                  parameters:params
                     success:success
                     failure:failure];
        };
    }
    
    return block;
}

- (void)callHTTPMethod:HTTPMethod
                   url:(NSString *)url
                params:(NSDictionary *)params
               headers:(NSDictionary *)headers
               success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
               failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure {
    
    //log
    [self logURL:url HTTPMethod:HTTPMethod andParams:params];
    
    //create rest engine if needed
    if (!_restEngine) {
        [self restEngine];
    }
    
    //set expected response to JSON
    [_restEngine setResponseSerializer:[AFJSONResponseSerializer serializer]];
    
    //backup original call
    __block void (^originalCall)(void) = [self processHTTPMethod:HTTPMethod
                                                              url:url
                                                           params:params
                                                          headers:headers
                                                          success:success
                                                          failure:failure];
    
    __weak typeof(self)weakSelf = self;
    
    //fallback
   __block void (^checkToken)() = ^(AFHTTPRequestOperation *operation, NSError *error) {
        
        __strong typeof(weakSelf)strongSelf = weakSelf;
        
        NSArray *errors = [(NSDictionary*)operation.responseObject valueForKeyPath:@"errors"];
        if ([errors hyb_isNotBlank]) {
            NSString *errorMsg = [errors.firstObject objectForKey:@"message"];
            NSString *failureType = [errors.firstObject objectForKey:@"type"];
            if(errorMsg) error = [strongSelf createDefaultErrorWithMessage:errorMsg failureReason:failureType];
        }
        
        DDLogError(@"Error during request to web service: %@", [error localizedDescription]);
        
        //verify if token is expired
        if ([error.localizedFailureReason isEqualToString:@"InvalidTokenError"]) {
            DDLogWarn(@"Invalid Token detected, we will try to refresh the token for the current user %@", [self currentUserId]);
            
            NSDictionary *presentTokenDetails = [strongSelf.userDefaults dictionaryForKey:strongSelf.currentUserId];
            
            if ([presentTokenDetails hyb_isNotBlank]) {
            
                __weak typeof(strongSelf)subWeakSelf = strongSelf;
                
                [strongSelf refreshTokenForUser:strongSelf.currentUserId
                      presentTokenDetails:presentTokenDetails
                                    block:^(NSString *refreshedToken, NSError *error) {
                                        
                                        __strong typeof(subWeakSelf)subStrongSelf = subWeakSelf;
                                        
                                        if (error) {
                                            //could not refresh token
                                            [subStrongSelf logoutCurrentUser];
                                        } else {
                                            [subStrongSelf insertAuthTokenToEngine:refreshedToken];
                                            originalCall();
                                        }
                                    }];
            }
        } else {
            failure(operation,error);
        }
    };
    
    //prepare try (see that the failure block is different from the original call)
    void (^tryCall)(void) =  [self processHTTPMethod:HTTPMethod
                                                 url:url
                                              params:params
                                             headers:headers
                                             success:success
                                             failure:checkToken];
    
    //do try
    tryCall();
}


#pragma mark - Stores Functionality

- (void)findStoreBySearchQuery:(NSString*)queryString andExecute:(void (^)(NSArray *, NSError *))executeWith {
    
    if([queryString hyb_isNotBlank]) {
        NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                                queryString, @"query",
                                nil];
        
        [self getStoresWithParams:params andExecute:executeWith];
    } else {
        executeWith(nil, nil);
    }
}

- (void)getStoresWithParams:(NSDictionary*)params andExecute:(void (^)(NSArray *, NSError *))executeWith {
    
    NSString *url = [self storesUrlForStore:[self currentStoreId]];
    
    NSMutableDictionary *tmpDict = nil;
    
    if(params) tmpDict = [NSMutableDictionary dictionaryWithDictionary:params];
    else tmpDict = [NSMutableDictionary dictionary];
    
    [tmpDict setObject:[NSString stringWithFormat:@"%d", pageSize] forKey:@"pageSize"];
    [tmpDict setObject:[NSString stringWithFormat:@"%d", pageOffset] forKey:@"currentPage"];
    [tmpDict setObject:@"FULL" forKey:@"fields"];
    
    NSDictionary *finalParams = [NSDictionary dictionaryWithDictionary:tmpDict];
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:finalParams
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     NSDictionary *values = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     
                     if ([values hyb_isNotBlank] && [values objectForKey:@"stores"]) {
                         NSArray *stores = [values objectForKey:@"stores"];
                         if ([stores hyb_isNotBlank]) {
                             NSMutableArray *result = [NSMutableArray array];
                             for (NSDictionary *storeValues in stores) {
                                 HYBStore *store = [HYBStore storeWithParams:storeValues];
                                 
                                 [result addObject:store];
                             }
                             executeWith([NSArray arrayWithArray:result], nil);
                         } else {
                             DDLogError(@"There seem to be no stores for this search");
                             executeWith([NSArray array], nil);
                         }
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

- (void)getStoreDetailWithStoreName:(NSString*)rawStoreName andParams:(NSDictionary*)params andExecute:(void (^)(HYBStore *, NSError *))executeWith {
    
    NSString *storeName = [rawStoreName URLEncode];
    
    if(storeName) {
        NSString *url = [self storeDetailsUrlForStore:[self currentStoreId] andStoreName:storeName];
        
        NSMutableDictionary *tmpDict = nil;
        
        if(params) tmpDict = [NSMutableDictionary dictionaryWithDictionary:params];
        else tmpDict = [NSMutableDictionary dictionary];
        
        [tmpDict setObject:@"FULL" forKey:@"fields"];
        
        NSDictionary *finalParams = [NSDictionary dictionaryWithDictionary:tmpDict];
        
        [self callHTTPMethod:@"GET"
                         url:url
                      params:finalParams
                     headers:nil
                     success:^(AFHTTPRequestOperation *task, id responseObject) {
                         NSDictionary *values = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                         if ([values hyb_isNotBlank]) {
                             HYBStore *store = [HYBStore storeWithParams:values];
                             executeWith(store, nil);
                         } else {
                             DDLogError(@"There seem to be no details for this storeId %@ and store name %@" , [self currentStoreId], storeName);
                             executeWith(nil, nil);
                         }
                     }
         
                     failure:^(AFHTTPRequestOperation *task, NSError *error) {
                         executeWith(nil, error);
                     }];
        
    } else {
        DDLogError(@"No store name defined");
        executeWith(nil, nil);
    }
}

#pragma mark - Products Functionality

- (void)findProductsWithBlock:(void (^)(NSArray *, NSError *))executeWith {
    NSDictionary *params = @{@"pageSize" : [NSString stringWithFormat:@"%d", pageSize],
                             @"currentPage" : [NSString stringWithFormat:@"%d", pageOffset]};
    
    NSString *url = [self productsUrlForStore:[self currentStoreId]];
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     NSMutableArray *productsContainer = [self productsFromJSONResponse:responseObject jsonTreePath:@"products"];
                     executeWith([NSArray arrayWithArray:productsContainer], nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
}

- (void)findProductsBySearchQuery:(NSString *)query andExecute:(void (^)(NSArray *foundProducts, NSString *spellingSuggestion, NSError *error))toExecute {
    if (![query hyb_isNotBlank]) {
        query = @"";
    }
    
    NSDictionary *params = @{@"pageSize" : [NSString stringWithFormat:@"%d", pageSize],
                             @"currentPage" : [NSString stringWithFormat:@"%d", pageOffset],
                             @"query" : query};
    
    [self findProductsByParams:params andExecute:toExecute];
}

- (void)findProductsByParams:(NSDictionary *)params andExecute:(void (^)(NSArray *, NSString *spellingSuggestion, NSError *error))executeWith {
    NSString *url = [self productsUrlForStore:[self currentStoreId]];
    
     __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSMutableArray *productsContainer = [self productsFromJSONResponse:responseObject jsonTreePath:@"products"];
                     
                     NSDictionary *jsonReponse = [[NSDictionary alloc] initWithDictionary:(NSDictionary*)responseObject];
                     NSString *spellingSuggestion = [jsonReponse valueForKeyPath:@"spellingSuggestion.suggestion"];
                     NSNumber *totalResults = [jsonReponse valueForKeyPath:@"pagination.totalResults"];
                     strongSelf.totalSearchResults = totalResults.intValue;
                     strongSelf.currentPage = [[jsonReponse valueForKeyPath:@"pagination.currentPage"] intValue];
                     strongSelf.pageSize = [[jsonReponse valueForKeyPath:@"pagination.pageSize"] intValue];
                     executeWith([NSArray arrayWithArray:productsContainer], spellingSuggestion, nil);
                 }
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, nil, error);
                 }];
}

- (void)findProductsByCategoryId:(NSString *)categoryId withBlock:(void (^)(NSArray *foundCategories, NSError *error))executeWith {
    NSAssert(categoryId != nil, @"Category must be present");
    
    NSDictionary *params = @{@"pageSize" : [NSString stringWithFormat:@"%d", pageSize], @"currentPage" : [NSString stringWithFormat:@"%d", pageOffset]};
    
    NSString *url = [self productsUrlForStore:[self currentStoreId] categoryId:categoryId];
    
    __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSMutableArray *productsContainer = [strongSelf productsFromJSONResponse:responseObject jsonTreePath:@"products"];
                     executeWith([NSArray arrayWithArray:productsContainer], nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
}

- (NSMutableArray *)productsFromJSONResponse:(id)JSON jsonTreePath:(NSString *)jsonPath {
    NSArray *productsJSON = [JSON valueForKeyPath:jsonPath];
    
    NSMutableArray *productsContainer = [NSMutableArray arrayWithCapacity:[productsJSON count]];
    
    for (NSDictionary *prodAttributes in productsJSON) {
        NSMutableDictionary *allAttributes = [[NSMutableDictionary alloc] initWithDictionary:prodAttributes];
        HYBProduct *newProduct = [HYBProduct productWithParams:allAttributes];
        newProduct.baseStoreURL = self.baseStoreUrl;
        [productsContainer addObject:newProduct];
    }
    
    DDLogDebug(@"Product data retrieved, with %lu new entries", (unsigned long) productsContainer.count);
    return productsContainer;
}

- (void)findProductById:(NSString *)productId withBlock:(void (^)(HYBProduct *, NSError *))executeWith {
    NSDictionary *params = @{@"fields" : @"FULL"};
    NSString *url = [self productDetailsURLForProduct:productId insideStore:[self currentStoreId]];
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     NSMutableDictionary *allAttribs = [[NSMutableDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     executeWith([HYBProduct productWithParams:allAttribs], nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
}



#pragma mark - Authentication and Authorization

- (void)autoLoginUser:(NSString *)username block:(void (^)(NSString *, NSError *))block {
    [self authenticateUser:username password:nil block:block];
}

- (void)authenticateUser:(NSString *const)user password:(NSString *)pass block:(void (^)(NSString *, NSError *))block {
    
    __weak typeof(self)weakSelf = self;
    
    [self retrieveToken:user
               password:pass
                  block:^(NSString *token, NSError *error) {
                      
                      __strong typeof(weakSelf)strongSelf = weakSelf;
                      
                      if (error) {
                          DDLogError(@"Problems during the auth token retrieval, reason: %@", [error localizedDescription]);
                          block(NSLocalizedString(@"login_failed_wrong_credentials", nil), error);
                          
                      } else {
                          NSString *authToken = token;
                          DDLogDebug(@"Token retrieved.");
                          DDLogDebug(@"Auth success");
                          
                          __weak typeof(strongSelf)subWeakSelf = strongSelf;
                          
                          [strongSelf fetchCustomerGroupsFor:user token:authToken block:^(NSArray *customerGroups, NSError *error) {
                              
                              __strong typeof(subWeakSelf)subStrongSelf = subWeakSelf;
                              
                              NSAssert([customerGroups hyb_isNotBlank], @"Customer groups must be present or at least not nil.");
                              
                              if (error) {
                                  DDLogError(@"Problems during the retrieval of user roles: %@", [error localizedDescription]);
                                  block(NSLocalizedString(@"login_error_retrieving_the_user_roles", @""), error);
                                  
                              } else if ([customerGroups containsObject:BUYERGROUP]) {
                                  subStrongSelf.currentUserId    = user;
                                  subStrongSelf.currentUserEmail = user;
                                  
                                  [subStrongSelf insertAuthTokenToEngine:authToken];
                                  
                                  //save username
                                  [[self userStorage] setObject:user forKey:PREVIOUSLY_AUTHENTICATED_USER_KEY];
                                  
                                  block(NSLocalizedString(@"login_success", nil), nil);
                              } else {
                                  NSString *msg = NSLocalizedString(@"login_wrong_user_role", nil);
                                  block(msg, [subStrongSelf createDefaultErrorWithMessage:msg failureReason:nil]);
                              }
                          }];
                      }
                  }];
}


- (void)fetchCustomerGroupsFor:(NSString *)user token:(NSString *)token block:(void (^)(NSArray *, NSError *))block {
    NSArray *userGroups;
    DDLogDebug(@"Start role recognition for user: %@", user);
    
    userGroups = @[@"customergroup", BUYERGROUP];
    DDLogDebug(@"Authentication successfull, user groups are: %@", userGroups);
    block(userGroups, nil);
}

- (void)retrieveToken:(NSString *)user password:(NSString *)pass block:(void (^)(NSString *, NSError *))executeWith {
    NSString *url = @"rest/oauth/token";
    
    NSDictionary *presentTokenDetails = [self.userDefaults dictionaryForKey:user];
    
    if ([presentTokenDetails hyb_isNotBlank] && [presentTokenDetails objectForKey:HYB2B_ACCESS_TOKEN_KEY]) {
        [self refreshTokenForUser:user presentTokenDetails:presentTokenDetails block:executeWith];
    } else {
        __block NSString *token = nil;
        DDLogDebug(@"Retrieving a first time token for the user %@ .", user);
        NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                                user,@"username",
                                pass, @"password",
                                @"mobile_android", @"client_id",
                                @"secret", @"client_secret",
                                @"password",@"grant_type",
                                nil];
        
         __weak typeof(self)weakSelf = self;
        
        [self callHTTPMethod:@"POST"
                         url:url
                      params:params
                     headers:nil
                     success:^(AFHTTPRequestOperation *task, id responseObject) {
                         
                         __strong typeof(weakSelf)strongSelf = weakSelf;
                         
                         NSMutableDictionary *resposeValues = [strongSelf saveToStorageByUserId:user JSON:responseObject];
                         token = [resposeValues objectForKey:HYB2B_ACCESS_TOKEN_KEY];
                         executeWith(token, nil);
                     }
         
                     failure:^(AFHTTPRequestOperation *task, NSError *error) {
                         
                         DDLogError(@"Error during retrieval of the token: %@", error);
                         executeWith(NSLocalizedString(@"login_failed_checkcredentials_or_user_rights", nil), error);
                     }];
    }
}

- (void)refreshTokenForUser:(NSString *)user presentTokenDetails:(NSDictionary *)presentTokenDetails block:(void (^)(NSString *, NSError *))executeWith {
    NSString *url = @"rest/oauth/token";
    DDLogDebug(@"Token for the user %@ found.", user);
    NSString *presentToken = [presentTokenDetails objectForKey:HYB2B_ACCESS_TOKEN_KEY];
    __block NSString *token = nil;
    if ([self isExpiredToken:presentTokenDetails]) {
        DDLogDebug(@"Token for the user %@ is expired and will be refreshed.", user);
        NSString *refreshToken = [presentTokenDetails objectForKey:HYB2B_REFRESH_TOKEN_KEY];
        
        NSDictionary *params = @{@"grant_type" : @"refresh_token",
                                 @"client_id" : @"mobile_android",
                                 @"client_secret" : @"secret",
                                 @"refresh_token" : refreshToken};
        
        __weak typeof(self)weakSelf = self;
        
        [self callHTTPMethod:@"POST"
                         url:url
                      params:params
                     headers:nil
                     success:^(AFHTTPRequestOperation *operation, id responseObject) {
                         
                         __strong typeof(weakSelf)strongSelf = weakSelf;
                         
                         DDLogError(@"Token refreshed from server");
                         NSMutableDictionary *resposeValues = [strongSelf saveToStorageByUserId:user JSON:responseObject];
                         token = [resposeValues objectForKey:HYB2B_ACCESS_TOKEN_KEY];
                         executeWith(token, nil);
                     }
         
                     failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                         DDLogError(@"Error during refreshing the token, this is either a web service "
                                    "issue or a connectivity problem.: %@", error);
                         executeWith(NSLocalizedString(@"login_failed_checkcredentials_or_user_rights", nil), error);
                     }];
        
    } else {
        DDLogDebug(@"Token for the user %@ is STILL VALID and will be reused.", user);
        token = presentToken;
        executeWith(token, nil);
    }
}

- (NSMutableDictionary *)saveToStorageByUserId:(NSString *)userId JSON:(id)JSON {
    
    NSDictionary *tempDictionary = [[NSDictionary alloc] initWithDictionary:(NSDictionary *) JSON];
    NSMutableDictionary *resposeValues = [[NSMutableDictionary alloc] initWithDictionary:tempDictionary];
    
    NSNumber *millisecondsToExpire = [resposeValues objectForKey:HYB2B_EXPIRE_VALUE_KEY];
    double secondsToExpire = millisecondsToExpire.doubleValue / 1000;
    NSDate *expirationTime = [[NSDate alloc] initWithTimeIntervalSinceNow:secondsToExpire];
    resposeValues[HYB2B_EXPIRATION_TIME_KEY] = expirationTime;
    
    [self cacheObject:resposeValues forKey:userId];
    
    return resposeValues;
}

- (BOOL)isExpiredToken:(NSDictionary *)tokenData {
    NSDate *expirationTime = [tokenData objectForKey:HYB2B_EXPIRATION_TIME_KEY];
    NSTimeInterval remainingMillisecondsToExpiration = [expirationTime timeIntervalSinceNow];
    BOOL isSomeRemainingMills = remainingMillisecondsToExpiration > 1;
    return !isSomeRemainingMills;
}

- (void)logoutCurrentUser {
    
    _restEngine = nil;
    
    NSString *currentUserId = [self.userDefaults objectForKey:LAST_AUTHENTICATED_USER_KEY];
    
    if (currentUserId) {
        [self.userDefaults removeObjectForKey:currentUserId];
        [self clearTokensForUser:currentUserId];
    }
    
    NSHTTPCookieStorage *storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    
    for (NSHTTPCookie *cookie in [storage cookies]) {
        DDLogDebug(@"Deleting present cookie %@", [cookie description]);
        [storage deleteCookie:cookie];
    }
    
    [self.userDefaults synchronize];
}

- (void)clearTokensForUser:(NSString *)user {
    DDLogDebug(@"clearTokens");
    
    [self.userDefaults removeObjectForKey:user];
    
    [self.userDefaults removeObjectForKey:LAST_AUTHENTICATED_USER_KEY];
    [self.userDefaults removeObjectForKey:CURRENT_CART_KEY];
    [self.userDefaults removeObjectForKey:CURRENT_COST_CENTERS_KEY];
    
    [self.userDefaults synchronize];
}

- (NSUserDefaults *)userStorage {
    return self.userDefaults;
}

#pragma mark - Loading Images

- (void)loadImageByUrl:(NSString *)url block:(void (^)(UIImage *, NSError *))block {
    
    //TODO: add caching
    
    //DDLogDebug(@"Loading full image for url %@", url);
    
    if ([url hyb_isNotBlank]) {
        
        //set expected response to be
        [_restEngine setResponseSerializer:[AFImageResponseSerializer serializer]];
        
        NSString *fullURLstring = [_baseStoreUrl stringByAppendingString:url];
        
        //add base store url
        [_restEngine GET:fullURLstring
              parameters:nil
                 success:^(AFHTTPRequestOperation *operation, id responseObject) {
                     block((UIImage *)responseObject, nil);
                 }
         
                 failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                     if ([error hyb_isNotBlank] && [error isConnectionOfflineError]) {
                         DDLogWarn(@"image retrieval failed, since device is disconnected, images will be taken from the cache: %@",
                                   [error localizedDescription]);
                     } else {
                         DDLogError(@"image retrieval failed, a dummy will be created, reason: %@", [error localizedDescription]);
                         UIImage *dummy = [UIImage imageNamed:@"placeholder.png"];
                         block(dummy, error);
                     }
                 }];
        
    } else {
        NSString *msg = @"image retrieval failed, given url is blank, a dummy will be created.";
        UIImage *generatedImage = [UIImage imageNamed:@"placeholder.png"];
        block(generatedImage, [self createDefaultErrorWithMessage:msg failureReason:nil]);
    }
    
}

- (void)loadImagesForProduct:(HYBProduct *)product block:(void (^)(NSMutableArray *images, NSError *error))block {
    
    NSAssert([product hyb_isNotBlank], @"Product must be not nil");
    
    NSMutableArray *galleryImages = [NSMutableArray arrayWithArray:[product galleryImagesData]];
    DDLogDebug(@"Loading %lu gallery images for product %@", (unsigned long) galleryImages.count, product.code);
    __block NSMutableArray *images = [NSMutableArray array];
    
    __block int expectedImageCount = (int)[galleryImages count];
    
    for (id obj in galleryImages) {
        NSDictionary *data = (NSDictionary *) obj;
        
        [self loadImageByUrl:[data objectForKey:@"url"] block:^(UIImage *image, NSError *error) {
            
            if (!image) {
                expectedImageCount--;
            } else {
                
                NSLog(@"Adding loaded image %@", image);
                @synchronized(images) {
                    [images addObject:image];
                    
                    if ((int)[images count] == expectedImageCount) {
                        DDLogDebug(@"Image download ready with %lu results.", (unsigned long) [images count]);
                        block([NSMutableArray arrayWithArray:images], nil);
                    }
                }
                
            }
        }];
    }
}

#pragma mark - Categories Functionality

- (void)findCategoriesWithBlock:(void (^)(NSArray *, NSError *))executeWith {
    
    NSString *url = [self categoriesUrlWithStore:[self currentStoreId]
                                       catalogId:[self currentCatalogId]
                                catalogVersionId:[self currentCatalogVersion]
                                  rootCategoryId:[self rootCategoryId]];
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:nil
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     NSDictionary *categoriesJSON = (NSDictionary *)responseObject;
                     
                     NSAssert([categoriesJSON hyb_isNotBlank], @"categories json must be valid after rest service response.");
                     
                     NSMutableArray *categoriesContainer = [NSMutableArray array];
                     
                     HYBCategory *categoryTree = [HYBCategory categoryWithParams:categoriesJSON];
                     [categoriesContainer addObject:categoryTree];
                     
                     DDLogDebug(@"Categories data retrieved, with %lu new entries", (unsigned long) categoriesContainer.count);
                     
                     executeWith([NSArray arrayWithArray:categoriesContainer], nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

#pragma mark - Cart Functionality

- (void)addProductToCurrentCart:(NSString *)productCode amount:(NSNumber *)amount block:(void (^)(HYBCart *cart, NSString *msg))executeWith {
    
    NSAssert([productCode hyb_isNotBlank], @"Product code is empty");
    NSAssert(amount.intValue > 0, @"Given amount of products to add is invalid");
    
    NSString *userId = [self currentUserId];
    
    HYBCart *cart = [self currentCartFromCache];
    
    NSString *url = [self addToCartUrlForCurrentUser:cart.code];
    NSDictionary *params = @{@"product" : productCode, @"quantity" : [NSString stringWithFormat:@"%d", amount.intValue]};
    
    __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"POST"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *operation, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSString *localizedMsg = [strongSelf localizedMsgFromResponse:responseObject];
                     [strongSelf retrieveCartByUserIdFromCurrentCartsCreateIfNothingPresent:userId
                                                                           andExecute:^(HYBCart *cart, NSError *error) {
                                                                               
                                                                               executeWith(cart, localizedMsg);
                                                                               
                                                                           }];
                     
                 }
     
                 failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                     
                     NSString *localizedMsg = NSLocalizedString(@"Product %@ was not added to the cart. Reason: '%@'",nil);
                     DDLogError(@"Problems during adding items to the cart %@ for user %@ reason is %@", cart.code, userId, error.localizedDescription);
                     NSString *msg = [NSString stringWithFormat:localizedMsg, productCode, [error localizedDescription]];
                     
                     executeWith(nil, msg);
                     
                 }];
    
}

- (void)updateProductOnCurrentCartAmount:(NSString *)entryNumber mount:(NSNumber *)amountToAdd andExecute:(void (^)(HYBCart *, NSString *))toExecute {
    HYBCart *cart = [self currentCartFromCache];
    NSString *url = [self updateCartUrlForCurrentUser:cart.code entryNumber:entryNumber];
    
     __weak typeof(self)weakSelf = self;
    
    [self updateCartWithUrl:url updatedParams:@{@"quantity" : amountToAdd.stringValue} andExecute:^(HYBCart *cart, NSError *error) {
        
        __strong typeof(weakSelf)strongSelf = weakSelf;
        
        if (error) {
            toExecute(nil, error.description);
        } else {
            [strongSelf retrieveCurrentCartAndExecute:^(HYBCart *refreshedCart, NSError *error) {
                toExecute(refreshedCart, nil);
            }];
        }
    }];
}


- (NSString *)localizedMsgFromResponse:(id)JSON {
    
    NSDictionary *responseValues = [[NSDictionary alloc] initWithDictionary:(NSDictionary *) JSON];
    
    NSNumber *quantityAdded = [responseValues objectForKey:@"quantityAdded"];
    NSString *statusMsg     = [responseValues objectForKey:@"statusMessage"];
    NSString *statusCode    = [responseValues objectForKey:@"statusCode"];
    
    NSString *localizedMsg;
    if ([statusCode isEqualToString:@"success"]) {
        if ([statusMsg hyb_isNotBlank]) {
            localizedMsg = statusMsg;
        } else {
            localizedMsg = [NSString stringWithFormat:@"%d item(s) added", [quantityAdded intValue]];
        }
    } else {
        if (statusMsg) {
            localizedMsg = [NSString stringWithFormat:@"%@ %d item(s) added", statusMsg, [quantityAdded intValue]];
        } else {
            DDLogError(@"Problems while retrieving the message for the add to cart action: The status message is missing.");
            localizedMsg = [NSString stringWithFormat:@"Problems while adding products to cart. Error was reported."];
        }
    }
    return localizedMsg;
}

- (void)retrieveCartByUserId:(NSString *)userId withBlock:(void (^)(HYBCart *, NSError *))executeWith {

    
    NSString *url = [self currenctCartURLForUser:userId insideStore:[self currentStoreId]];
    
    NSDictionary *params = @{@"fields" : @"FULL"};
    
    __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSDictionary *dict = [[NSDictionary alloc] initWithDictionary:(NSDictionary*)responseObject];
                     HYBCart *cart = [HYBCart cartWithParams:dict];
                     
                     if ([cart hyb_isNotBlank]) {
                         [strongSelf saveCartInCacheNotifyObservers:cart];
                         executeWith(cart, nil);
                     } else {
                         DDLogError(@"Problems retrieving cart for user %@", userId);
                         executeWith(nil, [[NSError alloc] initWithCoder:[NSCoder alloc]]);
                     }
                     
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     __weak typeof(strongSelf)subWeakSelf = strongSelf;
                     
                     DDLogDebug(@"User %@ seems to have no cart yet, a new cart will be created.", userId);
                     [strongSelf createCartForUser:userId andExecute:^(HYBCart *cart, NSError *error) {
                         
                          __strong typeof(subWeakSelf)subStrongSelf = subWeakSelf;
                         
                         if (error) {
                             DDLogError(@"Problems while the creation of a new cart for the user %@, reason is %@", userId, error.description);
                             executeWith(nil, error);
                         } else {
                             [subStrongSelf saveCartInCacheNotifyObservers:cart];
                             executeWith(cart, nil);
                         }
                     }];
                     
                 }];
    
}

- (void)retrieveCartByUserIdFromCurrentCartsCreateIfNothingPresent:(NSString *)userId
                                                        andExecute:(void (^)(HYBCart *, NSError *))toExecute {
    
    __weak typeof(self)weakSelf = self;
    
    [self retrieveAllCartsForUser:userId withBlock:^(NSArray *foundCarts, NSError *error) {
        
        __strong typeof(weakSelf)strongSelf = weakSelf;
        
        if (error) {
            toExecute(nil, error);
        } else {
            if ([foundCarts hyb_isNotBlank]) {
                HYBCart *firstCart = foundCarts.firstObject;
                [strongSelf saveCartInCacheNotifyObservers:firstCart];
                toExecute(firstCart, nil);
            } else {
                
                __weak typeof(strongSelf)subWeakSelf = strongSelf;
                
                DDLogDebug(@"User %@ seems to have no cart yet, a new cart will be created.", userId);
                [strongSelf createCartForUser:userId andExecute:^(HYBCart *cart, NSError *error) {
                    
                     __strong typeof(subWeakSelf)subStrongSelf = subWeakSelf;
                    
                    if (error) {
                        DDLogError(@"Problems while the creation of a new cart for the user %@, reason is %@", userId, error.description);
                        toExecute(nil, error);
                    } else {
                        [subStrongSelf saveCartInCacheNotifyObservers:cart];
                        toExecute(cart, nil);
                    }
                }];
            }
        }
    }];
}

- (void)saveCartInCacheNotifyObservers:(HYBCart *)firstCart {
    
    NSData *encodedObject = [NSKeyedArchiver archivedDataWithRootObject:firstCart];
    [self.userDefaults setObject:encodedObject forKey:CURRENT_CART_KEY];
    [self.userDefaults synchronize];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_CART_UPDATED object:self];
}

- (void)retrieveAllCartsForUser:(NSString *)userId withBlock:(void (^)(NSArray *, NSError *))executeWith {
    NSString *url = [self cartsURLForUser:userId insideStore:[self currentStoreId]];
    
    NSDictionary *params = @{@"fields" : @"FULL"};
    
    __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSDictionary *values = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     
                     if ([values hyb_isNotBlank] && [values objectForKey:@"carts"]) {
                         NSArray *carts = [values objectForKey:@"carts"];
                         
                         if ([carts hyb_isNotBlank]) {
                             NSMutableArray *result = [NSMutableArray arrayWithCapacity:carts.count];
                             
                             for (NSDictionary *cartValues in carts) {
                                                                  
                                 HYBCart *cart = [HYBCart cartWithParams:cartValues];
                                 if (cart && [cart.status isEqualToString:CART_OK])
                                     [result addObject:cart];
                                 else {
                                     DDLogError(@"Faulty cart detected for this user %@", userId);
                                     
                                     [strongSelf deleteCartForUser:userId
                                                    byCartId:cart.code
                                                 executeWith:nil];
                                 }
                             }
                             executeWith([NSArray arrayWithArray:result], nil);
                         } else {
                             DDLogError(@"There seem to be no carts for this user %@", userId);
                             executeWith([NSArray array], nil);
                         }
                     } else {
                         DDLogError(@"There seem to be no carts for this user %@", userId);
                         executeWith([NSArray array], nil);
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

- (void)retrieveCurrentCartAndExecute:(void (^)(HYBCart *, NSError *))toExecute {
    [self retrieveCartByUserIdFromCurrentCartsCreateIfNothingPresent:[self currentUserId] andExecute:^(HYBCart *cart, NSError *error) {
        toExecute(cart, error);
    }];
}

- (void)createCartForUser:(NSString *)userId andExecute:(void (^)(HYBCart *, NSError *))executeWith {
    NSString *url = [self cartsURLForUser:userId insideStore:[self currentStoreId]];
    
    NSDictionary *params = @{};
    
    [self callHTTPMethod:@"POST"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *operation, id responseObject) {
                     NSDictionary *cartParams = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     HYBCart *cart = [HYBCart cartWithParams:cartParams];
                     executeWith(cart, nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                     executeWith(nil, error);
                 }];
}

- (void)deleteCartForUser:(NSString *)userId byCartId:(NSString *)cartId executeWith:(void (^)(NSString *, NSError *))executeWith {
    NSString *url = [self cartByIdURLForUser:userId insideStore:self.currentStoreId cartId:cartId];
    
    NSDictionary *params = @{};
    
    [self callHTTPMethod:@"DELETE"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *operation, id responseObject) {
                     executeWith(@"Cart with code %ld was deleted.", nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

- (void)setPaymentType:(NSString *)paymentType onCartWithCode:(NSString *)code execute:(void (^)(HYBCart *, NSError *))toExecute {
    NSString *url = [self setPaymenTypeUrlCode:code userId:[self currentUserId] storeId:self.currentStoreId];
    [self updateCartWithUrl:url
              updatedParams:@{@"paymentType" : paymentType, @"fields" : @"FULL"}
                 andExecute:^(HYBCart *cart, NSError *error) {
                     toExecute(cart, error);
                 }];
}

- (void)setDeliveryAddressWithCode:(NSString *)addressId onCartWithCode:(NSString *)cartCode andExecute:(void (^)(HYBCart *, NSError *))toExecute {
    NSString *url = [self setDeliveryAddressUrlCode:cartCode userId:[self currentUserId] storeId:self.currentStoreId];
    [self updateCartWithUrl:url
              updatedParams:@{@"addressId" : addressId, @"fields" : @"FULL"}
                 andExecute:^(HYBCart *cart, NSError *error) {
                     toExecute(cart, error);
                 }];
}

- (void)setCostCenterWithCode:(NSString *)costCenterCode onCartWithCode:(NSString *)cartCode andExecute:(void (^)(HYBCart *, NSError *))toExecute {
    NSString *url = [self setCostCenterUrlForCartCode:cartCode userId:[self currentUserId] storeId:self.currentStoreId];
    [self updateCartWithUrl:url
              updatedParams:@{@"costCenterId" : costCenterCode, @"fields" : @"FULL"}
                 andExecute:^(HYBCart *cart, NSError *error) {
                     toExecute(cart, error);
                 }];
}

- (HYBCart *)currentCartFromCache {
    NSData *encodedObject = [[self userStorage] objectForKey:CURRENT_CART_KEY];
    return [NSKeyedUnarchiver unarchiveObjectWithData:encodedObject];
}

- (void)costCentersForCurrentStoreAndExecute:(void (^)(NSArray *, NSError *))executeWith {
    NSString *url = [self costCentersUrlForStore:[self currentStoreId]];
    
    NSData *encodedObject = [[self userStorage] objectForKey:CURRENT_COST_CENTERS_KEY];
    NSArray *currentCostCentersFromCache =  [NSKeyedUnarchiver unarchiveObjectWithData:encodedObject];
    
    if (currentCostCentersFromCache && currentCostCentersFromCache.count > 0) {
        executeWith(currentCostCentersFromCache, nil);
    } else {
        
        NSDictionary *params = @{};
        
        __weak typeof(self)weakSelf = self;
        
        [self callHTTPMethod:@"GET"
                         url:url
                      params:params
                     headers:nil
                     success:^(AFHTTPRequestOperation *task, id responseObject) {
                         
                         __strong typeof(weakSelf)strongSelf = weakSelf;
                         
                         NSArray *costCenters = [(NSDictionary*)responseObject valueForKeyPath:@"costCenters"];
                         if ([costCenters hyb_isNotBlank]) {
                             NSMutableArray *foundCenters = [NSMutableArray arrayWithCapacity:costCenters.count];
                             for (NSDictionary *centerParams in costCenters) {
                                 HYBCostCenter *center = [HYBCostCenter costCenterWithParams:centerParams];
                                 [foundCenters addObject:center];
                             }
                             
                             //serialize
                             NSData *encodedObject = [NSKeyedArchiver archivedDataWithRootObject:[NSArray arrayWithArray:foundCenters]];
                             [[strongSelf userStorage] setObject:encodedObject forKey:CURRENT_COST_CENTERS_KEY];
                             [[strongSelf userStorage] synchronize];
                             
                             executeWith([NSArray arrayWithArray:foundCenters], nil);
                         } else {
                             executeWith([NSArray array], nil);
                         }
                     }
         
                     failure:^(AFHTTPRequestOperation *task, NSError *error) {
                         executeWith(nil, error);
                     }];
        
    }
}

- (void)getDeliveryModesForCart:(NSString *)cartCode andExecute:(void (^)(NSArray *, NSError *))executeWith {
    NSAssert([cartCode hyb_isNotBlank], @"Cart code was not given.");
    NSString *url = [self getDeliveryModesUrlWithCode:cartCode userId:[self currentUserId] storeId:self.currentStoreId];
    
    NSDictionary *params = @{};
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     NSArray *modes = [(NSDictionary*)responseObject valueForKeyPath:@"deliveryModes"];
                     if ([modes hyb_isNotBlank]) {
                         NSMutableArray *foundDeliveryModes = [NSMutableArray arrayWithCapacity:modes.count];
                         for (NSDictionary *modeParam in modes) {
                             HYBDeliveryMode *m = [HYBDeliveryMode deliveryModeWithParams:modeParam];
                             [foundDeliveryModes addObject:m];
                         }
                         executeWith([NSArray arrayWithArray:foundDeliveryModes], nil);
                     } else {
                         executeWith([NSArray array], nil);
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
}

- (void)setDeliveryModeWithCode:(NSString *)modeCode onCartWithCode:(NSString *)cartCode andExecute:(void (^)(HYBCart *, NSError *))toExecute {
    NSString *url = [self setDeliveryModeUrlForCartCode:cartCode userId:[self currentUserId] storeId:self.currentStoreId];
    [self updateCartWithUrl:url
              updatedParams:@{@"deliveryModeId" : modeCode, @"fields" : @"FULL"}
                 andExecute:^(HYBCart *cart, NSError *error) {
                     toExecute(cart, error);
                 }];
}

- (void)placeOrderWithCart:(HYBCart *)cart andExecute:(void (^)(HYBOrder *, NSError *))executeWith {
    
    NSAssert([cart hyb_isNotBlank], @"Cart code must be present");
    NSString *url = [self placeOrderURLForUser:[self currentUserId] insideStore:[self currentStoreId]];
    DDLogDebug(@"Placing the order for cart %@", cart.code);
    
    NSDictionary *params = @{@"termsChecked" : @"true", @"cartId" : cart.code};
    
    __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"POST"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *operation, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSDictionary *JSON = (NSDictionary*)responseObject;
                     
                     if (JSON) {
                         DDLogDebug(@"ORDER PLACED!");
                         NSDictionary *orderValues = [[NSDictionary alloc] initWithDictionary:JSON];
                         NSString *orderCode = [orderValues objectForKey:@"code"];
                         [strongSelf findOrderByCode:orderCode andExecute:^(HYBOrder *order, NSError *error) {
                             if (error) {
                                 executeWith(nil, error);
                             } else {
                                 executeWith(order, nil);
                             }
                         }];
                     } else {
                         DDLogError(@"ORDER NOT PLACED! probably authentication timeout or not complete");
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

- (void)findOrderByCodeFromAllOrdersOrTakeFirst:(NSString *)code andExecute:(void (^)(HYBOrder *, NSError *))toExecute {
    NSString *userId = [self currentUserId];
    
    __weak typeof(self)weakSelf = self;
    
    [self retrieveAllOrdersForUser:userId andExecute:^(NSArray *foundOrders, NSError *error) {
        
        __strong typeof(weakSelf)strongSelf = weakSelf;
        
        if (error) {
            toExecute(nil, error);
        } else {
            if ([foundOrders hyb_isNotBlank]) {
                __block HYBOrder *foundOrder;
                for (HYBOrder *order in foundOrders) {
                    if ([order.code isEqualToString:code]) {
                        foundOrder = order;
                    }
                }
                if (foundOrder) {
                    toExecute(foundOrder, nil);
                } else {
                    DDLogError(@"Order was not found for code %@, the first order will be taken for now, please clarify why the cart and order do not have the same ids.", code);
                    toExecute(foundOrders.firstObject, nil);
                }
            } else {
                toExecute(nil, [strongSelf createDefaultErrorWithMessage:[NSString stringWithFormat:@"No order was found for the given code %@", code] failureReason:nil]);
            }
        }
    }];
}

- (void)findOrderByCode:(NSString *)code andExecute:(void (^)(HYBOrder *, NSError *))executeWith {
    NSAssert([code hyb_isNotBlank], @"Order code must be given.");
    
    NSString *url = [self orderByCodeForUserUrl:code userId:[self currentUserId] insideStore:[self currentStoreId]];
    
    NSDictionary * params = @{@"fields" : @"FULL"};
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     NSDictionary *values = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     HYBOrder *order = [HYBOrder orderWithParams:values];
                     executeWith(order, nil);
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
}

- (void)retrieveOrdersForUser:(NSString *)userId withParams:(NSDictionary*)params andExecute:(void (^)(NSArray *, NSError *))executeWith {
    
    NSString *url = [self ordersURLForUser:userId insideStore:[self currentStoreId]];
    
    NSMutableDictionary *tmpDict = [NSMutableDictionary dictionaryWithDictionary:params];
    [tmpDict setObject:@"FULL" forKey:@"fields"];
    [tmpDict setObject:[NSString stringWithFormat:@"%d", pageSize] forKey:@"pageSize"];
    [tmpDict setObject:[NSString stringWithFormat:@"%d", pageOffset] forKey:@"currentPage"];
    
    NSDictionary *finalParams = [NSDictionary dictionaryWithDictionary:tmpDict];
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:finalParams
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     
                     NSDictionary *values = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     
                     if ([values hyb_isNotBlank] && [values objectForKey:@"orders"]) {
                         NSArray *orders = [values objectForKey:@"orders"];
                         if ([orders hyb_isNotBlank]) {
                             NSMutableArray *result = [NSMutableArray arrayWithCapacity:orders.count];
                             for (NSDictionary *orderValues in orders) {
                                 HYBOrder *order = [HYBOrder orderWithParams:orderValues];
                                 [result addObject:order];
                             }
                             executeWith([NSArray arrayWithArray:result], nil);
                         } else {
                             DDLogError(@"There seem to be no orders for this user %@", userId);
                             executeWith([NSArray array], nil);
                         }
                     } else {
                         DDLogError(@"There seem to be no orders for this user %@", userId);
                         executeWith([NSArray array], nil);
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
}

- (void)retrieveAllOrdersForUser:(NSString *)userId andExecute:(void (^)(NSArray *, NSError *))executeWith {
    
    NSString *url = [self ordersURLForUser:userId insideStore:[self currentStoreId]];
    
    NSDictionary *params = @{@"fields" : @"FULL"};
    
    [self callHTTPMethod:@"GET"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *task, id responseObject) {
                     
                     NSDictionary *values = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     if ([values hyb_isNotBlank] && [values objectForKey:@"orders"]) {
                         NSArray *orders = [values objectForKey:@"orders"];
                         if ([orders hyb_isNotBlank]) {
                             NSMutableArray *result = [NSMutableArray arrayWithCapacity:orders.count];
                             for (NSDictionary *orderValues in orders) {
                                 HYBOrder *order = [HYBOrder orderWithParams:orderValues];
                                 [result addObject:order];
                             }
                             executeWith([NSArray arrayWithArray:result], nil);
                         } else {
                             DDLogError(@"There seem to be no orders for this user %@", userId);
                             executeWith([NSArray array], nil);
                         }
                     } else {
                         DDLogError(@"There seem to be no orders for this user %@", userId);
                         executeWith([NSArray array], nil);
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *task, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

- (void)updateCartWithUrl:(NSString *)url
            updatedParams:(NSDictionary *)params
               andExecute:(void (^)(HYBCart *, NSError *))executeWith {
    
     __weak typeof(self)weakSelf = self;
    
    [self callHTTPMethod:@"PUT"
                     url:url
                  params:params
                 headers:nil
                 success:^(AFHTTPRequestOperation *operation, id responseObject) {
                     
                     __strong typeof(weakSelf)strongSelf = weakSelf;
                     
                     NSDictionary *params = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)responseObject];
                     if ([params hyb_isNotBlank]) {
                         HYBCart *cart = [HYBCart cartWithParams:params];
                         if ([cart hyb_isNotBlank] && [cart.code hyb_isNotBlank]) {
                             executeWith(cart, nil);
                         } else {
                             executeWith([strongSelf currentCartFromCache], nil);
                         }
                     } else {
                         // response is not really giving the cart back we will retrieve the cart from the cache
                         
                         executeWith([self currentCartFromCache], nil);
                     }
                 }
     
                 failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                     executeWith(nil, error);
                 }];
    
}

#pragma mark - Feedback

- (void)logURL:(NSString*)url HTTPMethod:(NSString*)httpMethod andParams:(NSDictionary *)params {
    
    if([params hyb_isNotBlank]){
        DDLogVerbose(@"%@ to URL %@ and params number: %lu", httpMethod, url, (unsigned long)[params count]);
    } else {
        DDLogVerbose(@"%@ to URL %@", httpMethod, url);
    }
    
}


- (NSError *)createDefaultErrorWithMessage:(NSString *)errorMsg failureReason:(NSString *)failureReason {
    NSError *error;
    
    NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
    
    [userInfo setObject:errorMsg forKey:NSLocalizedDescriptionKey];
    if (failureReason) {
        [userInfo setObject:failureReason forKey:NSLocalizedFailureReasonErrorKey];
    }
    error = [NSError errorWithDomain:HYBOCCErrorDomain code:HYB2B_ERROR_CODE_TECHNICAL userInfo:userInfo];
    
    return error;
}


#pragma mark - URL Creation

- (NSString *)categoriesUrlWithStore:(NSString *)storeId
                           catalogId:(NSObject *)catalogId
                    catalogVersionId:(NSObject *)catalogVersionId
                      rootCategoryId:(NSObject *)rootCategoryId {
    
    // /rest/v1/apparel-uk/catalogs/apparelProductCatalog/Online/categories/categories
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/catalogs/%@/%@/categories/%@", [self restUrlPrefix],
                     storeId, catalogId, catalogVersionId, rootCategoryId];
    return url;
}

- (NSString *)productsUrlForStore:(NSString *)storeId categoryId:(NSString *)categoryId {
    // /rest/v2/powertools/categories/1800/products
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/categories/%@/products", [self restUrlPrefix], storeId, categoryId];
    return url;
}

- (NSString *)productDetailsURLForProduct:(NSString *)productId insideStore:(NSString *)catalogId {
    NSString *url = [NSString stringWithFormat:@"%@/%@/products/%@", [self restUrlPrefix], catalogId, productId];
    return url;
}

- (NSString *)cartsURLForUser:(NSString *)userId insideStore:(NSString *)storeId {
    ///rest/v2/powertools/users/mark.rivers@rustic-hw.com/carts/
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/carts", [self restUrlPrefix], storeId, userId];
    return url;
}

- (NSString *)ordersURLForUser:(NSString *)userId insideStore:(NSString *)storeId {
    ///rest/v2/powertools/users/mark.rivers@rustic-hw.com/orders/
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/orders", [self restUrlPrefix], storeId, userId];
    return url;
}

- (NSString *)currenctCartURLForUser:(NSString *)userId insideStore:(NSString *)storeId {
    ///rest/v2/powertools/users/mark.rivers@rustic-hw.com/carts/
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/carts/current", [self restUrlPrefix], storeId, userId];
    return url;
}

- (NSString *)cartByIdURLForUser:(NSString *)userId insideStore:(NSString *)storeId cartId:(NSString *)cartId {
    ///rest/v2/powertools/users/mark.rivers@rustic-hw.com/carts/
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/carts/%@", [self restUrlPrefix], storeId, userId, cartId];
    return url;
}

- (NSString *)orderByIdURLForUser:(NSString *)userId insideStore:(NSString *)storeId orderId:(NSString *)orderId {
    ///rest/v2/powertools/users/mark.rivers@rustic-hw.com/orders/
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/orders/%@", [self restUrlPrefix], storeId, userId, orderId];
    return url;
}

- (NSString *)costCentersUrlForStore:(NSString *)storeId {
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/costcenters", [self restUrlPrefix], storeId];
    return url;
}

- (NSString *)productsUrlForStore:(NSString *)storeId {
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/products/search", [self restUrlPrefix], storeId];
    return url;
}

- (NSString *)storesUrlForStore:(NSString *)storeId {
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/stores", [self restUrlPrefix], storeId];
    return url;
}

- (NSString *)storeDetailsUrlForStore:(NSString *)storeId andStoreName:(NSString *)storeName {
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/stores/%@", [self restUrlPrefix], storeId, storeName];
    return url;
}

- (NSString *)addToCartUrlForCurrentUser:(NSString *)cartId {
    // /rest/v2/powertools/users/mark.rivers@rustic-hw.com/carts/00004028/entries
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/carts/%@/entries", [self restUrlPrefix], [self currentStoreId], [self currentUserId], cartId];
    return url;
}


- (NSString *)updateCartUrlForCurrentUser:(NSString *)cartId entryNumber:(NSObject *)entryNumber {
    // rest/v2/powertools/users/mark.rivers@rustic-hw.com/carts/00004028/entries/0?qty=5
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/carts/%@/entries/%@", [self restUrlPrefix], [self currentStoreId], [self currentUserId], cartId, entryNumber];
    return url;
}

- (NSString *)setPaymenTypeUrlCode:(NSString *)code userId:(NSString *)userId storeId:(NSString *)storeId {
    NSString *cartUrl = [self cartByIdURLForUser:userId insideStore:storeId cartId:code];
    NSString *url = [NSString stringWithFormat:@"%@/paymenttype/", cartUrl];
    return url;
}

- (NSString *)setDeliveryAddressUrlCode:(NSString *)cartCode userId:(NSString *)userId storeId:(NSString *)storeId {
    NSString *cartUrl = [self cartByIdURLForUser:userId insideStore:storeId cartId:cartCode];
    NSString *url = [NSString stringWithFormat:@"%@/addresses/delivery/", cartUrl];
    return url;
}

- (NSString *)setDeliveryModeUrlForCartCode:(NSString *)cartCode userId:(NSString *)userId storeId:(NSString *)storeId {
    NSString *cartUrl = [self cartByIdURLForUser:userId insideStore:storeId cartId:cartCode];
    NSString *url = [NSString stringWithFormat:@"%@/deliverymode/", cartUrl];
    return url;
}

- (NSString *)setCostCenterUrlForCartCode:(NSString *)cartCode userId:(NSString *)userId storeId:(NSString *)storeId {
    NSString *cartUrl = [self cartByIdURLForUser:userId insideStore:storeId cartId:cartCode];
    NSString *url = [NSString stringWithFormat:@"%@/costcenter/", cartUrl];
    return url;
}

- (NSString *)getDeliveryModesUrlWithCode:(NSString *)cartCode userId:(NSString *)userId storeId:(NSString *)storeId {
    NSString *cartUrl = [self cartByIdURLForUser:userId insideStore:storeId cartId:cartCode];
    NSString *url = [NSString stringWithFormat:@"%@/deliverymodes/", cartUrl];
    return url;
}

- (NSString *)orderByCodeForUserUrl:(NSString *)orderCode userId:(NSString *)id insideStore:(NSString *)store {
    NSString *ordersUrl = [self ordersURLForUser:id insideStore:store];
    NSString *url = [NSString stringWithFormat:@"%@/%@", ordersUrl, orderCode];
    return url;
}

- (NSString *)placeOrderURLForUser:(NSString *)userId insideStore:(NSString *)storeId {
    NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/users/%@/orders", [self restUrlPrefix], storeId, userId];
    return url;
}


@end