package net.tyrai.asgardbackend.matchrunner.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

@Entity(name = "Match")
@Table(name = "matches")
public class Match {
	public static String InProgress = "InProgress";
	public static String Created = "Created";
	public static String Done = "Done";
	public static String Win = "Win";
	public static String Loss = "Loss";
	public static String Tie = "Tie";
	public static String Error = "Error";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	@Column(name = "bot1", nullable = false)
    private String bot1;

	@Column(name = "version1", nullable = false)
    private String version1;

	@Column(name = "build1", nullable = true)
    private String build1;

	@Column(name = "bot2", nullable = false)
    private String bot2;

	@Column(name = "version2", nullable = false)
    private String version2;

	@Column(name = "build2", nullable = true)
    private String build2;

	@Column(name = "map", nullable = false)
    private String map;

	@Column(name = "result", nullable = true)
    private String result;

	@Column(name = "status", nullable = false)
    private String status;
	
	@Column(name = "replay", nullable = true)
    private String replay;

	@CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "started_date")
    private Date startedDate;

    @Column(name = "finished_date")
    private Date finishedDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
    public String getBot1() {
		return bot1;
	}

	public void setBot1(String bot1) {
		this.bot1 = bot1;
	}

	public String getVersion1() {
		return version1;
	}

	public void setVersion1(String version1) {
		this.version1 = version1;
	}

	public String getBuild1() {
		return build1;
	}

	public void setBuild1(String build1) {
		this.build1 = build1;
	}

	public String getBot2() {
		return bot2;
	}

	public void setBot2(String bot2) {
		this.bot2 = bot2;
	}

	public String getVersion2() {
		return version2;
	}

	public void setVersion2(String version2) {
		this.version2 = version2;
	}

	public String getBuild2() {
		return build2;
	}

	public void setBuild2(String build2) {
		this.build2 = build2;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReplay() {
		return replay;
	}

	public void setReplay(String replay) {
		this.replay = replay;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

}
