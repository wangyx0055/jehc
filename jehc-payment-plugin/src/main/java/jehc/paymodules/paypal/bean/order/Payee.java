package jehc.paymodules.paypal.bean.order;


import com.alibaba.fastjson.annotation.JSONField;

public class Payee  {
	/**
	 * Email Address associated with the Payee's PayPal Account. If the provided email address is not associated with any PayPal Account, the payee can only receive PayPal Wallet Payments. Direct Credit Card Payments will be denied due to card compliance requirements.
	 */
	private String email;
	/**
	 * Encrypted PayPal account identifier for the Payee.
	 */
	@JSONField(name = "merchant_id")
	private String merchantId;
	/**
	 * First Name of the Payee.
	 */
	@JSONField(name = "first_name")
	private String firstName;
	/**
	 * Last Name of the Payee.
	 */
	@JSONField(name = "last_name")
	private String lastName;
	/**
	 * Unencrypted PayPal account Number of the Payee
	 */
	@JSONField(name = "account_number")
	private String accountNumber;
	/**
	 * Information related to the Payee.
	 */
	private Phone phone;

	/**
	 * Default Constructor
	 */
	public Payee() {
	}

	/**
	 * Email Address associated with the Payee's PayPal Account. If the provided email address is not associated with any PayPal Account, the payee can only receive PayPal Wallet Payments. Direct Credit Card Payments will be denied due to card compliance requirements.
	 */
	@SuppressWarnings("all")
	public String getEmail() {
		return this.email;
	}

	/**
	 * Encrypted PayPal account identifier for the Payee.
	 */
	@SuppressWarnings("all")
	public String getMerchantId() {
		return this.merchantId;
	}

	/**
	 * First Name of the Payee.
	 */
	@SuppressWarnings("all")
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Last Name of the Payee.
	 */
	@SuppressWarnings("all")
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Unencrypted PayPal account Number of the Payee
	 */
	@SuppressWarnings("all")
	public String getAccountNumber() {
		return this.accountNumber;
	}

	/**
	 * Information related to the Payee.
	 */
	@SuppressWarnings("all")
	public Phone getPhone() {
		return this.phone;
	}

	/**
	 * Email Address associated with the Payee's PayPal Account. If the provided email address is not associated with any PayPal Account, the payee can only receive PayPal Wallet Payments. Direct Credit Card Payments will be denied due to card compliance requirements.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Payee setEmail(final String email) {
		this.email = email;
		return this;
	}

	/**
	 * Encrypted PayPal account identifier for the Payee.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Payee setMerchantId(final String merchantId) {
		this.merchantId = merchantId;
		return this;
	}

	/**
	 * First Name of the Payee.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Payee setFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	/**
	 * Last Name of the Payee.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Payee setLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	/**
	 * Unencrypted PayPal account Number of the Payee
	 * @return this
	 */
	@SuppressWarnings("all")
	public Payee setAccountNumber(final String accountNumber) {
		this.accountNumber = accountNumber;
		return this;
	}

	/**
	 * Information related to the Payee.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Payee setPhone(final Phone phone) {
		this.phone = phone;
		return this;
	}

	@Override
	@SuppressWarnings("all")
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof Payee)) return false;
		final Payee other = (Payee) o;
		if (!other.canEqual((Object) this)) return false;
		if (!super.equals(o)) return false;
		final Object this$email = this.getEmail();
		final Object other$email = other.getEmail();
		if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
		final Object this$merchantId = this.getMerchantId();
		final Object other$merchantId = other.getMerchantId();
		if (this$merchantId == null ? other$merchantId != null : !this$merchantId.equals(other$merchantId)) return false;
		final Object this$firstName = this.getFirstName();
		final Object other$firstName = other.getFirstName();
		if (this$firstName == null ? other$firstName != null : !this$firstName.equals(other$firstName)) return false;
		final Object this$lastName = this.getLastName();
		final Object other$lastName = other.getLastName();
		if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) return false;
		final Object this$accountNumber = this.getAccountNumber();
		final Object other$accountNumber = other.getAccountNumber();
		if (this$accountNumber == null ? other$accountNumber != null : !this$accountNumber.equals(other$accountNumber)) return false;
		final Object this$phone = this.getPhone();
		final Object other$phone = other.getPhone();
		if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) return false;
		return true;
	}

	@SuppressWarnings("all")
	protected boolean canEqual(final Object other) {
		return other instanceof Payee;
	}

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + super.hashCode();
		final Object $email = this.getEmail();
		result = result * PRIME + ($email == null ? 43 : $email.hashCode());
		final Object $merchantId = this.getMerchantId();
		result = result * PRIME + ($merchantId == null ? 43 : $merchantId.hashCode());
		final Object $firstName = this.getFirstName();
		result = result * PRIME + ($firstName == null ? 43 : $firstName.hashCode());
		final Object $lastName = this.getLastName();
		result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
		final Object $accountNumber = this.getAccountNumber();
		result = result * PRIME + ($accountNumber == null ? 43 : $accountNumber.hashCode());
		final Object $phone = this.getPhone();
		result = result * PRIME + ($phone == null ? 43 : $phone.hashCode());
		return result;
	}
}
