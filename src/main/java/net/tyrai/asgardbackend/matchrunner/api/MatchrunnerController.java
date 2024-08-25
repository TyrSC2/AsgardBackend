package net.tyrai.asgardbackend.matchrunner.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.tyrai.asgardbackend.matchrunner.api.data.BotDetails;
import net.tyrai.asgardbackend.matchrunner.api.data.CreateBot;
import net.tyrai.asgardbackend.matchrunner.api.data.CreateBuild;
import net.tyrai.asgardbackend.matchrunner.api.data.CreateMatch;
import net.tyrai.asgardbackend.matchrunner.api.data.CreateVersion;
import net.tyrai.asgardbackend.matchrunner.repository.Bot;
import net.tyrai.asgardbackend.matchrunner.repository.BotRepository;
import net.tyrai.asgardbackend.matchrunner.repository.BotVersion;
import net.tyrai.asgardbackend.matchrunner.repository.BotVersionRepository;
import net.tyrai.asgardbackend.matchrunner.repository.Build;
import net.tyrai.asgardbackend.matchrunner.repository.BuildRepository;
import net.tyrai.asgardbackend.matchrunner.repository.Match;
import net.tyrai.asgardbackend.matchrunner.repository.MatchRepository;

@RestController
@RequestMapping("/matchrunner")
public class MatchrunnerController {
	@Value("${local-play-bootstrap-folder}")
	private String localPlayBootstrapFolder;

	@Autowired
	BotRepository botRepository;

	@Autowired
	BotVersionRepository botVersionRepository;

	@Autowired
	BuildRepository buildRepository;
	
	@Autowired
	MatchRepository matchRepository;

	Random random = new Random();

	private String[] mapPool = new String[] {
		  "Equilibrium513AIE.SC2Map",
		  "GoldenAura513AIE.SC2Map",
		  "Gresvan513AIE.SC2Map",
		  "HardLead513AIE.SC2Map",
		  "Oceanborn513AIE.SC2Map",
		  "SiteDelta513AIE.SC2Map"
	 };
	// Ketroc 2023S1 compatible maps 
	/*
	private String[] mapPool = new String[] {
			  "WaterfallAIE.SC2Map",
			  "BerlingradAIE.SC2Map",
			  "StargazersAIE.SC2Map",
			  "MoondanceAIE.SC2Map",
		 };
		 */

	/**
	 * Register event.
	 * @throws Exception
	 */
	@GetMapping("bots")
	public List<Bot> getBots() {
		return botRepository.findAll();
	}

	@GetMapping("bots/{name}")
	public BotDetails getBot(@PathVariable(value = "name")String name) {
		BotDetails result = new BotDetails();
		
		Bot bot = botRepository.findByName(name);
		result.setId(bot.getId());
		result.setName(bot.getName());
		result.setRace(bot.getRace());
		result.setType(bot.getType());
		result.setCreatedDate(bot.getCreatedDate());
		result.setVersions(botVersionRepository.findByBot(name));
		result.setBuilds(buildRepository.findByBot(name));
		
		return result;
	}
	@PostMapping("bots")
	public void createBot(@RequestBody CreateBot request) {
		Bot bot = new Bot();
		bot.setCreatedDate(new Date());
		bot.setName(request.getName());
		bot.setRace(request.getRace());
		bot.setType(request.getType());
		botRepository.save(bot);
	}
	@PostMapping(value = "bots/{name}/version", consumes = {"multipart/form-data"})
	public void createVersion(@PathVariable(value = "name")String name, @RequestParam(value="version") String version, @RequestParam(value="file", required=false) MultipartFile file) throws IOException {
		BotVersion botVersion = new BotVersion();
		botVersion.setBot(name);
		botVersion.setName(version);
		botVersion.setCreatedDate(new Date());
		if (file != null) {
		    File targetFile = new File("tmp/" + name + "_" + version + "/bot.zip");
		    FileUtils.createParentDirectories(targetFile);
		    OutputStream outStream = new FileOutputStream(targetFile);
		    outStream.write(file.getBytes());
		    outStream.close();
		    
	        File destDir = new File("bots/" + name + "/" + version);

	        byte[] buffer = new byte[1024];
	        ZipInputStream zis = new ZipInputStream(new FileInputStream(targetFile));
	        ZipEntry zipEntry = zis.getNextEntry();
	        while (zipEntry != null) {
	            File newFile = newFile(destDir, zipEntry);
	            if (zipEntry.isDirectory()) {
	                if (!newFile.isDirectory() && !newFile.mkdirs()) {
	                    throw new IOException("Failed to create directory " + newFile);
	                }
	            } else {
	                // fix for Windows-created archives
	                File parent = newFile.getParentFile();
	                if (!parent.isDirectory() && !parent.mkdirs()) {
	                    throw new IOException("Failed to create directory " + parent);
	                }

	                // write file content
	                FileOutputStream fos = new FileOutputStream(newFile);
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                    fos.write(buffer, 0, len);
	                }
	                fos.close();
	            }
	            zipEntry = zis.getNextEntry();
	        }

	        zis.closeEntry();
	        zis.close();

		}
		botVersionRepository.save(botVersion);
	}
	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}

	@PostMapping("bots/{name}/build")
	public void createBuild(@PathVariable(value = "name")String name, @RequestBody CreateBuild request) {
		Build build = new Build();
		build.setBot(name);
		build.setName(request.getName());
		build.setCreatedDate(new Date());
		buildRepository.save(build);
	}
	
	@PostMapping("matches")
	public void scheduleMatch(@RequestBody CreateMatch request) {
		Match match = new Match();
		match.setBot1("Tyr");
		match.setVersion1(request.getTestVersion());
		if (request.getTestBuild() != null)
			match.setBuild1(request.getTestBuild());
		match.setBot2(request.getOpponentName());
		match.setVersion2(request.getOpponentVersion());
		if (request.getOpponentBuild() != null)
			match.setBuild2(request.getOpponentBuild());
		match.setCreatedDate(new Date());
		match.setStatus(Match.Created);
		match.setMap(mapPool[random.nextInt(mapPool.length)]);
		matchRepository.save(match);
	}
	@GetMapping("matches")
	public List<Match> getMatches() {
		return matchRepository.findAllByOrderByCreatedDateDesc();
	}
	@GetMapping("matches/{id}/replay")
	public ResponseEntity<byte[]> getReplay(@PathVariable(value = "id") long id, HttpServletResponse response) throws IOException {
		Optional<Match> match = matchRepository.findById(id);
		if (match.isEmpty() ||  match.get().getReplay() == null)
			throw new FileNotFoundException();
		java.io.File file = new java.io.File(match.get().getReplay());
		FileInputStream fis = new FileInputStream(file);
	    ResponseEntity<byte[]> result = ResponseEntity.ok()
	    	      .contentType(MediaType.APPLICATION_OCTET_STREAM)
	    	      .body(fis.readAllBytes());
	    response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
	    fis.close();
	    return result;
	}
}
