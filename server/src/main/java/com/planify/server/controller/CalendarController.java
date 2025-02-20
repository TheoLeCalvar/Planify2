package com.planify.server.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.planify.server.controller.returnsClass.Config;
import com.planify.server.controller.returnsClass.PlanningReturn;
import com.planify.server.models.*;
import com.planify.server.service.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.planify.server.service.TAFService;
import com.planify.server.solver.SolverExecutor;

@RestController
@RequestMapping("/api")
public class CalendarController {

    @Autowired
    private TAFService tafService;

    @Autowired
    private PlanningService planningService;

    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";

    @GetMapping(value = "/solver/run/{configId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runSolverMain(@PathVariable Long configId) {
        /*Optional<Planning> planning = planningService.findById(configId);
        if (planning.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No Planning with this id was found", 404));
        }
        Planning realPlanning = planning.get();*/

        // A supprimer
        TAF taf = tafService.findById(configId).orElseThrow(() -> new IllegalArgumentException("TAF not found"));
        Calendar calendar = taf.getCalendars().getFirst();
        Planning realPlanning = planningService.addPlanning(calendar);
        // Fin de à supprimer
        
        SolverExecutor.generatePlanning(realPlanning);
        
        return ResponseEntity.ok("The solver is launched ! (PlanningId : " + realPlanning.getId() + ")");
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
                        answer.add(new PlanningReturn(planning.getId(), formatted, planning.getName()));
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

    @GetMapping(value = "/config/{configId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getConfig(@PathVariable Long configId) {
        Optional<Planning> optionalPlanning = planningService.findById(configId);
        if (optionalPlanning.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No planning with this id was found", 404));
        }
        Planning planning = optionalPlanning.get();

        Config config = new Config(planning);

        return ResponseEntity.ok(config);

    }

    @PatchMapping("/config/{configId}")
    public ResponseEntity<?> updateConfig(@PathVariable Long configId, @RequestBody Config config) {
        Optional<Planning> optionalPlanning = planningService.findById(configId);
        if (optionalPlanning.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No planning with this id was found", 404));
        }
        Planning planning = optionalPlanning.get();

        planning.updateConfig(config);
        planningService.save(planning);

        return ResponseEntity.ok(new Config(planning));

    }

    @DeleteMapping(value = "/config/{configId}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long configId) {
        if (planningService.existById(configId)) {
            planningService.delete(configId);
            ResponseEntity.ok("Planning deleted !");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("No planning with this id was found", 404));
    }

}
