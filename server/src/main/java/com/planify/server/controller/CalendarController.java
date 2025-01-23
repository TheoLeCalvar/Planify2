package com.planify.server.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.planify.server.controller.returnsClass.PlanningReturn;
import com.planify.server.models.Planning;
import com.planify.server.models.ScheduledLesson;
import com.planify.server.service.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planify.server.controller.returnsClass.TAFShort;
import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;
import com.planify.server.service.CalendarService;
import com.planify.server.service.TAFService;
import com.planify.server.solver.SolverMain;
import com.planify.server.solver.SolverServices;

@RestController
@RequestMapping("/api")
public class CalendarController {

    @Autowired
    private TAFService tafService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private PlanningService planningService;

    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";

    @GetMapping(value = "/solver/run/{idTaf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runSolverMain(@PathVariable Long idTaf) {
        Optional<TAF> taf = tafService.findById(idTaf);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        TAF realTaf = taf.get();

        Calendar calendar = realTaf.getCalendars().getFirst();
        Planning planning = calendar.getPlannings().getFirst();

        String result = SolverMain.generatePlanningString(planning);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/solver/history/{idTaf}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> getCalendarHistory(@PathVariable Long idTaf) {
        Optional<TAF> taf = tafService.findById(idTaf);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        List<PlanningReturn> answer = new ArrayList<PlanningReturn>();
        List<Calendar> calendars = taf.get().getCalendars();
        if (calendars!=null && !calendars.isEmpty()) {
            for (Calendar calendar : calendars) {
                List<Planning> plannings = planningService.findByCalendar(calendar);
                if (plannings!=null) {
                    for (Planning planning : plannings) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatted = planning.getTimestamp().format(formatter);
                        answer.add(new PlanningReturn(planning.getId(), formatted));
                    }
                }
            }
        }
        return ResponseEntity.ok(answer);

    }

    @GetMapping(value = "/solver/result/{planningId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPlanning(@PathVariable Long planningId) {
        Optional<Planning> optionalPlanning = planningService.findById(planningId);
        if (optionalPlanning.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No planning with this id was found", 404));
        }
        List<ScheduledLesson> scheduledLessons = optionalPlanning.get().getScheduledLessons();
        return ResponseEntity.ok(scheduledLessons);
    }

}
