<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="financialCart" tagdir="/WEB-INF/tags/addons/financialacceleratorstorefront/desktop/cart" %>
<%@ attribute name="displayChangeOptionLink" required="false" type="java.lang.Boolean" %>

<div class="span-8 cartItemsHeadline">
    <c:forEach items="${cartData.entries}" var="entry" varStatus="status">
        <c:if test="${status.first}">
            <div>
            	<div class="quoteName">
         			<span class="productname">
         				<spring:theme code="${cartData.insuranceQuote.quoteTitle}" var="quoteTitle" />
         				<spring:theme code="checkout.cart.type.quote" arguments="${quoteTitle}" text="${quoteTitle} Quote" /> 
        			</span>
        			<c:if test="${cartData.insuranceQuote.state eq 'BIND' }">
        				<p>
	                    	<spring:theme code="checkout.cart.quote.id" text="ID" />: <span class="price">${cartData.insuranceQuote.quoteId}</span>
	                    </p>
	                    <p>
	                    	<spring:theme code="checkout.cart.expiry.date" text="Expiry Date" />: <span class="price">${cartData.insuranceQuote.formattedExpiryDate }</span>
	                    </p>
        			</c:if>
         		</div>

				<c:set var="paymentFrequency" value="" />
				
				<c:choose>
					<c:when test="${not empty entry.product.price.recurringChargeEntries }">
						<c:set var="paymentFrequency"><spring:theme code="checkout.cart.payment.frequency.monthly" text="Monthly" /></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="paymentFrequency"><spring:theme code="checkout.cart.payment.frequency.annual" text="Annual" /></c:set>
					</c:otherwise>
				</c:choose>
				
				<div class="cartItem">
                    <span class="quoteItem"><spring:theme code="checkout.cart.payment.frequency" text="Payment Frequency" />: </span>
                    <span class="price">${paymentFrequency}</span>
				</div>
				
				<c:set var="shownStartDate" value="" />
				<c:if test="${cartData.insuranceQuote.quoteType ne 'EVENT'}">
					<c:set var="shownStartDate">${cartData.insuranceQuote.startDate}</c:set>
				</c:if>
				<c:if test="${not empty cartData.insuranceQuote.formattedStartDate}">
					<c:set var="shownStartDate">${cartData.insuranceQuote.formattedStartDate}</c:set>
				</c:if>

				<c:if test="${not empty shownStartDate}">
                <div class="cartItem">
                    <span><spring:theme code="checkout.cart.start.date" text="Start Date" />:</span>
                    <span class="price">${cartData.insuranceQuote.formattedStartDate}</span>
				</div>
				</c:if>

				<c:if test="${cartData.insuranceQuote.quoteType eq 'TRAVEL'}">
					<c:if test="${not empty cartData.insuranceQuote.tripEndDate}">
                    <div class="cartItem">
                        <span><spring:theme code="checkout.cart.end.date" text="End Date" />:</span>
                        <span class="price">${cartData.insuranceQuote.tripEndDate}</span>
					</div>
					</c:if>
					<c:if test="${not empty cartData.insuranceQuote.tripNoOfTravellers}">
					<div class="cartItem">
                        <span><spring:theme code="checkout.cart.num.travellers" text="Number of Travellers" />:</span>
                        <span class="price">${cartData.insuranceQuote.tripNoOfTravellers}</span>
					</div>
					<div class="cartItem">
                        <span><spring:theme code="checkout.cart.age.travellers" text="Travellers Ages" />:</span>
                        <span class="price">
                            <c:forEach items="${cartData.insuranceQuote.tripTravellersAge}" var="age" varStatus="status">
                            ${age}<c:if test="${not status.last}">,</c:if>
                            </c:forEach>
                        </span>
					</div>
					</c:if>
				</c:if>
				
           		<div class="cartItem">
                    <span>
                        <c:choose>
                        <c:when test="${cartData.insuranceQuote.propertyCoverRequired eq 'Buildings-Only' }">
                            <spring:theme code="checkout.property.cover.building" text="Buildings Only" />
                        </c:when>
                        <c:when test="${cartData.insuranceQuote.propertyCoverRequired eq 'Contents-Only' }">
                            <spring:theme code="checkout.property.cover.contents" text="Contents Only" />
                        </c:when>
                        <c:when test="${cartData.insuranceQuote.propertyCoverRequired eq 'Building-And-Contents' }">
                            <spring:theme code="checkout.property.cover.building.and.contents" text="Buildings & Contents" />
                        </c:when>
                        <c:when test="${not empty cartData.insuranceQuote.propertyCoverRequired }">
                            ${cartData.insuranceQuote.propertyCoverRequired}
                        </c:when>
                        <c:otherwise>
                            ${entry.product.name}
                        </c:otherwise>
                        </c:choose>
                        </span>
                    <span class="price"><format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/></span>
           		</div>
            
                <c:if test="${not empty entry.product.price.oneTimeChargeEntries and not hideCartBillingEvents}">
                    <c:forEach items="${entry.product.price.oneTimeChargeEntries}" var="oneTimeChargeEntry" begin="1">
                        <div class="price">
                            <c:set var="priceText">
                                <format:price priceData="${oneTimeChargeEntry.price}"/>
                            </c:set>
                            <p><spring:theme code="text.price.up.to"/>${priceText}
                                &nbsp;&nbsp;${oneTimeChargeEntry.billingTime.name}</p>
                        </div>
                    </c:forEach>
                </c:if>
                
            </div>
        </c:if>
    </c:forEach>
</div>
<div class="span-8 cartItems">
    <c:forEach items="${cartData.entries}" var="entry" varStatus="status">
        <c:if test="${not status.first and entry.removeable}">
            <div class="cartItem">
                <c:url value="${entry.product.url}" var="productUrl"/>
                <span>
                    <ycommerce:testId code="cart_product_name">
                        ${entry.product.name}
                    </ycommerce:testId>
                </span>
                <span class="price">
                    <ycommerce:testId code="cart_totalProductPrice_label">
                        <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
                    </ycommerce:testId>
                </span>
            </div>
        </c:if>
    </c:forEach>
</div>
