package net.tyrai.asgardbackend.monitoringevent.api;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.tyrai.asgardbackend.monitoringevent.api.data.EventRequest;
import net.tyrai.asgardbackend.monitoringevent.repository.Event;
import net.tyrai.asgardbackend.monitoringevent.repository.EventRepository;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

	@Autowired
	EventRepository eventRepository;
	/**
	 * Register event.
	 * @throws Exception
	 */
	@PostMapping("")
	public void createEvent(@Valid @RequestBody EventRequest eventRequest) {
		Event event = new Event();
		event.setTitle(eventRequest.getTitle());
		event.setMessage(eventRequest.getMessage());
		event.setCode(eventRequest.getCode());
		event.setTrace(eventRequest.getTrace());
		event.setOrigin(eventRequest.getOrigin());
		event.setCreatedDate(new Date());
		eventRepository.save(event);
	}

	public void createEvent(String title, String message, String code, String trace, String origin) {
		Event event = new Event();
		event.setTitle(title);
		event.setMessage(message);
		event.setCode(code);
		event.setTrace(trace);
		event.setOrigin(origin);
		event.setCreatedDate(new Date());
		eventRepository.save(event);
		
	}
}
