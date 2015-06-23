//
// HYBServiceSpec.m
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
#import "HYBAppDelegate.h"
#import "HYB2BService.h"
#import "DDASLLogger.h"
#import "DDTTYLogger.h"
#import "HYBCategory.h"
#import "HYBConstants.h"
#import "HYBCart.h"
#import "HYBStore.h"
#import "HYBCostCenter.h"
#import "HYBAddress.h"
#import "HYBDeliveryMode.h"
#import "HYBOrder.h"
#import "NSObject+HYBAdditionalMethods.h"
#import "Specta.h"
#import "HYBProduct.h"
#import <Foundation/Foundation.h>
#import <MyEnvironmentConfig/MYEnvironmentConfig.h>
#import <hybrisMobileCommerceLibrary/MYEnvironmentConfig+HYBAdditionalMethods.h>


NSString *TEST_USER_NAME = @"axel.krause@rustic-hw.com";
NSString *TEST_PASSWORD = @"12341234";

NSString *TEST_PRODUCT_CODE = @"1979039";

NSString *TEST_SEARCH_TERM = @"shoe";
NSString *TEST_SEARCH_RESULT_SUGGESTED_TERM = @"sheet";

NSString *TEST_ORDER_CODE = @"00002098";
NSString *TEST_STORE_NAME = @"Hybris Powertools Tacoma";

int TEST_PAGE_SIZE_NUMBER = 5;
int TEST_EXPECTED_SEARCH_RESULT_NUMBER = 17;

SpecBegin(HYBService)

describe(@"Products Retrieval", ^{
    __block HYB2BService *b2bService;
    __block NSString *buildUserId;

    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];
        
        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];

        buildUserId = [[MYEnvironmentConfig sharedConfig] buildUserId];
        NSLog(@"Build user id: %@", buildUserId);
        
        
    });
    beforeEach(
               ^{
                   b2bService = [[HYB2BService alloc] initWithDefaults];
               }
               );
    it(@"should init with caching", ^{
        BOOL useCache = [b2bService isUsingCache];
        expect(useCache).to.beTruthy();
    });
    it(@"should retrieve the products", ^AsyncBlock {
        [b2bService findProductsWithBlock:^(NSArray *products, NSError *error) {
            expect([products count] > 0).to.beTruthy();
            expect(error).to.beFalsy();
            done();
        }];
    });
    // TODO data dependency present on product containing the search term
    
    it(@"should search for the products by query and show a spelling suggestion", ^AsyncBlock {
        [b2bService findProductsBySearchQuery:TEST_SEARCH_TERM andExecute:^(NSArray *products, NSString *spellingSuggestion, NSError *error) {
            expect([products count] > 0).to.beTruthy();
            expect(spellingSuggestion).to.equal(TEST_SEARCH_RESULT_SUGGESTED_TERM);
            expect(error).to.beFalsy();
            done();
        }];
    });
    // TODO data dependency present on product  and the number of this products in the system
    it(@"should search for the products by query and save the pagination results", ^AsyncBlock {
        b2bService.pageSize = TEST_PAGE_SIZE_NUMBER;
        
        [b2bService findProductsBySearchQuery:TEST_SEARCH_TERM andExecute:^(NSArray *products, NSString *spellingSuggestion, NSError *error) {
            expect([products count]).to.equal(b2bService.pageSize);
            int totalSearchResults = b2bService.totalSearchResults;
            expect(totalSearchResults).to.equal(TEST_EXPECTED_SEARCH_RESULT_NUMBER);
            expect(error).to.beFalsy();
            done();
        }];
    });
    it(@"should retrieve the products in category", ^AsyncBlock {
        [b2bService findCategoriesWithBlock:^(NSArray *foundCategories, NSError *error) {
            expect([foundCategories hyb_isNotBlank]).to.beTruthy();
            expect(error).to.beFalsy();
            
            HYBCategory *rootNode = [foundCategories firstObject];
            HYBCategory *childOfRoot = [[rootNode subcategories] lastObject];
            
            [b2bService findProductsByCategoryId:childOfRoot.id withBlock:^(NSArray *products, NSError *error) {
                expect([products hyb_isNotBlank]).to.beTruthy();
                done();
            }];
        }];
    });
    it(@"should retrieve a product by id", ^AsyncBlock {
        [b2bService findProductsWithBlock:^(NSArray *products, NSError *error) {
            HYBProduct *prod = [products firstObject];
            [b2bService findProductById:prod.code withBlock:^(HYBProduct *foundProduct, NSError *error) {
                expect(foundProduct).to.beTruthy();
                expect(foundProduct.code).to.equal(prod.code);
                done();
            }];
        }];
    });
    it(@"should retrieve necessary product attributes", ^AsyncBlock {
        [b2bService findProductsWithBlock:^(NSArray *products, NSError *error) {
            HYBProduct *prod = [products firstObject];
            [b2bService findProductById:prod.code withBlock:^(HYBProduct *foundProduct, NSError *error) {
                expect(foundProduct).to.beTruthy();
                expect(foundProduct.desc).to.beTruthy();
                expect(foundProduct.summary).to.beTruthy();
                expect(foundProduct.price).to.beTruthy();
                done();
            }];
        }];
    });
    it(@"should create a mock image for an empty given product url", ^AsyncBlock {
        [b2bService loadImageByUrl:nil block:^(UIImage *image, NSError *error) {
            expect(image).to.beTruthy();
            expect(image.size.height).to.beGreaterThan(0);
            expect(error).to.beTruthy();
            done();
        }];
    });
    it(@"should load gallery product images", ^AsyncBlock {
        [b2bService findProductsWithBlock:^(NSArray *products, NSError *error) {
            HYBProduct *prod = [products firstObject];
            [b2bService findProductById:prod.code withBlock:^(HYBProduct *foundProduct, NSError *error) {
                expect(foundProduct).to.beTruthy();
                [b2bService loadImagesForProduct:foundProduct block:^(NSMutableArray *images, NSError *error) {
                    DDLogDebug(@"Images count is %lu", (unsigned long)[images count]);
                    expect(images).to.beTruthy();
                    expect(error == nil);
                    done();
                }];
            }];
        }];
    });
    it(@"should retrieve the products in category", ^AsyncBlock {
        [b2bService findCategoriesWithBlock:^(NSArray *foundCategories, NSError *error) {
            expect([foundCategories hyb_isNotBlank]).to.beTruthy();
            expect(error).to.beFalsy();
            HYBCategory *root = [foundCategories firstObject];
            expect(root).to.beTruthy;
            expect([root isRoot]).to.beTruthy();
            done();
        }];
    });
});


