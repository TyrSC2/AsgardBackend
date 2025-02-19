package net.tyrai.asgardbackend.matchrunner.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

@Entity(name = "BotVersion")
@Table(name = "bot_versions")
public class BotVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	@Column(name = "name", nullable = true)
    private String name;

	@Column(name = "bot", nullable = true)
    private String bot;

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
	
    public String getBot() {
		return bot;
	}

	public void setBot(String bot) {
		this.bot = bot;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
