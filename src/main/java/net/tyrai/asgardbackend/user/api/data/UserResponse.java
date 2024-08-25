package net.tyrai.asgardbackend.user.api.data;

import net.tyrai.asgardbackend.user.api.data.userresponse.PaymentPlanInformation;

public class UserResponse {
	private String email;
	private PaymentPlanInformation paymentPlan;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public PaymentPlanInformation getPaymentPlan() {
		return paymentPlan;
	}

	public void setPaymentPlan(PaymentPlanInformation paymentPlan) {
		this.paymentPlan = paymentPlan;
	}
}