describe(@"Store Locator", ^{
    __block HYB2BService *b2bService;
    __block NSString *buildUserId;
    
    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];
        
        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
        buildUserId = [[MYEnvironmentConfig sharedConfig] buildUserId];
    });
    beforeEach(^{
        b2bService = [[HYB2BService alloc] initWithDefaults];
        b2bService.userDefaults = [[NSUserDefaults alloc] init];
    });
    it(@"should retrieve a list of stores", ^AsyncBlock {
        [b2bService getStoresWithParams:nil andExecute:^(NSArray *stores, NSError *error) {
            for(HYBStore *store in stores) {
                expect(store.name).to.beTruthy();
                expect(store.longitude).to.beTruthy();
                expect(store.latitude).to.beTruthy();
                expect(store.formattedAddress).to.beTruthy();
                done();
            }
        }];
    });
    it(@"should retrieve the details of a store", ^AsyncBlock {
        [b2bService getStoreDetailWithStoreName:TEST_STORE_NAME andParams:nil andExecute:^(HYBStore *store, NSError *error) {
            expect(store.name).to.beTruthy();
            expect(store.longitude).to.beTruthy();
            expect(store.latitude).to.beTruthy();
            expect(store.formattedAddress).to.beTruthy();
            expect(store.phone).to.beTruthy();
            expect(store.openingHours).to.beTruthy();
            done();
        }];
    });
});

