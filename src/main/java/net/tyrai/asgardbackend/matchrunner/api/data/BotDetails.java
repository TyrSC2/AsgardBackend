package net.tyrai.asgardbackend.matchrunner.api.data;

import java.util.Date;
import java.util.List;

import net.tyrai.asgardbackend.matchrunner.repository.BotVersion;
import net.tyrai.asgardbackend.matchrunner.repository.Build;

public class BotDetails {
    private long id;
    private String name;
    private String type;
    private String race;
    private Date createdDate;
	private List<Build> builds;
	private List<BotVersion> versions;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<Build> getBuilds() {
		return builds;
	}

	public void setBuilds(List<Build> builds) {
		this.builds = builds;
	}

	public List<BotVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<BotVersion> versions) {
		this.versions = versions;
	}
}
