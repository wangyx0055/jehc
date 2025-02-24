package jehc.paymodules.paypal.bean.order;


import java.util.List;

public class Refund {
	/**
	 * ID of the refund transaction. 17 characters max.
	 */
	private String id;
	/**
	 * Details including both refunded amount (to payer) and refunded fee (to payee). 10 characters max.
	 */
	private Amount amount;
	/**
	 * State of the refund.
	 */
	private String state;
	/**
	 * Reason description for the Sale transaction being refunded.
	 */
	private String reason;
	/**
	 * Your own invoice or tracking ID number. Character length and limitations: 127 single-byte alphanumeric characters.
	 */
	private String invoiceNumber;
	/**
	 * ID of the Sale transaction being refunded.
	 */
	private String saleId;
	/**
	 * ID of the sale transaction being refunded.
	 */
	private String captureId;
	/**
	 * ID of the payment resource on which this transaction is based.
	 */
	private String parentPayment;
	/**
	 * Description of what is being refunded for.
	 */
	private String description;
	/**
	 * Time of refund as defined in [RFC 3339 Section 5.6](http://tools.ietf.org/html/rfc3339#section-5.6).
	 */
	private String createTime;
	/**
	 * Time that the resource was last updated.
	 */
	private String updateTime;
	/**
	 * The reason code for the refund state being pending
	 */
	private String reasonCode;
	/**
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public Refund() {
	}


	/**
	 * ID of the refund transaction. 17 characters max.
	 */
	
	public String getId() {
		return this.id;
	}

	/**
	 * Details including both refunded amount (to payer) and refunded fee (to payee). 10 characters max.
	 */
	
	public Amount getAmount() {
		return this.amount;
	}

	/**
	 * State of the refund.
	 */
	
	public String getState() {
		return this.state;
	}

	/**
	 * Reason description for the Sale transaction being refunded.
	 */
	
	public String getReason() {
		return this.reason;
	}

	/**
	 * Your own invoice or tracking ID number. Character length and limitations: 127 single-byte alphanumeric characters.
	 */
	
	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}

	/**
	 * ID of the Sale transaction being refunded.
	 */
	
	public String getSaleId() {
		return this.saleId;
	}

	/**
	 * ID of the sale transaction being refunded.
	 */
	
	public String getCaptureId() {
		return this.captureId;
	}

	/**
	 * ID of the payment resource on which this transaction is based.
	 */
	
	public String getParentPayment() {
		return this.parentPayment;
	}

	/**
	 * Description of what is being refunded for.
	 */
	
	public String getDescription() {
		return this.description;
	}

	/**
	 * Time of refund as defined in [RFC 3339 Section 5.6](http://tools.ietf.org/html/rfc3339#section-5.6).
	 */
	
	public String getCreateTime() {
		return this.createTime;
	}

	/**
	 * Time that the resource was last updated.
	 */
	
	public String getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * The reason code for the refund state being pending
	 */
	
	public String getReasonCode() {
		return this.reasonCode;
	}

	/**
	 */
	
	public List<Links> getLinks() {
		return this.links;
	}

	/**
	 * ID of the refund transaction. 17 characters max.
	 * @return this
	 */
	
	public Refund setId(final String id) {
		this.id = id;
		return this;
	}

	/**
	 * Details including both refunded amount (to payer) and refunded fee (to payee). 10 characters max.
	 * @return this
	 */
	
	public Refund setAmount(final Amount amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * State of the refund.
	 * @return this
	 */
	
	public Refund setState(final String state) {
		this.state = state;
		return this;
	}

	/**
	 * Reason description for the Sale transaction being refunded.
	 * @return this
	 */
	
	public Refund setReason(final String reason) {
		this.reason = reason;
		return this;
	}

	/**
	 * Your own invoice or tracking ID number. Character length and limitations: 127 single-byte alphanumeric characters.
	 * @return this
	 */
	
	public Refund setInvoiceNumber(final String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}

	/**
	 * ID of the Sale transaction being refunded.
	 * @return this
	 */
	
	public Refund setSaleId(final String saleId) {
		this.saleId = saleId;
		return this;
	}

	/**
	 * ID of the sale transaction being refunded.
	 * @return this
	 */
	
	public Refund setCaptureId(final String captureId) {
		this.captureId = captureId;
		return this;
	}

	/**
	 * ID of the payment resource on which this transaction is based.
	 * @return this
	 */
	
	public Refund setParentPayment(final String parentPayment) {
		this.parentPayment = parentPayment;
		return this;
	}

	/**
	 * Description of what is being refunded for.
	 * @return this
	 */
	
	public Refund setDescription(final String description) {
		this.description = description;
		return this;
	}

	/**
	 * Time of refund as defined in [RFC 3339 Section 5.6](http://tools.ietf.org/html/rfc3339#section-5.6).
	 * @return this
	 */
	
	public Refund setCreateTime(final String createTime) {
		this.createTime = createTime;
		return this;
	}

	/**
	 * Time that the resource was last updated.
	 * @return this
	 */
	
	public Refund setUpdateTime(final String updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	/**
	 * The reason code for the refund state being pending
	 * @return this
	 */
	
	public Refund setReasonCode(final String reasonCode) {
		this.reasonCode = reasonCode;
		return this;
	}

	/**
	 *
	 * @return this
	 */
	
	public Refund setLinks(final List<Links> links) {
		this.links = links;
		return this;
	}

}
