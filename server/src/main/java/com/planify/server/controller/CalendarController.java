package com.planify.server.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.planify.server.controller.returnsClass.CheckOK;
import com.planify.server.controller.returnsClass.Config;
import com.planify.server.controller.returnsClass.PlanningReturn;
import com.planify.server.controller.returnsClass.TAFSynchronised;
import com.planify.server.models.*;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
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
    public ResponseEntity<?> runSolverMain(@PathVariable Long configId, @RequestBody Config config) {
        Optional<Planning> planning = planningService.findById(configId);
        if (planning.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No Planning with this id was found", 404));
        }
        Planning realPlanning = planning.get();

        for (Config.CSyncrho cSyncrho : config.getConstraintsSynchronisation()) {
            Planning otherPlanning = planningService.findById(cSyncrho.getOtherPlanning()).orElseThrow(()-> new IllegalArgumentException("The other planning does nott exist"));
            realPlanning.addConstraintSynchroniseWithTAF(new ConstraintSynchroniseWithTAF(realPlanning, otherPlanning, cSyncrho.isEnabled(), cSyncrho.isGenerateOtherPlanning() ));
        }

        Calendar calendar = realPlanning.getCalendar();
        TAF taf = calendar.getTaf();

        if (taf.getCalendars().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No calendar", 409));
        }

        if (calendar.getSlots().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No slots", 409));
        }
        if (taf.getUes().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No UE", 409));
        }
        for (UE ue : taf.getUes()) {
            if (ue.getLessons().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("No lessons in UE", 409));
            }
        }
        
        SolverExecutor.generatePlanning(realPlanning);
        
        return ResponseEntity.ok("The solver is launched ! (PlanningId : " + realPlanning.getId() + ")");
    }

    @GetMapping(value = "/solver/check/{configId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkSolverMain(@PathVariable Long configId) {
        Optional<Planning> oPlanning = planningService.findById(configId);
        if (oPlanning.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No Planning with this id was found", 404));
        }
        Planning planning = oPlanning.get();

        Calendar calendar = planning.getCalendar();
        TAF taf = calendar.getTaf();
        System.out.println("Calendrier de la TAF: " + taf.getCalendars().toString());
        List<Calendar> CS = taf.getCalendars();
        for (Calendar c : CS) {
            System.out.println("Calendrier de la TAF: " + c.toString());
            System.out.println("Slots du calendrier: " + c.getSlots());
        }

        if (calendar.getSlots().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No slots", 409));
        }

        if (taf.getCalendars().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No calendar", 409));
        }

        if (taf.getUes().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("No UE", 409));
        }
        List<LessonLecturer> lessonLecturers = new ArrayList<>();
        List<Lesson> lessons = new ArrayList<>();
        for (UE ue : taf.getUes()) {
            if (ue.getLessons().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("No lessons in UE", 409));
            }
            lessons.addAll(ue.getLessons());
        }
        for (Lesson lesson : lessons) {
            lessonLecturers.addAll(lesson.getLessonLecturers());
        }
        /*for (LessonLecturer lecturer : lessonLecturers) {
            if (lecturer.getUser().getLastUpdatedAvailability() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Lecturer availability not filled", 409));
            }
        }
*/
        // Find the TAF with which there is a synchronisation
        List<Lesson> allLessons = taf.getUes().stream()
                .flatMap(ue -> ue.getLessons().stream())
                .toList();
        List<TAF> tafs = allLessons.stream()
                .flatMap(lesson -> lesson.synchronisedWith().stream())
                .toList();
        List<TAFSynchronised> tafSynchroniseds = new ArrayList<>();
        if (tafs != null && !tafs.isEmpty()) {
            for (TAF sTaf : tafs) {
                List<PlanningReturn> returns = new ArrayList<>();
                List<Planning> plannings = sTaf.getCalendars().getFirst().getPlannings();
                for (Planning p : plannings) {
                    PlanningReturn pr = new PlanningReturn(p.getId(), p.getName(), p.getTimestamp(), p.getStatus());
                    returns.add(pr);
                }
                TAFSynchronised tafSynchronised = new TAFSynchronised(sTaf.getId(), sTaf.getName(),returns);
                tafSynchroniseds.add(tafSynchronised);
            }

            CheckOK ok = new CheckOK(tafSynchroniseds);
            return ResponseEntity.ok(ok);
        }
        else {
            SolverExecutor.generatePlanning(planning);
            return ResponseEntity.ok("The solver is launched ! (PlanningId : " + planning.getId() + ")");
        }

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
                        answer.add(new PlanningReturn(planning.getId(), planning.getTimestamp(), planning.getName()));
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
            return ResponseEntity.ok("Planning deleted !");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("No planning with this id was found", 404));
    }

}
