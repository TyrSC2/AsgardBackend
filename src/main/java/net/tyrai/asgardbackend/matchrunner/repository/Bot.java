package net.tyrai.asgardbackend.matchrunner.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

@Entity(name = "Bot")
@Table(name = "bots")
public class Bot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	@Column(name = "name", nullable = true)
    private String name;

	@Column(name = "type", nullable = true)
    private String type;

	@Column(name = "race", nullable = true)
    private String race;
	
    @CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

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

}
