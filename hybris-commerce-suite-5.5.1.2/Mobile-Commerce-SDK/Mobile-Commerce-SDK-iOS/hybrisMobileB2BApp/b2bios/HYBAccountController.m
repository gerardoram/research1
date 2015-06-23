//
// HYBAccountController.m
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


#import "HYBAccountController.h"

@interface HYBAccountController ()

@end

@implementation HYBAccountController

- (instancetype)initWithBackEndService:(id <HYBBackEndFacade>)b2bService {

    if (self = [super initWithBackEndService:b2bService]) {
        self.view.backgroundColor = [UIColor whiteColor];
        
        UILabel *label = [UILabel new];
        label.font = [UIFont fontWithName:@"HelveticaNeue" size:45];
        label.text = NSLocalizedString(@"Account", nil);
        [label sizeToFit];
        
        label.center = self.view.center;
        
        [self.view addSubview:label];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self topToolbar].searchDelegate = self;
}

@end
