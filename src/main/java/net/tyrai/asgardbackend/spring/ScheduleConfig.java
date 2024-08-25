package net.tyrai.asgardbackend.spring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import net.tyrai.asgardbackend.matchrunner.repository.Bot;
import net.tyrai.asgardbackend.matchrunner.repository.BotRepository;
import net.tyrai.asgardbackend.matchrunner.repository.Match;
import net.tyrai.asgardbackend.matchrunner.repository.MatchRepository;

@Configuration
@EnableAsync
@EnableScheduling
public class ScheduleConfig {

	@Value("${local-play-bootstrap-folder}")
	private String localPlayBootstrapFolder;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BotRepository botRepository;

	@Scheduled(cron = "0/5 * * * * *")
	public void runMatches() {
		List<Match> inProgress = matchRepository.findAllByStatusOrderByCreatedDateAsc(Match.InProgress);
		if (inProgress != null && inProgress.size() >= 1)
			return;
		List<Match> createdMatches = matchRepository.findAllByStatusOrderByCreatedDateAsc(Match.Created);
		if (createdMatches == null || createdMatches.size() == 0)
			return;
		Match next = createdMatches.get(0);
		next.setStatus(Match.InProgress);
		next.setStartedDate(new Date());
		matchRepository.save(next);
		try {
		FileUtil.deleteContents(new java.io.File(localPlayBootstrapFolder + "replays"));
		Bot bot1 = botRepository.findByName(next.getBot1());
		Bot bot2 = botRepository.findByName(next.getBot2());
		String matchesContent = "1," + next.getBot1() + ",T," + bot1.getType() + ",2," + next.getBot2()
				+ ("Tyr".equals(next.getBot2()) ? "2" : "") + "," + getRaceCode(bot2) + "," + bot2.getType()
				+ "," + next.getMap();
		List<String> lines = new ArrayList<>();
		lines.add(matchesContent);
		Files.write(Paths.get(localPlayBootstrapFolder + "matches"), lines, Charset.defaultCharset());
		deleteDirectory(new java.io.File(localPlayBootstrapFolder + "bots/Tyr"));
		deleteDirectory(new java.io.File(localPlayBootstrapFolder + "bots/" + bot2.getName()));
		String bot1SourceDir = "bots/" + bot1.getName() + "/";
		String bot1TargetDir = localPlayBootstrapFolder + "bots/" + bot1.getName() + "/";
		copyDirectory(bot1SourceDir + next.getVersion1(), bot1TargetDir);
		if (next.getBuild1() != null && !"None".equals(next.getBuild1())) {
			FileUtil.copyFile(new java.io.File(bot1SourceDir + "buildSelection.py"),
					new java.io.File(bot1TargetDir + "buildSelection.py"));
			ProcessBuilder pb = new ProcessBuilder("python3", "buildSelection.py", next.getBuild1());
			pb.directory(new java.io.File(bot1TargetDir));
			Process process = pb.start();
			process.waitFor();
		}
		String bot2SourceDir = "bots/" + bot2.getName() + "/";
		String bot2TargetDir;
		if ("Tyr".equals(bot2.getName()))
			bot2TargetDir = localPlayBootstrapFolder + "bots/Tyr2/";
		else
			bot2TargetDir = localPlayBootstrapFolder + "bots/" + bot2.getName() + "/";
		copyDirectory(bot2SourceDir + next.getVersion2(), bot2TargetDir);
		if ("Tyr".equals(next.getBot2())) {
			Files.move(Paths.get(bot2TargetDir + "Tyr.dll"), Paths.get(bot2TargetDir + "Tyr2.dll"),
					StandardCopyOption.REPLACE_EXISTING);
			Files.move(Paths.get(bot2TargetDir + "Tyr.deps.json"), Paths.get(bot2TargetDir + "Tyr2.deps.json"),
					StandardCopyOption.REPLACE_EXISTING);
			Files.move(Paths.get(bot2TargetDir + "Tyr.runtimeconfig.json"),
					Paths.get(bot2TargetDir + "Tyr2.runtimeconfig.json"), StandardCopyOption.REPLACE_EXISTING);
		}
		if (next.getBuild2() != null && !"None".equals(next.getBuild2())) {
			FileUtil.copyFile(new java.io.File(bot2SourceDir + "buildSelection.py"),
					new java.io.File(bot2TargetDir + "buildSelection.py"));
			ProcessBuilder pb = new ProcessBuilder("python3", "buildSelection.py", next.getBuild2());
			pb.directory(new java.io.File(bot2TargetDir));
			Process process = pb.start();
			process.waitFor();
		}

			ProcessBuilder pb = new ProcessBuilder("docker", "compose", "-f", "docker-compose-host-network.yml", "up");
			pb.inheritIO();
			pb.directory(new java.io.File(localPlayBootstrapFolder));
			// Process process = Runtime.getRuntime()
			// .exec("/bin/sh -c docker-compose up", new String[0], new
			// java.io.File(localPlayBootstrapFolder));

			Process process = pb.start();
			new BufferedReader(new InputStreamReader(process.getInputStream())).lines().forEach(System.out::println);
			process.waitFor();
			System.out.println("Finished waiting for process");
			next.setStatus(Match.Done);
			String result = Files.readString(Paths.get(localPlayBootstrapFolder + "results.json"));
			int resultPos = result.lastIndexOf(" \"type\": ");
			if (resultPos > 0) {
				String playerWin = result.substring(resultPos + 10, resultPos + 20);
				if (playerWin.equals("Player1Win"))
					next.setResult(Match.Win);
				else if (playerWin.equals("Player2Win"))
					next.setResult(Match.Loss);
				else if ("InitializationError".equals(result.substring(resultPos + 12, resultPos + 31)))
					next.setResult(Match.Error);
				else
					next.setResult(Match.Tie);
			} else
				next.setResult(Match.Tie);
			File replayFolder = new java.io.File(localPlayBootstrapFolder + "replays");
			File[] replays = replayFolder.listFiles();
			if (replays.length > 0) {
				String replayPath = "results/" + next.getId() + "/" + next.getId() + "_" + next.getBot1() + "_"
						+ next.getBot2() + ".SC2Replay";
				FileUtils.moveFile(replays[0], new java.io.File(replayPath));
				next.setReplay(replayPath);
			}
			next.setFinishedDate(new Date());
			matchRepository.save(next);
			System.out.println("Finished registering game result");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			try {
				next.setResult(Match.Error);
				matchRepository.save(next);
			} catch (Exception e2) {

			}
			return;
		}
	}

	private String getRaceCode(Bot bot) {
		if (bot.getRace().equals("Any"))
			return "R";
		return bot.getRace().substring(0, 1);
	}

	public static void deleteDirectory(File directory) {
		if (!directory.exists())
			return;
		for (File file : Objects.requireNonNull(directory.listFiles())) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}
	}

	public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
			throws IOException {
		File sourceDirectory = new File(sourceDirectoryLocation);
		File destinationDirectory = new File(destinationDirectoryLocation);
		FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
	}
}