describe(@"Order History", ^{
    __block HYB2BService *b2bService;
    __block NSString *buildUserId;
    
    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];
        
        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
        buildUserId = [[MYEnvironmentConfig sharedConfig] buildUserId];
    });
    beforeEach(^{
        b2bService = [[HYB2BService alloc] initWithDefaults];
        b2bService.userDefaults = [[NSUserDefaults alloc] init];
    });
    it(@"should retrieve a list of orders", ^AsyncBlock {
        
        [b2bService authenticateUser:buildUserId password:TEST_PASSWORD block:^(NSString *msg, NSError *error) {
            
            expect(msg).to.equal(NSLocalizedString(@"login_success", nil));
            expect(error).to.beFalsy();
            
            [b2bService retrieveOrdersForUser:buildUserId withParams:[NSDictionary dictionary] andExecute:^(NSArray *orders, NSError *error) {
                for(HYBOrder *order in orders) {
                    expect(order.code).to.beTruthy;
                    expect(order.placed).to.beTruthy;
                    expect(order.statusDisplay).to.beTruthy;
                    expect(order.total).to.beTruthy;
                    expect(order.totalValue).to.beTruthy;
                    expect(order.totalWithTax).to.beTruthy;
                }
            }];
            done();
        }];
    });
    it(@"should retrieve an order by id", ^AsyncBlock {
        
        [b2bService authenticateUser:buildUserId password:TEST_PASSWORD block:^(NSString *msg, NSError *error) {
            
            expect(msg).to.equal(NSLocalizedString(@"login_success", nil));
            expect(error).to.beFalsy();
            
            [b2bService findOrderByCode:TEST_ORDER_CODE andExecute:^(HYBOrder *order, NSError *error) {
                expect(order.code).to.beTruthy;
                expect(order.placed).to.beTruthy;
                expect(order.statusDisplay).to.beTruthy;
                expect(order.total).to.beTruthy;
                expect(order.totalValue).to.beTruthy;
                expect(order.totalWithTax).to.beTruthy;
            }];
            done();
        }];
    });
});

describe(@"Authentication", ^{
    __block NSString *buildUserId;

    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];

        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
        buildUserId = [[MYEnvironmentConfig sharedConfig] buildUserId];
    });
    // TODO data dependency present on specific user
    it(@"should authenticate the user successfully", ^AsyncBlock {
        HYB2BService *b2bService = [[HYB2BService alloc] initWithDefaults];
        
        [b2bService authenticateUser:buildUserId password:TEST_PASSWORD block:^(NSString *msg, NSError *error) {
            expect(msg).to.equal(NSLocalizedString(@"login_success", nil));
            expect(error).to.beFalsy();
            [b2bService logoutCurrentUser];
            done();
        }];
    });
    it(@"should not authenticate the user at failure", ^AsyncBlock {
        HYB2BService *b2bService = [[HYB2BService alloc] initWithDefaults];
        [b2bService logoutCurrentUser];
        
        [b2bService authenticateUser:buildUserId password:@"wrongPasswordForAuth" block:^(NSString *msg, NSError *error) {
            expect(msg).to.equal(NSLocalizedString(@"login_failed_wrong_credentials", nil));
            expect(error).to.beTruthy();
            done();
        }];
    });
    it(@"should obtain the token for user failure", ^AsyncBlock {
        HYB2BService *b2bService = [[HYB2BService alloc] initWithDefaults];
        [b2bService logoutCurrentUser];
        
        [b2bService retrieveToken:buildUserId password:@"wrongPasswordForTokenRetrieval" block:^(NSString *messageOrToken, NSError *error) {
            NSString *expected = NSLocalizedString(@"login_failed_checkcredentials_or_user_rights", nil);
            expect(messageOrToken).to.equal(expected);
            
            done();
        }];
    });
    it(@"should recognize not expired token", ^AsyncBlock {
        HYB2BService *b2bService = [[HYB2BService alloc] initWithDefaults];
        
        NSNumber *millisecondsToExpire = @10000;
        double secondsToExpire = millisecondsToExpire.doubleValue / 1000;
        NSDate *expirationTime = [[NSDate alloc] initWithTimeIntervalSinceNow:secondsToExpire];
        NSDictionary *resposeValues = @{HYB2B_EXPIRATION_TIME_KEY : expirationTime};
        
        BOOL result = [b2bService isExpiredToken:resposeValues];
        expect(result).to.beFalsy();
        [b2bService logoutCurrentUser];
        done();
    });
    // TODO data dependency present on specific user
    it(@"should obtain the token for user success and save it to properties", ^AsyncBlock {
        NSUserDefaults *userDefaults = [[NSUserDefaults alloc] init];
        HYB2BService *b2bService = [[HYB2BService alloc] initWithDefaults];
        b2bService.userDefaults = userDefaults;
        
        NSDate *now = [[NSDate alloc] initWithTimeIntervalSinceNow:0];
        
        [b2bService retrieveToken:buildUserId password:TEST_PASSWORD block:^(NSString *messageOrToken, NSError *error) {
            expect(messageOrToken).to.beTruthy();
            expect(error).to.beFalsy();
            
            NSDictionary *tokenData = [userDefaults objectForKey:buildUserId];
            expect(tokenData).to.beTruthy();
            expect([tokenData objectForKey:HYB2B_EXPIRATION_TIME_KEY]).to.beTruthy();
            
            // reconfigure the expiration time to be much shorter, for the next test
            NSMutableDictionary *newTokenData = [[NSMutableDictionary alloc] initWithDictionary:tokenData];
            [newTokenData setObject:now forKey:HYB2B_EXPIRATION_TIME_KEY];
            [userDefaults setObject:newTokenData forKey:buildUserId];
            
            // now reuse the previously saved token doing a refresh
            [b2bService retrieveToken:buildUserId password:TEST_PASSWORD block:^(NSString *messageOrToken, NSError *error) {
                expect(error).to.beFalsy();
                expect(messageOrToken).to.beTruthy();
                
                NSDictionary *tokenData = [userDefaults objectForKey:buildUserId];
                expect(tokenData).to.beTruthy();
                expect([tokenData objectForKey:HYB2B_EXPIRATION_TIME_KEY] != now).to.beTruthy();
                
                [b2bService logoutCurrentUser];
                
                done();
            }];
        }];
    });
});

