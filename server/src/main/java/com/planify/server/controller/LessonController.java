package com.planify.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.planify.server.controller.returnsClass.TAFReturn;
import com.planify.server.controller.returnsClass.TAFShort;
import com.planify.server.controller.returnsClass.UEShort;
import com.planify.server.models.Calendar;
import com.planify.server.models.TAF;
import com.planify.server.models.TAFManager;
import com.planify.server.models.UE;
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

    // Get the list of TAF
    @GetMapping(value = "/taf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTAFs() {
        List<TAF> tafs = tafService.findAll();
        List<TAFShort> answer = new ArrayList<TAFShort>();
        for (TAF taf : tafs) {
            answer.add(new TAFShort(taf.getId(), taf.getName(), taf.getDescription()));
        }
        return ResponseEntity.ok(answer);
    }

    // Data on a given TAF (id)
    @GetMapping(value = "/taf/{idTAF}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTAFById(@PathVariable Long idTAF) {
        Optional<TAF> taf = tafService.findById(idTAF);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        TAF realTaf = taf.get();
        TAFReturn tafReturn = new TAFReturn(
                realTaf.getId(),
                realTaf.getName(),
                realTaf.getDescription(),
                realTaf.getUes().stream().map(ue -> new UEShort(ue)).collect(Collectors.toList()),
                realTaf.getCalendars().stream().map(Calendar::getId).collect(Collectors.toList()),
                realTaf.getTafManagers().stream().map(TAFManager::getId).collect(Collectors.toList()),
                realTaf.getBeginDate(),
                realTaf.getEndDate());
        System.out.println(tafReturn.toString());
        return ResponseEntity.ok(tafReturn);
    }

}
