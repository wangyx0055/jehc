package jehc.paymodules.paypal.bean.order;


import java.util.List;

public class Order {
	/**
	 * Identifier of the order transaction.
	 */
	private String id;
	/**
	 * Identifier to the purchase unit associated with this object. Obsolete. Use one in cart_base.
	 */
	private String purchaseUnitReferenceId;

	/**
	 * Amount being collected.
	 */
	private Amount amount;
	/**
	 * specifies payment mode of the transaction
	 */
	private String paymentMode;
	/**
	 * State of the order transaction.
	 */
	private String state;
	/**
	 * Reason code for the transaction state being Pending or Reversed. This field will replace pending_reason field eventually. Only supported when the `payment_method` is set to `paypal`.
	 */
	private String reasonCode;

	/**
	 * The level of seller protection in force for the transaction.
	 */
	private String protectionEligibility;
	/**
	 * The kind of seller protection in force for the transaction. This property is returned only when the `protection_eligibility` property is set to `ELIGIBLE`or `PARTIALLY_ELIGIBLE`. Only supported when the `payment_method` is set to `paypal`. Allowed values:<br> `ITEM_NOT_RECEIVED_ELIGIBLE`- Sellers are protected against claims for items not received.<br> `UNAUTHORIZED_PAYMENT_ELIGIBLE`- Sellers are protected against claims for unauthorized payments.<br> One or both of the allowed values can be returned.
	 */
	private String protectionEligibilityType;
	/**
	 * ID of the Payment resource that this transaction is based on.
	 */
	private String parentPayment;
	/**
	 * Fraud Management Filter (FMF) details applied for the payment that could result in accept/deny/pending action.
	 */
	private FmfDetails fmfDetails;
	/**
	 * Time the resource was created in UTC ISO8601 format.
	 */
	private String createTime;
	/**
	 * Time the resource was last updated in UTC ISO8601 format.
	 */
	private String updateTime;
	/**
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public Order() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Order(Amount amount) {
		this.amount = amount;
	}


	/**
	 * Identifier of the order transaction.
	 */
	
	public String getId() {
		return this.id;
	}

	/**
	 * Identifier to the purchase unit associated with this object. Obsolete. Use one in cart_base.
	 */
	
	public String getPurchaseUnitReferenceId() {
		return this.purchaseUnitReferenceId;
	}



	/**
	 * Amount being collected.
	 */
	
	public Amount getAmount() {
		return this.amount;
	}

	/**
	 * specifies payment mode of the transaction
	 */
	
	public String getPaymentMode() {
		return this.paymentMode;
	}

	/**
	 * State of the order transaction.
	 */
	
	public String getState() {
		return this.state;
	}

	/**
	 * Reason code for the transaction state being Pending or Reversed. This field will replace pending_reason field eventually. Only supported when the `payment_method` is set to `paypal`.
	 */
	
	public String getReasonCode() {
		return this.reasonCode;
	}



	/**
	 * The level of seller protection in force for the transaction.
	 */
	
	public String getProtectionEligibility() {
		return this.protectionEligibility;
	}

	/**
	 * The kind of seller protection in force for the transaction. This property is returned only when the `protection_eligibility` property is set to `ELIGIBLE`or `PARTIALLY_ELIGIBLE`. Only supported when the `payment_method` is set to `paypal`. Allowed values:<br> `ITEM_NOT_RECEIVED_ELIGIBLE`- Sellers are protected against claims for items not received.<br> `UNAUTHORIZED_PAYMENT_ELIGIBLE`- Sellers are protected against claims for unauthorized payments.<br> One or both of the allowed values can be returned.
	 */
	
	public String getProtectionEligibilityType() {
		return this.protectionEligibilityType;
	}

	/**
	 * ID of the Payment resource that this transaction is based on.
	 */
	
	public String getParentPayment() {
		return this.parentPayment;
	}

	/**
	 * Fraud Management Filter (FMF) details applied for the payment that could result in accept/deny/pending action.
	 */
	
	public FmfDetails getFmfDetails() {
		return this.fmfDetails;
	}

	/**
	 * Time the resource was created in UTC ISO8601 format.
	 */
	
	public String getCreateTime() {
		return this.createTime;
	}

	/**
	 * Time the resource was last updated in UTC ISO8601 format.
	 */
	
	public String getUpdateTime() {
		return this.updateTime;
	}

	/**
	 */
	
	public List<Links> getLinks() {
		return this.links;
	}

	/**
	 * Identifier of the order transaction.
	 * @return this
	 */
	
	public Order setId(final String id) {
		this.id = id;
		return this;
	}

	/**
	 * Identifier to the purchase unit associated with this object. Obsolete. Use one in cart_base.
	 * @return this
	 */
	
	public Order setPurchaseUnitReferenceId(final String purchaseUnitReferenceId) {
		this.purchaseUnitReferenceId = purchaseUnitReferenceId;
		return this;
	}



	/**
	 * Amount being collected.
	 * @return this
	 */
	
	public Order setAmount(final Amount amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * specifies payment mode of the transaction
	 * @return this
	 */
	
	public Order setPaymentMode(final String paymentMode) {
		this.paymentMode = paymentMode;
		return this;
	}

	/**
	 * State of the order transaction.
	 * @return this
	 */
	
	public Order setState(final String state) {
		this.state = state;
		return this;
	}

	/**
	 * Reason code for the transaction state being Pending or Reversed. This field will replace pending_reason field eventually. Only supported when the `payment_method` is set to `paypal`.
	 * @return this
	 */
	
	public Order setReasonCode(final String reasonCode) {
		this.reasonCode = reasonCode;
		return this;
	}


	/**
	 * The level of seller protection in force for the transaction.
	 * @return this
	 */
	
	public Order setProtectionEligibility(final String protectionEligibility) {
		this.protectionEligibility = protectionEligibility;
		return this;
	}

	/**
	 * The kind of seller protection in force for the transaction. This property is returned only when the `protection_eligibility` property is set to `ELIGIBLE`or `PARTIALLY_ELIGIBLE`. Only supported when the `payment_method` is set to `paypal`. Allowed values:<br> `ITEM_NOT_RECEIVED_ELIGIBLE`- Sellers are protected against claims for items not received.<br> `UNAUTHORIZED_PAYMENT_ELIGIBLE`- Sellers are protected against claims for unauthorized payments.<br> One or both of the allowed values can be returned.
	 * @return this
	 */
	
	public Order setProtectionEligibilityType(final String protectionEligibilityType) {
		this.protectionEligibilityType = protectionEligibilityType;
		return this;
	}

	/**
	 * ID of the Payment resource that this transaction is based on.
	 * @return this
	 */
	
	public Order setParentPayment(final String parentPayment) {
		this.parentPayment = parentPayment;
		return this;
	}

	/**
	 * Fraud Management Filter (FMF) details applied for the payment that could result in accept/deny/pending action.
	 * @return this
	 */
	
	public Order setFmfDetails(final FmfDetails fmfDetails) {
		this.fmfDetails = fmfDetails;
		return this;
	}

	/**
	 * Time the resource was created in UTC ISO8601 format.
	 * @return this
	 */
	
	public Order setCreateTime(final String createTime) {
		this.createTime = createTime;
		return this;
	}

	/**
	 * Time the resource was last updated in UTC ISO8601 format.
	 * @return this
	 */
	
	public Order setUpdateTime(final String updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	/**
	 *
	 * @return this
	 */
	
	public Order setLinks(final List<Links> links) {
		this.links = links;
		return this;
	}

}
