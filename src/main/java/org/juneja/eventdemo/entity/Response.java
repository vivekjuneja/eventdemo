package org.juneja.eventdemo.entity;

public class Response {

	private String responseId;

	private String responseMessage;

	private String responseCode;

	public Response(String responseId, String responseMessage,
			String responseCode) {
		super();
		this.responseId = responseId;
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public String toString() {
		return "Response [responseId=" + responseId + ", responseMessage="
				+ responseMessage + ", responseCode=" + responseCode + "]";
	}

}
