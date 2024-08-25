package net.tyrai.asgardbackend.matchrunner.api.data;

public class CreateBot {
	private String name;
	private String type;
	private String race;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
}