describe(@"Cart Management", ^{
    __block HYB2BService *backEndService;
    __block NSString *buildUserId;
    
     NSLog(@"Cart Management");
    
    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];
        
        backEndService = [[HYB2BService alloc] initWithDefaults];
        backEndService.userDefaults = [[NSUserDefaults alloc] init];

        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
        buildUserId = [[MYEnvironmentConfig sharedConfig] buildUserId];
    });
    // TODO data dependency present, expected are cost centers, payment types and address setup
    it(@"full checkout workflow", ^AsyncBlock {
        
        NSLog(@"authenticateUser");
        
        [backEndService authenticateUser:buildUserId password:TEST_PASSWORD block:^(NSString *msg, NSError *error) {
            
            expect(msg).to.equal(NSLocalizedString(@"login_success", nil));
            expect(error).to.beFalsy();
            
             NSLog(@"retrieveCurrentCartAndExecute");
            
            [backEndService retrieveCurrentCartAndExecute:^(HYBCart *cart, NSError *error) {
                expect(cart).to.beTruthy();
                
                 NSLog(@"addProductToCurrentCart");
                
                [backEndService addProductToCurrentCart:TEST_PRODUCT_CODE amount:@5 block:^(HYBCart *cart, NSString *successMsg) {
                    expect(cart).to.beTruthy();
                    
                     NSLog(@"updateProductOnCurrentCartAmount");
                    
                    [backEndService updateProductOnCurrentCartAmount:@"0" mount:@3 andExecute:^(HYBCart *cart, NSString *string) {
                        expect(cart).to.beTruthy();
                        
                        NSLog(@"setPaymentType");
                        
                        [backEndService setPaymentType:CART_PAYMENTTYPE_ACCOUNT onCartWithCode:cart.code execute:^(HYBCart *cart, NSError *successMsg) {
                            expect(cart.paymentTypeCode).to.equal(CART_PAYMENTTYPE_ACCOUNT);
                            
                             NSLog(@"costCentersForCurrentStoreAndExecute");
                            
                            [backEndService costCentersForCurrentStoreAndExecute:^(NSArray *costCenter, NSError *err) {
                                expect(costCenter.count > 0).to.beTruthy();
                                
                                HYBCostCenter *center = costCenter.firstObject;
                                expect(center).to.beTruthy();
                                
                                 NSLog(@"setCostCenterWithCode");
                                
                                [backEndService setCostCenterWithCode:center.code onCartWithCode:cart.code andExecute:^(HYBCart *cart, NSError *error) {
                                    expect(error).to.beFalsy();
                                    expect(cart.code).to.beTruthy();
                                    
                                    HYBAddress *addr = center.addresses.firstObject;
                                    expect(addr).to.beTruthy();
                                    
                                    NSLog(@"setDeliveryAddressWithCode");
                                    
                                    [backEndService setDeliveryAddressWithCode:addr.id onCartWithCode:cart.code andExecute:^(HYBCart *cart, NSError *error) {
                                        expect(error).to.beFalsy();
                                        expect(cart).to.beTruthy();
                                        expect(cart.code).to.beTruthy();
                                        
                                         NSLog(@"getDeliveryModesForCart");
                                        
                                        [backEndService getDeliveryModesForCart:cart.code andExecute:^(NSArray *modes, NSError *error) {
                                            expect(cart).to.beTruthy();
                                            expect(cart.code).to.beTruthy();
                                            expect(error).to.beFalsy();
                                            expect(modes.count > 0).to.beTruthy();
                                            HYBDeliveryMode *mode = modes.firstObject;
                                            
                                            NSLog(@"setDeliveryModeWithCode");
                                            
                                            [backEndService setDeliveryModeWithCode:mode.code onCartWithCode:cart.code andExecute:^(HYBCart *cart, NSError *error) {
                                                expect(error).to.beFalsy();
                                                expect(cart).to.beTruthy();
                                                expect(cart.code).to.beTruthy();
                                                
                                                  NSLog(@"placeOrderWithCart");
                                                
                                                [backEndService placeOrderWithCart:cart andExecute:^(HYBOrder *order, NSError *error) {
                                                    expect(error).to.beFalsy();
                                                    expect(order.code).to.beTruthy();
                                                    
                                                     NSLog(@"findOrderByCode");
                                                    
                                                    [backEndService findOrderByCode:order.code andExecute:^(HYBOrder *order, NSError *error) {
                                                        HYBAddress *deliveryAddr = order.deliveryAddress;
                                                        HYBDeliveryMode *mode = order.deliveryMode;
                                                        
                                                        expect(deliveryAddr.formattedAddress).to.beTruthy();
                                                        expect(deliveryAddr.formattedAddressBreakLines).to.beTruthy();
                                                        expect(mode.name).to.beTruthy();
                                                        
                                                        done();
                                                    }];
                                                }];
                                            }];
                                        }];
                                    }];
                                }];
                            }];
                        }];
                    }];
                }];
            }];
        }];
    });
});

