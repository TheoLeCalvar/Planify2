package com.planify.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.planify.server.controller.returnsClass.BlockShort;
import com.planify.server.controller.returnsClass.LessonShort;
import com.planify.server.controller.returnsClass.TAFReturn;
import com.planify.server.controller.returnsClass.TAFShort;
import com.planify.server.controller.returnsClass.UEShort;
import com.planify.server.models.Block;
import com.planify.server.models.Calendar;
import com.planify.server.models.Lesson;
import com.planify.server.models.Sequencing;
import com.planify.server.models.TAF;
import com.planify.server.models.UE;
import com.planify.server.models.User;
import com.planify.server.service.AntecedenceService;
import com.planify.server.service.BlockService;
import com.planify.server.service.LessonLecturerService;
import com.planify.server.service.LessonService;
import com.planify.server.service.SequencingService;
import com.planify.server.service.SynchronizationService;
import com.planify.server.service.TAFService;
import com.planify.server.service.UEService;
import com.planify.server.service.UserService;

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

    @Autowired
    private LessonLecturerService lessonLecturerService;

    @Autowired
    private UserService userService;

    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";

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
                realTaf.getTafManagers().stream().map(manager -> manager.getUser().getFullName())
                        .collect(Collectors.toList()),
                realTaf.getBeginDate(),
                realTaf.getEndDate());
        System.out.println(tafReturn.toString());
        return ResponseEntity.ok(tafReturn);
    }

    // Add the lessons to the UE
    @PutMapping(value = "/ue/{ueId}/lesson", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putLessonInUE(@PathVariable Long ueId, @RequestBody List<BlockShort> blocks) {
        // Check if the UE exists
        if (!ueService.existsById(ueId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("UE not found", 404));
        }

        // Delete the previous lessons of the UE
        List<Lesson> oldLessons = lessonService.findByUE();

        // HERRRRRRE !!!!!!!!

        // Add the lessons and their impacts
        if (blocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("The body is empty", 404));
        } else {
            if (ueService.existsById(ueId)) {
                // Get the blocks' UE
                UE ue = ueService.findById(ueId).get();

                // Stock the last lesson of each block so that we can order the lessons later
                Map<Long, Lesson> lastLessonOfBlocks = new HashMap<>();

                for (BlockShort block : blocks) {

                    // Put the lessons of the block in the DB
                    List<Lesson> reaLessons = new ArrayList<Lesson>();
                    for (LessonShort lesson : block.getLessons()) {
                        Lesson realLesson = lessonService.add(lesson.getTitle(), lesson.getDescription(), ue);
                        reaLessons.add(realLesson);

                        // Save the lecturers of the lesson
                        if (!(lesson.getLecturers() == null)) {
                            for (Long lecturerId : lesson.getLecturers()) {
                                Optional<User> user = userService.findById(lecturerId);
                                if (user.isEmpty()) {
                                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                            .body(new ErrorResponse("The user " + Long.toString(lecturerId)
                                                    + " is not found. (Lecturer of lesson: " + lesson.getTitle() + ")",
                                                    404));
                                }
                                lessonLecturerService.addLessonLecturer(user.get(), realLesson);
                            }
                        }

                    }

                    System.out.println(GREEN + "Lessons of block " + block.getTitle() + " have been added" + RESET);

                    // Add the block to the DB
                    blockService.addBlock(block.getTitle(), reaLessons.get(0), block.getDescription());

                    System.out.println(GREEN + "Block " + block.getTitle() + " has been added" + RESET);

                    // Add the antecedence for lesson in two differents block
                    for (Long anteriorBlock : block.getDependencies()) {
                        Lesson anteriorLesson = lastLessonOfBlocks.get(anteriorBlock);
                        antecedenceService.addAntecedence(anteriorLesson, reaLessons.get(0));
                        System.out.println(GREEN + "Antecedence of Lessons between " + Long.toString(anteriorBlock)
                                + " and " + block.getTitle() + " has been added" + RESET);
                    }

                    // Add the antecedence and the sequencing for the lessons in a same block
                    for (int i = 0; i < reaLessons.size() - 1; i++) {
                        antecedenceService.addAntecedence(reaLessons.get(i), reaLessons.get(i + 1));
                        sequencingService.add(reaLessons.get(i), reaLessons.get(i + 1));
                    }

                    System.out.println(GREEN + "Antecedence and Sequencing in the " + block.getTitle()
                            + " have been added" + RESET);

                    lastLessonOfBlocks.put(block.getId(), reaLessons.get(reaLessons.size() - 1));
                }

                return ResponseEntity.ok(blocks);

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("No UE with this id was found", 404));
            }

        }
    }

    // Data on a given UE (id)
    @GetMapping(value = "/ue/{ueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUEById(@PathVariable Long ueId) {
        Optional<UE> ue = ueService.findById(ueId);
        if (ue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No UE with this id was found", 404));
        }
        UE realUe = ue.get();
        UEShort ueReturn = new UEShort(realUe);
        System.out.println(realUe.toString());
        return ResponseEntity.ok(ueReturn);
    }

    // Lessons for a given UE (ID)
    @GetMapping(value = "/ue/{ueId}/lesson", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUELessonsById(@PathVariable Long ueId) {
        Optional<UE> ue = ueService.findById(ueId);
        if (ue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No UE with this id was found", 404));
        }
        UE realUe = ue.get();

        // Find blocks linked to lesson to this UE
        // for each block find all lessons linked thanks to antecedences
        // create blockshort and lesson short each time

        List<Lesson> lessons = realUe.getLessons();
        List<Block> blocks = blockService.findAll().stream()
                .filter(block -> lessons.contains(block.getFirstLesson()))
                .collect(Collectors.toList());
        List<BlockShort> blockShorts = new ArrayList<>();
        List<Long> blockDependenciesSave = new ArrayList<>();

        for (Block block : blocks) {
            List<Long> blockDependencies = new ArrayList<>();
            blockDependencies.addAll(blockDependenciesSave);
            Lesson currentLesson = block.getFirstLesson();
            List<LessonShort> lessonShorts = new ArrayList<>();
            LessonShort currentLessonShort = new LessonShort(
                    currentLesson.getId(),
                    currentLesson.getName(),
                    currentLesson.getDescription(),
                    currentLesson.getLessonLecturers().stream().map(lecturer -> lecturer.getId().getIdUser())
                            .collect(Collectors.toList()));
            lessonShorts.add(currentLessonShort);
            while (!currentLesson.getSequencingsAsPrevious().isEmpty()) {
                // Adding lesson to list until there are no more lessons in this block
                Sequencing sequencing = currentLesson.getSequencingsAsPrevious().getFirst();
                currentLesson = sequencing.getNextLesson();
                currentLessonShort = new LessonShort(
                        currentLesson.getId(),
                        currentLesson.getName(),
                        currentLesson.getDescription(),
                        currentLesson.getLessonLecturers().stream().map(lecturer -> lecturer.getId().getIdUser())
                                .collect(Collectors.toList()));
                lessonShorts.add(currentLessonShort);
            }

            System.out.println("BLOCK LESSON :" + block.getId() + lessonShorts);
            // Adding Block to list
            blockDependencies.add(block.getId());
            blockDependenciesSave = blockDependencies;
            System.out.println("DEPENDENCIES :" + blockDependencies);
            BlockShort blockShort = new BlockShort(
                    block.getId(),
                    block.getTitle(),
                    block.getDescription(),
                    lessonShorts,
                    blockDependencies);
            blockShorts.add(blockShort);
        }

        System.out.println(blockShorts.toString());
        return ResponseEntity.ok(blockShorts);

    }

    // Modify an UE
    @PutMapping(value = "/ue/{ueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyUE(@PathVariable Long ueId, @RequestBody UEShort newUE) {
        Optional<UE> oue = ueService.findById(ueId);
        if (oue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No UE with this id was found", 404));
        }

        UE ue = oue.get();
        ue.setName(newUE.getName());
        ue.setDescription(newUE.getDescription());
        ueService.save(ue);
        return ResponseEntity.ok(ue);
    }

}
