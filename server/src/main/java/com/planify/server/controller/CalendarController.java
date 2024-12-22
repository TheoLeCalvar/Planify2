package com.planify.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @GetMapping(value = "/solver/run/{idTaf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runSolverMain(@PathVariable Long idTaf) {
        Optional<TAF> taf = tafService.findById(idTaf);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        TAF realTaf = taf.get();

        Calendar calendar = realTaf.getCalendars().getFirst();

        String result = SolverMain.generateCal(calendar);

        return ResponseEntity.ok(result);
    }

}
