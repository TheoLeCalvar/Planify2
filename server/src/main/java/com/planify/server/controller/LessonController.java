package com.planify.server.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planify.server.models.TAF;
import com.planify.server.service.AntecedenceService;
import com.planify.server.service.BlockService;
import com.planify.server.service.LessonService;
import com.planify.server.service.SequencingService;
import com.planify.server.service.SynchronizationService;
import com.planify.server.service.TAFService;
import com.planify.server.service.UEService;

@RestController
@RequestMapping("/api")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private UEService ueService;

    @Autowired
    private TAFService tafService;

    @Autowired
    private AntecedenceService antecedenceService;

    @Autowired
    private SequencingService sequencingService;

    @Autowired
    private SynchronizationService synchronizationService;

    @Autowired
    private BlockService blockService;

    @GetMapping("/taf/{idTAF}")
    public ResponseEntity<?> getTAFById(@PathVariable Long idTAF) {
        Optional<TAF> taf = tafService.findById(idTAF);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        return ResponseEntity.ok(tafService.findById(idTAF).get());
    }

}
