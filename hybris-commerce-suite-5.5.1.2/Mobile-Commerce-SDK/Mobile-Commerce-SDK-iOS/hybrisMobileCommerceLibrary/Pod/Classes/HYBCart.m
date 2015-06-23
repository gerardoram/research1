//
//  HYBCart.m
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

#import "HYBCart.h"

#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"
#import "NSObject+HYBAdditionalMethods.h"

#import "HYBOrderPromotion.h"
#import "HYBProductPromotion.h"
#import "HYBDeliveryOrderGroup.h"
#import "HYBEntry.h"
#import "HYBConsumedEntry.h"

@implementation HYBCart {
    NSArray  *_itemsHolder;
    NSNumber *_totalUnitCountHolder;
}

+ (instancetype)cartWithParams:(NSDictionary*)params {
    
    NSError *error = nil;
    
    HYBCart *cart = [MTLJSONAdapter modelOfClass:[HYBCart class]
                                fromJSONDictionary:params
                                             error:&error];
    
    if([cart hyb_isNotBlank]) {
        cart.status = CART_OK;
        
        //finish setup
        if(![cart isEmpty]) {
            
            //link promotions to items
            if([cart.items hyb_isNotBlank]) [cart assignPromotions];
        }
    }
    
    if (error) {
        NSLog(@"Couldn't convert JSON to HYBCart models: %@", error);
        return nil;
    }
    
    return cart;
}


+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
             @"type"                        : @"type",
             @"appliedOrderPromotions"      : @"appliedOrderPromotions",
             @"appliedProductPromotions"    : @"appliedProductPromotions",
             @"calculated"                  : @"calculated",
             @"code"                        : @"code",
             @"deliveryItemsQuantity"       : @"deliveryItemsQuantity",
             @"deliveryOrderGroups"         : @"deliveryOrderGroups",
             //@"deliveryCost"                : @"deliveryCost",
             @"entries"                     : @"entries",
             @"guid"                        : @"guid",
             @"net"                         : @"net",
             @"orderDiscountsB"             : @"orderDiscounts",
             @"pickupItemsQuantity"         : @"pickupItemsQuantity",
             @"productDiscounts"            : @"productDiscounts",
             @"site"                        : @"site",
             @"store"                       : @"store",
             @"subTotal"                    : @"subTotal",
             @"totalDiscounts"              : @"totalDiscounts",
             @"totalItems"                  : @"totalItems",
             @"totalPriceB"                 : @"totalPrice",
             @"totalPriceWithTaxB"          : @"totalPriceWithTax",
             @"totalTax"                    : @"totalTax",
             @"paymentType"                 : @"paymentType",
             @"potentialProductPromotions"  : @"potentialProductPromotions",
             @"totalUnitCount"              : @"totalUnitCount"
             };
}

//dicts
/*
 + (NSValueTransformer *)deliveryCostBJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}
*/

+ (NSValueTransformer *)orderDiscountsBJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)productDiscountsJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)subTotalJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)totalDiscountsJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)totalPriceBJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)totalPriceWithTaxBJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)totalTaxJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPrice class]];
}

+ (NSValueTransformer *)paymentTypeJSONTransformer {
    return [NSValueTransformer mtl_JSONDictionaryTransformerWithModelClass:[HYBPaymentType class]];
}

//arrays
+ (NSValueTransformer *)appliedOrderPromotionsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBOrderPromotion class]];
}

+ (NSValueTransformer *)appliedProductPromotionsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBProductPromotion class]];
}

+ (NSValueTransformer *)deliveryOrderGroupsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBDeliveryOrderGroup class]];
}

+ (NSValueTransformer *)entriesJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBEntry class]];
}

+ (NSValueTransformer *)potentialProductPromotionsJSONTransformer {
    return [NSValueTransformer mtl_JSONArrayTransformerWithModelClass:[HYBProductPromotion class]];
}

//legacy

- (BOOL)isEmpty {
    if(![_totalUnitCountHolder hyb_isNotBlank]) [self items];
    return ([_totalUnitCountHolder intValue] == 0);
}

- (NSNumber*)totalPrice {
    return _totalPriceB.value;
}

- (NSNumber*)totalPriceWithTax {
    return _totalPriceWithTaxB.value;
}

- (NSNumber*)orderDiscounts {
    return _orderDiscountsB.value;
}

- (NSString*)totalTaxFormatted {
    return _totalTax.formattedValue;
}

- (NSString*)totalPriceWithTaxFormatted {
    return _totalPriceWithTaxB.formattedValue;
}

- (NSString*)totalPriceFormatted {
    return _totalPriceB.formattedValue;
}

- (NSString*)subTotalFormatted {
    return _subTotal.formattedValue ;
}

- (NSNumber*)totalUnitCount {
    if (_totalUnitCountHolder) {
        return _totalUnitCountHolder;
    }
    
    return _totalUnitCount;
}

- (NSArray*)items {
    
    __block int totalCount = 0;
    
    //calculate items number
    if ([_entries hyb_isNotBlank]) {
        NSMutableArray *itemsHolder = [NSMutableArray array];
        
        for (HYBEntry *entry in _entries) {
            [itemsHolder addObject:entry];
            totalCount += [entry.quantity intValue];
        }
        _itemsHolder = [NSArray arrayWithArray:itemsHolder];
    } else {
        _itemsHolder = [NSArray array];
    }
    
    _totalUnitCountHolder = [NSNumber numberWithInt:totalCount];
    
    return _itemsHolder;
}

- (void)assignPromotions {
    if ([_appliedProductPromotions hyb_isNotBlank]) {
        for (HYBProductPromotion *promotion in _appliedProductPromotions) {
            
            NSString *promotionDescription  = promotion.promotionDescription;
            NSArray *consumedEntries        = promotion.consumedEntries;
            
            for (HYBConsumedEntry *consumedEntry in consumedEntries) {
                
                NSNumber *entryNumber = consumedEntry.orderEntryNumber;
                
                for (HYBEntry *entry in _itemsHolder) {
                    if([[entry entryNumber] intValue] == [entryNumber intValue]) {
                        entry.discountMessage = [NSString stringWithString:promotionDescription];
                        break;
                    }
                }
            }
        }
    }
}

- (NSString*)orderDiscountsFormattedValue {
    return _orderDiscountsB.formattedValue;
}

- (NSString*)orderDiscountsMessage {
    if([_appliedOrderPromotions hyb_isNotBlank]) {
       return [[_appliedOrderPromotions firstObject] promotionDescription];
    }    
    return nil;
}

- (NSString*)paymentTypeCode {
    if ([_paymentType hyb_isNotBlank] && [_paymentType.code hyb_isNotBlank]) {
        return _paymentType.code;
    }
    return @"ACCOUNT";
}
    

- (NSString*)paymentDisplayName {
    if ([_paymentType hyb_isNotBlank] && [_paymentType.displayName hyb_isNotBlank]) {
        return _paymentType.displayName;
    }
    return @"ACCOUNT PAYMENT";
}

@end
