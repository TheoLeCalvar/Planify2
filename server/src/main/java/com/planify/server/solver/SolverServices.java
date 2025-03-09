package com.planify.server.solver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.planify.server.service.AntecedenceService;
import com.planify.server.service.CalendarService;
import com.planify.server.service.DayService;
import com.planify.server.service.GlobalUnavailabilityService;
import com.planify.server.service.LessonService;
import com.planify.server.service.PlanningService;
import com.planify.server.service.SequencingService;
import com.planify.server.service.SlotService;
import com.planify.server.service.SynchronizationService;
import com.planify.server.service.TAFService;
import com.planify.server.service.UserService;
/**
 * Class component to access to the different services of the backend from SolverExecutor and SolverMain.
 * @author Nathan RABIER
 */
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
	
	@Lazy
	@Autowired
	private SequencingService sequencingService;
	
	@Lazy
	@Autowired
	private SlotService slotService;
	
	@Lazy
	@Autowired
	private AntecedenceService antecedenceService;
	
	@Lazy
	@Autowired
	private UserService userService;
	
	@Lazy
	@Autowired
	private PlanningService planningService;

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
	
	public SequencingService getSequencingService() {
		return sequencingService;
	}
	
	public SlotService getSlotService() {
		return slotService;
	}

	public AntecedenceService getAntecedenceService() {
		return antecedenceService;
	}

	public UserService getUserService() {
		return userService;
	}

	public PlanningService getPlanningService() {
		return planningService;
	}
}
