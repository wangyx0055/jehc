package jehc.paymodules.paypal.bean.order;


import java.util.List;

public class Error  {
	/**
	 * Human readable, unique name of the error.
	 */
	private String name;
	/**
	 * Message describing the error.
	 */
	private String message;
	/**
	 * Additional details of the error
	 */
	private List<ErrorDetails> details;
	/**
	 * URI for detailed information related to this error for the developer.
	 */
	private String informationLink;
	/**
	 * PayPal internal identifier used for correlation purposes.
	 */
	private String debugId;

	/**
	 * @deprecated This property is not available publicly
	 * PayPal internal error code.
	 */
	@Deprecated
	private String code;


	/**
	 * Default Constructor
	 */
	public Error() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Error(String name, String message, String informationLink, String debugId) {
		this.name = name;
		this.message = message;
		this.informationLink = informationLink;
		this.debugId = debugId;
	}

	public String toString() {
		return "name: " + this.name + "\tmessage: " + this.message + "\tdetails: " + this.details + "\tdebug-id: " + this.debugId + "\tinformation-link: " + this.informationLink;
	}

	/**
	 * Human readable, unique name of the error.
	 */
	@SuppressWarnings("all")
	public String getName() {
		return this.name;
	}

	/**
	 * Message describing the error.
	 */
	@SuppressWarnings("all")
	public String getMessage() {
		return this.message;
	}

	/**
	 * Additional details of the error
	 */
	@SuppressWarnings("all")
	public List<ErrorDetails> getDetails() {
		return this.details;
	}

	/**
	 * URI for detailed information related to this error for the developer.
	 */
	@SuppressWarnings("all")
	public String getInformationLink() {
		return this.informationLink;
	}

	/**
	 * PayPal internal identifier used for correlation purposes.
	 */
	@SuppressWarnings("all")
	public String getDebugId() {
		return this.debugId;
	}

	/**
	 * @deprecated This property is not available publicly
	 * PayPal internal error code.
	 */
	@Deprecated
	@SuppressWarnings("all")
	public String getCode() {
		return this.code;
	}



	/**
	 * Human readable, unique name of the error.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Error setName(final String name) {
		this.name = name;
		return this;
	}

	/**
	 * Message describing the error.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Error setMessage(final String message) {
		this.message = message;
		return this;
	}

	/**
	 * Additional details of the error
	 * @return this
	 */
	@SuppressWarnings("all")
	public Error setDetails(final List<ErrorDetails> details) {
		this.details = details;
		return this;
	}

	/**
	 * URI for detailed information related to this error for the developer.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Error setInformationLink(final String informationLink) {
		this.informationLink = informationLink;
		return this;
	}

	/**
	 * PayPal internal identifier used for correlation purposes.
	 * @return this
	 */
	@SuppressWarnings("all")
	public Error setDebugId(final String debugId) {
		this.debugId = debugId;
		return this;
	}


	/**
	 * @deprecated This property is not available publicly
	 * PayPal internal error code.
	 * @return this
	 */
	@Deprecated
	@SuppressWarnings("all")
	public Error setCode(final String code) {
		this.code = code;
		return this;
	}


}