describe(@"Multiple add products", ^{
    __block HYB2BService *backEndService;
    __block NSString *buildUserId;
    
    NSLog(@"Multiple add products");
    
    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];
        
        backEndService = [[HYB2BService alloc] initWithDefaults];
        backEndService.userDefaults = [[NSUserDefaults alloc] init];

        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
        buildUserId = [[MYEnvironmentConfig sharedConfig] buildUserId];
    });
    
    it(@"rapidly add the same product multiple time", ^AsyncBlock {
        
         NSLog(@"authenticateUser");
        
        [backEndService authenticateUser:buildUserId password:TEST_PASSWORD block:^(NSString *msg, NSError *error) {
            
            expect(msg).to.equal(NSLocalizedString(@"login_success", nil));
            expect(error).to.beFalsy();
            
            double delayInSeconds = 1.0;
            dispatch_queue_t testQueue = dispatch_queue_create("testQueue", nil);
            dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
            dispatch_after(popTime, testQueue, ^(void) {
                
                 NSLog(@"retrieveCurrentCartAndExecute");
                
                [backEndService retrieveCurrentCartAndExecute:^(HYBCart *cart, NSError *error) {
                    expect(cart).to.beTruthy();
                    
                    __block int count = 0;
                    __block int maxCount = 5;
                    __block int totalAdded = 0;
                    
                   __block NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:0.3
                                                                      target:[NSBlockOperation blockOperationWithBlock:^{
                        
                        if (++count > maxCount) {
                            [timer invalidate];
                            timer = nil;
                            
                            NSLog(@"delayed verification ");
                            
                            //delayed verification (wait for late callbacks)
                            double verificationDelayInSeconds = 3.0;
                            dispatch_queue_t doneQueue = dispatch_queue_create("doneQueue", nil);
                            dispatch_time_t secondPopTime = dispatch_time(DISPATCH_TIME_NOW, verificationDelayInSeconds * NSEC_PER_SEC);
                            dispatch_after(secondPopTime, doneQueue, ^(void) {
                                
                                 NSLog(@"delayed verification DONE");
                                
                                expect(maxCount).to.equal(totalAdded);
                                done();
                            });
                            
                        } else {
                            NSLog(@"addProductToCurrentCart");
                            
                            [backEndService addProductToCurrentCart:TEST_PRODUCT_CODE amount:@1 block:^(HYBCart *cart, NSString *successMsg) {
                                expect(cart).to.beTruthy();
                                expect(cart.code).to.beTruthy();
                                expect(error).to.beFalsy();
                                
                                totalAdded++;
                            }];
                        }
                        
                    }]
                                                                    selector:@selector(main)
                                                                    userInfo:nil
                                                                     repeats:YES];
                    
                }];
            });
        }];
    });
});
describe(@"Basic Rest-WS Features", ^{
    
    NSLog(@"Basic Rest-WS Features");
    
    __block HYB2BService *b2bService;
    
    beforeAll(^{
        NSString *bundlePath = [[NSBundle bundleForClass:[self class]] resourcePath];
        [NSBundle bundleWithPath:bundlePath];
        
        [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
    });
    
    beforeEach(^{
        b2bService = [[HYB2BService alloc] initWithDefaults];
        b2bService.userDefaults = [[NSUserDefaults alloc] init];
        
    });
    it(@"should the proper error message from the web service response in error case", ^AsyncBlock {
        
        NSString *url = [[NSString alloc] initWithFormat:@"%@/%@/catalogs/%@/%@/categories/%@", [b2bService restUrlPrefix],
                         [b2bService currentStoreId], [b2bService currentCatalogId], [b2bService currentCatalogVersion], [b2bService rootCategoryId]];
        
        [b2bService callHTTPMethod:@"GET"
                               url:url
                            params:nil
                           headers:nil
                           success:^(AFHTTPRequestOperation *operation, id responseObject) {
                               
                                NSLog(@"Basic Rest-WS Features GET result success");
                               
                               expect(responseObject).to.beTruthy();
                               responseObject = (NSDictionary*)responseObject;
                               expect(responseObject[@"pageSize"]).to.beTruthy();
                               expect(responseObject[@"id"]).to.beTruthy();
                               expect(responseObject[@"numberOfPages"]).to.beTruthy();
                               expect(responseObject[@"subcategories"]).to.beTruthy();
                               expect(responseObject[@"totalNumber"]).to.beTruthy();
                               expect(responseObject[@"type"]).to.beTruthy();
                               expect(responseObject[@"name"]).to.beTruthy();
                               expect(responseObject[@"url"]).to.beTruthy();
                               done();
                           }
         
                           failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                               
                                NSLog(@"Basic Rest-WS Features GET result failure");
                               
                               expect(error).to.beTruthy();
                               done();
                           }];
        
    });
    it(@"should support urls with port", ^{
        
        NSLog(@"should support urls with port");
        
        NSDictionary *config = [[MYEnvironmentConfig sharedConfig] configValues];
        NSString *host = [config objectForKey:HOST_ATTRIBUTE_KEY];
        NSString *port = [config objectForKey:PORT_ATTRIBUTE_KEY];
        
        expect(b2bService.baseStoreUrl).to.contain(host);
        if (port) expect(b2bService.baseStoreUrl).to.contain(port);
        
    });
});


SpecEnd

