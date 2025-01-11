package com.planify.server.solver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.planify.server.service.CalendarService;
import com.planify.server.service.DayService;
import com.planify.server.service.GlobalUnavailabilityService;
import com.planify.server.service.LessonService;
import com.planify.server.service.SynchronizationService;
import com.planify.server.service.TAFService;

@Component
public class SolverServices {
	@Lazy
    @Autowired
	private CalendarService calendarService;
	
	@Lazy
    @Autowired
	private TAFService tafService;
	
	@Lazy
    @Autowired
	private LessonService lessonService;
	
	@Lazy
	@Autowired
	private GlobalUnavailabilityService globalUnavailabilityService;
	
	@Lazy
	@Autowired
	private SynchronizationService synchronizationService;
	
	@Lazy
	@Autowired
	private DayService dayService;

	public SolverServices() {}
	
	public CalendarService getCalendarService() {
		return calendarService;
	}

	public TAFService getTafService() {
		return tafService;
	}

	public LessonService getLessonService() {
		return lessonService;
	}
	
	public GlobalUnavailabilityService getGlobalUnavailabilityService() {
		return globalUnavailabilityService;
	}

	public SynchronizationService getSynchronizationService() {
		return synchronizationService;
	}
	
	public DayService getDayService() {
		return dayService;
	}
}
