/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.insurance.data.InsuranceQuoteReviewData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.financialacceleratorstorefront.checkout.step.InsuranceCheckoutStep;
import de.hybris.platform.financialfacades.facades.InsuranceCartFacade;
import de.hybris.platform.financialfacades.facades.InsuranceQuoteFacade;
import de.hybris.platform.financialservices.enums.QuoteBindingState;
import de.hybris.platform.storefront.checkout.facades.InsuranceQuoteReviewFacade;
import de.hybris.platform.storefront.checkout.steps.InsuranceCheckoutGroup;
import de.hybris.platform.storefront.controllers.ControllerConstants;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;


@Controller
@RequestMapping(value = "/checkout/multi/quote")
public class QuoteReviewCheckoutStepController extends AbstractInsuranceCheckoutStepController
{
	protected static final String QUOTE_REVIEW = "quote-review";

	@Resource
	private InsuranceQuoteReviewFacade insuranceQuoteReviewFacade;

	@Resource
	private InsuranceQuoteFacade insuranceQuoteFacade;

	@Resource(name = "cartFacade")
	private InsuranceCartFacade cartFacade;

	@Override
	@RequestMapping(value = "/review", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = QUOTE_REVIEW)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException,
			CommerceCartModificationException
	{
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		setupDeliveryAddress();
		prepareQuoteReview(model);

		try
		{
			getCartFacade().saveCurrentUserCart();
		}
		catch (final CommerceSaveCartException e)
		{
			LOG.error("Save cart error.");
			e.printStackTrace();
		}

		final CategoryData categoryData = cartFacade.getSelectedInsuranceCategory();
		if (categoryData != null)
		{
			model.addAttribute("categoryName", categoryData.getName());
		}

		return ControllerConstants.Views.Pages.MultiStepCheckout.QuoteReviewPage;
	}

	@Override
	protected void setCheckoutStepLinksForModel(final Model model, final CheckoutStep checkoutStep)
	{
		super.setCheckoutStepLinksForModel(model, checkoutStep);
		if (QuoteBindingState.UNBIND.equals(insuranceQuoteFacade.getQuoteStateFromSessionCart()))
		{
			model.addAttribute("nextStepUrl", StringUtils.remove(checkoutStep.currentStep(), "redirect:"));
		}
	}

	protected void prepareQuoteReview(final Model model)
	{
		model.addAttribute("cartData", getCartFacade().getSessionCart());
		final List<InsuranceQuoteReviewData> insuranceQuoteReviews = insuranceQuoteReviewFacade.getInsuranceQuoteReviews();
		model.addAttribute("insuranceQuoteReviews", insuranceQuoteReviews);

		boolean isValidQuote = ((InsuranceCheckoutStep) getCheckoutStep()).isValid();
		for (final InsuranceQuoteReviewData reviewData : insuranceQuoteReviews)
		{
			final Map<String, Boolean> validationMap = Maps.newHashMap();
			if (getCheckoutStep().getCheckoutGroup() instanceof InsuranceCheckoutGroup)
			{
				final Map<String, InsuranceCheckoutStep> progressBar = ((InsuranceCheckoutGroup) getCheckoutStep().getCheckoutGroup())
						.getInsuranceCheckoutProgressBar();
				for (final Map.Entry<String, InsuranceCheckoutStep> step : progressBar.entrySet())
				{
					validationMap.put(step.getKey(), step.getValue().isValid());
					if (isValidQuote && !step.getValue().isValid())
					{
						isValidQuote = Boolean.FALSE;
					}
				}
			}
			reviewData.setValidation(validationMap);
		}

		if (!isValidQuote)
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.quote.review.invalid");
		}
		model.addAttribute("isValidQuote", isValidQuote);
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		if (getCartFacade() != null)
		{
			final boolean isUpdatedFromBindToUnbind = getCartFacade().unbindQuoteInSessionCart();
			if (isUpdatedFromBindToUnbind)
			{
				return getCheckoutStep().currentStep();
			}
		}
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}


	@Override
	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(QUOTE_REVIEW);
	}

	protected InsuranceQuoteFacade getInsuranceQuoteFacade()
	{
		return insuranceQuoteFacade;
	}

	public void setInsuranceQuoteFacade(final InsuranceQuoteFacade insuranceQuoteFacade)
	{
		this.insuranceQuoteFacade = insuranceQuoteFacade;
	}
}
