package net.tyrai.asgardbackend.matchrunner.api.data;

public class CreateMatch {
	private String testVersion;
	private String testBuild;
	private String opponentName;
	private String opponentVersion;
	private String opponentBuild;

	public String getTestVersion() {
		return testVersion;
	}
	public void setTestVersion(String testVersion) {
		this.testVersion = testVersion;
	}
	public String getTestBuild() {
		return testBuild;
	}
	public void setTestBuild(String testBuild) {
		this.testBuild = testBuild;
	}
	public String getOpponentName() {
		return opponentName;
	}
	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}
	public String getOpponentVersion() {
		return opponentVersion;
	}
	public void setOpponentVersion(String opponentVersion) {
		this.opponentVersion = opponentVersion;
	}
	public String getOpponentBuild() {
		return opponentBuild;
	}
	public void setOpponentBuild(String opponentBuild) {
		this.opponentBuild = opponentBuild;
	}
}
