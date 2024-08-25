package net.tyrai.asgardbackend.user.api.data.userresponse;

public class PaymentPlanInformation {
	private String period;
	private long amount;
	private String currency;
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
