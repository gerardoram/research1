//
// HYBAppDelegateSpec.m
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
#import <MyEnvironmentConfig/MYEnvironmentConfig.h>
#import <hybrisMobileCommerceLibrary/NSObject+HYBAdditionalMethods.h>
#import "HYBAppDelegate.h"
#import "HYBBackEndFacade.h"
#import "Specta.h"

SpecBegin(HYBAppDelegate)
    describe(@"HYBAppDelegate", ^{
        __block HYBAppDelegate *delegate;

        beforeAll(^{
            delegate = [[HYBAppDelegate alloc] init];
        });
        it(@"should create app delegate", ^{
            expect(delegate).to.beTruthy();
        });
        it(@"should confgure app from environment settgins", ^{
            NSDictionary *config = [[MYEnvironmentConfig sharedConfig] configValues];

            expect([[config objectForKey:HOST_ATTRIBUTE_KEY] hyb_isNotBlank]).to.beTruthy();
            expect([[config objectForKey:REST_URL_ATTRIBUTE_KEY] hyb_isNotBlank]).to.beTruthy();
        });
    });
SpecEnd
