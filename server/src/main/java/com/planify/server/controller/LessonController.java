package com.planify.server.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.planify.server.controller.returnsClass.*;
import com.planify.server.models.*;
import com.planify.server.models.Calendar;
import com.planify.server.models.Planning.Status;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.models.constraints.ConstraintsOfUE;
import com.planify.server.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

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

    @Autowired  
    private SlotService slotService;

    @Autowired
    private GlobalUnavailabilityService globalUnavailabilityService;

    @Autowired
    private DayService dayService;

    @Autowired
    private WeekService weekService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private PlanningService planningService;
    
    @Autowired
    private ConstraintsOfUEService constraintsOfUEService;
    
    @Autowired
    private ConstraintSynchroniseWithTAFService constraintSynchroniseWithTAFService;

    @Autowired
    private TAFManagerService tafManagerService;

    @Autowired
    private UEManagerService ueManagerService;

    @Autowired
    private UserUnavailabilityService userUnavailabilityService;


    // Get the list of TAF
    @Operation(summary = "Get the list of the TAFs of the user authentified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of the TAF",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TAFShort.class)))),
            @ApiResponse(responseCode = "401", description = "User not authentified",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/taf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTAFs() {
        // Retrieving user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required", 401));
        }

        String mail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByMail(mail);

        // If user not found, return error
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", 404));
        }

        User user = userOpt.get();

        // Retrieving related TAF of user
        List<TAF> relatedTafs = new ArrayList<TAF>();
        List<TAFManager> tafManagers = user.getTafManagers();
        List<UEManager> ueManagers = user.getUeManagers();
        List<LessonLecturer> lessonLecturers = user.getLessonLecturers();
        relatedTafs.addAll(tafManagers.stream().map(manager -> manager.getTaf()).collect(Collectors.toList()));
        relatedTafs.addAll(ueManagers.stream().map(manager -> manager.getUe().getTaf()).collect(Collectors.toList()));
        relatedTafs.addAll(lessonLecturers.stream().map(lecturer -> lecturer.getLesson().getUe().getTaf()).collect(Collectors.toList()));
        relatedTafs = relatedTafs.stream().distinct().collect(Collectors.toList());
        List<TAFShort> answer = new ArrayList<TAFShort>();
        for (TAF taf : relatedTafs) {
            answer.add(new TAFShort(taf.getId(), taf.getName(), taf.getDescription()));
        }
        return ResponseEntity.ok(answer);
    }

    // Get the list of TAF
    @Operation(summary = "Get the list of all the TAFs")
    @GetMapping(value = "/alltaf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TAFShort>> getAllTAFs() {
        List<TAF> tafs = tafService.findAll();
        List<TAFShort> answer = new ArrayList<TAFShort>();
        for (TAF taf : tafs) {
            answer.add(new TAFShort(taf.getId(), taf.getName(), taf.getDescription()));
        }
        return ResponseEntity.ok(answer);
    }

    // Data on a given TAF (id)
    @Operation(summary = "Get the detail about the TAF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Details of the TAF",
                    content = @Content(mediaType = "application/json",
                            schema  = @Schema(implementation  =  TAFReturn.class))),
            @ApiResponse(responseCode = "400", description = "No TAF with this id was found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/taf/{idTAF}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTAFById(@PathVariable Long idTAF) {
        Optional<TAF> taf = tafService.findById(idTAF);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        TAF realTaf = taf.get();
        List<PlanningReturn> resultPlanning = new ArrayList<PlanningReturn>();
        List<Calendar> calendars = realTaf.getCalendars();

        // Find the TAF with which there is a synchronisation
        List<Lesson> allLessons = realTaf.getUes().stream()
                .flatMap(ue -> ue.getLessons().stream())
                .toList();
        List<Long> tafsSynchronized = allLessons.stream()
                .flatMap(lesson -> lesson.synchronisedWith().stream().map(TAF::getId))
                .toList();

        if (calendars!=null && !calendars.isEmpty()) {
            for (Calendar calendar : calendars) {
                List<Planning> plannings = planningService.findByCalendar(calendar);
                if (plannings!=null) {
                    for (Planning planning : plannings) {
                        if (planning.getStatus() != Planning.Status.CONFIG) {
                            resultPlanning.add(new PlanningReturn(planning.getId(), planning.getName(), planning.getTimestamp(), planning.getStatus(), planning.isSolutionOptimal()));
                        }
                    }
                }
            }
        }
        TAFReturn tafReturn = new TAFReturn(
                realTaf.getId(),
                realTaf.getName(),
                realTaf.getDescription(),
                realTaf.getUes().stream().map(ue -> new UEShort(ue)).collect(Collectors.toList()),
                realTaf.getCalendars().stream().map(Calendar::getId).collect(Collectors.toList()),
                realTaf.getTafManagers().stream().map(manager -> new UserBrief(manager.getUser().getId(), manager.getUser().getFullName()))
                        .collect(Collectors.toList()),
                realTaf.getBeginDate(),
                realTaf.getEndDate(),
                resultPlanning,
                tafsSynchronized);
        System.out.println(tafReturn.toString());
        return ResponseEntity.ok(tafReturn);
    }

    // Add the lessons to the UE
    @Operation(summary = "Add the lessons to the UE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lessons added",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BlockShort.class)))),
            @ApiResponse(responseCode = "400", description = "No UE with this id was found or the body is empty",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/ue/{ueId}/lesson", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putLessonInUE(@PathVariable Long ueId, @RequestBody List<BlockShort> blocks) {
        // Check if the UE exists

        if (!ueService.existsById(ueId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("UE not found", 400));
        }

        // Get the blocks' UE
        UE ue = ueService.findById(ueId).get();

        List<Lesson> oldLessons = ue.getLessons();
        System.out.println(RED);
        System.out.println(oldLessons);
        System.out.println(RESET);
        if (!oldLessons.isEmpty()) {
            System.out.println("Dans if");
            boolean b = !oldLessons.isEmpty();
            while (b) {
                Lesson oldLesson = oldLessons.removeFirst();
                lessonService.delete(oldLesson.getId());
                b = !oldLessons.isEmpty();
            }
        }

        System.out.println(ue.getLessons());

        // Add the lessons and their impacts
        if (blocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("The body is empty", 400));
        } else {
            if (ueService.existsById(ueId)) {

                // Stock the last lesson of each block so that we can order the lessons later
                Map<Long, Lesson> lastLessonOfBlocks = new HashMap<>();

                for (BlockShort block : blocks) {

                    if (block.getLessons()!=null && !block.getLessons().isEmpty()) {

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
                                                .body("The user " + Long.toString(lecturerId)
                                                        + " is not found. (Lecturer of lesson: " + lesson.getTitle() + ")");
                                    }
                                    lessonLecturerService.addLessonLecturer(user.get(), realLesson);
                                }
                            }

                            // Add the synchronisation
                            System.out.println(GREEN + "Synchronisation:" + lesson.getSynchronise() + RESET);
                            if (lesson.getSynchronise()!=null && !lesson.getSynchronise().isEmpty()) {
                                for (LessonSynchronised s : lesson.getSynchronise()) {
                                    Lesson lesson2 = lessonService.findById(s.getId()).orElseThrow(() ->new IllegalArgumentException("The Lesson doesn't exist"));
                                    synchronizationService.addSynchronization(realLesson,lesson2);
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

                }

                return ResponseEntity.ok(blocks);

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("No UE with this id was found", 404));
            }

        }
    }

    // Data on a given UE (id)
    @Operation(summary = "Get the detail about the UE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Details of the UE",
                    content = @Content(mediaType = "application/json",
                            schema  = @Schema(implementation  =  UEShort.class))),
            @ApiResponse(responseCode = "400", description = "No UE with this id was found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/ue/{ueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUEById(@PathVariable Long ueId) {
        Optional<UE> ue = ueService.findById(ueId);
        if (ue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No UE with this id was found", 400));
        }
        UE realUe = ue.get();
        UEShort ueReturn = new UEShort(realUe);
        System.out.println(realUe.toString());
        return ResponseEntity.ok(ueReturn);
    }

    // Lessons for a given UE (ID)
    @Operation(summary = "Get the lessons to the UE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The lessons of the UE",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BlockShort.class)))),
            @ApiResponse(responseCode = "400", description = "No UE with this id was found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
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
        //List<Long> blockDependenciesSave = new ArrayList<>();

        for (Block block : blocks) {
            //List<Long> blockDependencies = new ArrayList<>();
            //blockDependencies.addAll(blockDependenciesSave);
            Lesson currentLesson = block.getFirstLesson();
            List<LessonShort> lessonShorts = new ArrayList<>();

            // Finding synchronization
            Long actualId1 = currentLesson.getId();
            List<Lesson> synchronised1 = Optional.ofNullable(currentLesson.getSynchronizations())
                    .orElse(Collections.emptyList())
                    .stream()
                    .flatMap(s -> Optional.ofNullable(s.getLessons()).orElse(Collections.emptyList()).stream()) // Évite null sur getLessonIds()
                    .distinct()
                    .filter(lesson -> !Objects.equals(lesson.getId(), actualId1))
                    .toList();

            List<LessonSynchronised> lessonSynchroniseds1 = synchronised1.stream().map(lesson -> new LessonSynchronised(lesson.getId(), lesson.getUe().getTaf().getName(), lesson.getUe().getName(), lesson.getName())).toList();

            LessonShort currentLessonShort = new LessonShort(
                    currentLesson.getId(),
                    currentLesson.getName(),
                    currentLesson.getDescription(),
                    currentLesson.getLessonLecturers().stream().map(lecturer -> lecturer.getId().getIdUser())
                            .collect(Collectors.toList()),
                    lessonSynchroniseds1
            );
            lessonShorts.add(currentLessonShort);
            while (!currentLesson.getSequencingsAsPrevious().isEmpty()) {
                // Adding lesson to list until there are no more lessons in this block
                Sequencing sequencing = currentLesson.getSequencingsAsPrevious().getFirst();
                currentLesson = sequencing.getNextLesson();
                // Finding synchronization
                Long actualId = currentLesson.getId();
                List<Lesson> synchronised = Optional.ofNullable(currentLesson.getSynchronizations())
                        .orElse(Collections.emptyList())
                        .stream()
                        .flatMap(s -> Optional.ofNullable(s.getLessons()).orElse(Collections.emptyList()).stream()) // Évite null sur getLessonIds()
                        .distinct()
                        .filter(lesson -> !Objects.equals(lesson.getId(), actualId))
                        .toList();

                List<LessonSynchronised> lessonSynchroniseds = synchronised.stream().map(lesson -> new LessonSynchronised(lesson.getId(), lesson.getUe().getTaf().getName(), lesson.getUe().getName(), lesson.getName())).toList();

                currentLessonShort = new LessonShort(
                        currentLesson.getId(),
                        currentLesson.getName(),
                        currentLesson.getDescription(),
                        currentLesson.getLessonLecturers().stream().map(lecturer -> lecturer.getId().getIdUser())
                                .collect(Collectors.toList()),
                        lessonSynchroniseds
                );
                lessonShorts.add(currentLessonShort);
            }

            System.out.println("BLOCK LESSON :" + block.getId() + lessonShorts);
            // Adding Block to list
            //blockDependencies.add(block.getId());
            //blockDependenciesSave = blockDependencies;
            //System.out.println("DEPENDENCIES :" + blockDependencies);
            BlockShort blockShort = new BlockShort(
                    block.getId(),
                    block.getTitle(),
                    block.getDescription(),
                    lessonShorts,
                    new ArrayList<Long>());
            blockShorts.add(blockShort);
        }

        for (BlockShort blockShort: blockShorts) {
            List<Long> dependences = new ArrayList<Long>();
            Long nextLessonId = blockShort.getLessons().get(0).getId();
            Lesson nextLesson = lessonService.findById(nextLessonId).get();
            List<Antecedence> antecedences = antecedenceService.findByNextLesson(nextLesson);
            for (Antecedence antecedence: antecedences) {
                for (BlockShort potentialDependence: blockShorts) {
                    Long previousLessonId = potentialDependence.getLessons().getLast().getId();
                    if (antecedence.getPreviousLesson().getId() == previousLessonId) {
                        dependences.add(potentialDependence.getId());
                    }
                }
            }

            blockShort.setDependencies(dependences);
        }

        System.out.println(blockShorts.toString());
        return ResponseEntity.ok(blockShorts);

    }

    // Modify an UE
    @Operation(summary = "Modify an UE")
    @PutMapping(value = "/ue/{ueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> modifyUE(@PathVariable Long ueId, @RequestBody UECreation newUE) {
        Optional<UE> oue = ueService.findById(ueId);
        if (oue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No UE with this id was found");
        }

        UE ue = oue.get();
        ue.setName(newUE.getName());
        ue.setDescription(newUE.getDescription());
        ueService.save(ue);
        if (newUE.getManagers()!= null) {
            for (Long id : newUE.getManagers()) {
                User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("The User doesn't exist"));
                ueManagerService.addUEManager(user, ue);
            }
        }
        return ResponseEntity.ok("UE modified");
    }

    // Modify an TAF
    @Operation(summary = "Modify a TAF")
    @PutMapping(value = "/taf/{tafId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> modifyTAF(@PathVariable Long tafId, @RequestBody TAFShort newTaf) {
        Optional<TAF> otaf = tafService.findById(tafId);
        if (otaf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No TAF with this id was found");
        }

        TAF taf = otaf.get();
        System.out.println(GREEN + "Name = " + newTaf.getName() + RESET);
        System.out.println(GREEN + "Description = " + newTaf.getDescription() + RESET);
        System.out.println(GREEN + "Start Date = " + newTaf.getStartDate() + RESET);
        System.out.println(GREEN + "End Date = " + newTaf.getEndDate() + RESET);
        taf.setName(newTaf.getName());
        taf.setDescription(newTaf.getDescription());
        taf.setBeginDate(newTaf.getStartDate());
        taf.setEndDate(newTaf.getEndDate());
        tafService.save(taf);
        if(newTaf.getManagers()!=null) {
            for (Long id: newTaf.getManagers() ) {
                User TAFmanager = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("The User doesn't exist"));
                tafManagerService.addTAFManager(TAFmanager, taf);
            }
        }
        return ResponseEntity.ok("Taf mofified");
    }

    @Operation(summary = "Add the slots to a TAF and their availabilities")
    @PutMapping(value = "/taf/{tafId}/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putSlotInTaf(@PathVariable Long tafId, @RequestBody List<SlotShort> slots) {
        if (slots.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The body is empty");
        } else {
            if (tafService.existsById(tafId)) {
                // Get the slots' TAF
                TAF taf = tafService.findById(tafId).get();

                // Defining date time formatter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Finding first week and counting weeks to differentiate them
                SlotShort firstSlot = slots.getFirst();

                LocalDateTime firstSlotStart = LocalDateTime.parse(firstSlot.getStart(), formatter);

                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int currentWeekCount = firstSlotStart.get(weekFields.weekOfYear());
                int weekCount = 1;

                Calendar calendar = null;

                ResponseEntity<?> responseEntity = getSlotByTafId(tafId);
                if (responseEntity.getStatusCode().value()==200) {
                    List<SlotShort> slotShorts = (List<SlotShort>) responseEntity.getBody();
                    SlotShort slotShort = slotShorts.getFirst();
                    Optional<Slot> relatedSlot = slotService.findById(Long.parseLong(slotShort.getId()));
                    if (relatedSlot.isPresent()) {
                        Calendar relatedCalendar = relatedSlot.get().getCalendar();
                        List<Slot> relatedSlots = relatedCalendar.getSlots();
                        List<Slot> slotsCopy = new ArrayList<>(relatedSlots);
                        for (Slot slot : slotsCopy) {
                            slotService.deleteSlot(slot.getId());
                        }
                        calendar = relatedCalendar;
                    }
                }

                if (calendar == null) {
                    calendar = taf.getCalendars().getFirst();
                }

                //calendarService.save(calendar);
                Integer year = firstSlotStart.getYear();
                Week currentWeek = new Week(weekCount, year);
                weekService.save(currentWeek);

                String[] parts = firstSlot.getInWeekId().split("_");
                int dayCount = Integer.parseInt(parts[0]);
                Day currentDay = new Day(dayCount, currentWeek);
                dayService.save(currentDay);

                for (SlotShort slot : slots) {

                    // Converting start date to LocalDataTime
                    LocalDateTime currentSlotStart = LocalDateTime.parse(slot.getStart(), formatter);
                    LocalDateTime currentSlotEnd = LocalDateTime.parse(slot.getEnd(), formatter);

                    // Looking day number and slot number
                    parts = slot.getInWeekId().split("_");

                    // Change of week (and day)
                    if (currentSlotStart.get(weekFields.weekOfYear()) != currentWeekCount) {
                        currentWeekCount = currentSlotStart.get(weekFields.weekOfYear());
                        weekCount++;
                        currentWeek = new Week(weekCount, year);
                        weekService.save(currentWeek);

                        dayCount = Integer.parseInt(parts[0]);
                        currentDay = new Day(dayCount, currentWeek);
                        dayService.save(currentDay);
                    }
                    // Change of day
                    else if (Integer.parseInt(parts[0]) != dayCount) {
                        dayCount = Integer.parseInt(parts[0]);
                        currentDay = new Day(dayCount, currentWeek);
                        dayService.save(currentDay);
                    }

                    Slot newSlot = slotService.add(Integer.parseInt(parts[1]), currentDay, calendar, currentSlotStart, currentSlotEnd);
                    slotService.save(newSlot);
                    
                    // Unavailable slot
                    if (slot.getStatus() == AvailabilityEnum.UNAVAILABLE) {
                        GlobalUnavailability globalUnavailability = globalUnavailabilityService.addGlobalUnavailability(true, newSlot);
                        globalUnavailabilityService.save(globalUnavailability);
                    }

                    // Unpreferred slot
                    if (slot.getStatus() == AvailabilityEnum.UNPREFERRED) {
                        GlobalUnavailability globalUnavailability = globalUnavailabilityService.addGlobalUnavailability(false, newSlot);
                        globalUnavailabilityService.save(globalUnavailability);
                    }
                }
                return ResponseEntity.ok("ok");

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No TAF with this id was found");
            }

        }
    }

    @Operation(summary = "Get available slots for a specific TAF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available slots",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SlotShort.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid TAF ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "204", description = "No slots or calendars available",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/taf/{tafId}/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSlotByTafId(@PathVariable Long tafId) {
        Optional<TAF> taf = tafService.findById(tafId);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        TAF realTaf = taf.get();

        if (realTaf.getCalendars().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ErrorResponse("No calendars for this taf were found", 204));
        }
        Calendar calendar = realTaf.getCalendars().getLast();
        if (calendar.getSlots().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ErrorResponse("No slots for this taf were found", 204));
        }
        List<Slot> slots = calendar.getSlots();
        System.out.println("GET CALENDAR SLOTS : " + slots);
        List<SlotShort> slotShorts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Slot slot : slots) {
            String inWeekId = slot.getDay().getNumber() + "_" + slot.getNumber();
            SlotShort slotShort = new SlotShort(slot.getId().toString(), 
                inWeekId, slot.getStart().format(formatter), 
                slot.getEnd().format(formatter), 
                AvailabilityEnum.AVAILABLE);
            Optional<GlobalUnavailability> optGlobalUnavailability = globalUnavailabilityService.findBySlot(slot);
            if (optGlobalUnavailability.isPresent()) {
                GlobalUnavailability globalUnavailability = optGlobalUnavailability.get();
                if (globalUnavailability.getStrict()) {
                    slotShort.setStatus(AvailabilityEnum.UNAVAILABLE);
                }
                else {
                    slotShort.setStatus(AvailabilityEnum.UNPREFERRED);
                }
            }
            slotShorts.add(slotShort);
            
        }

        System.out.println(slotShorts.toString());
        return ResponseEntity.ok(slotShorts);

    }

    @Operation(summary = "Create a TAF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available slots",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/taf")
    ResponseEntity<?> addTAF(@RequestBody TAFCreation newTaf) {
        // Retrieving user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        String mail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByMail(mail);

        // If user not found, return error
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        }

        User user = userOpt.get();

        // Creation of the TAF
        TAF taf = tafService.addTAF(newTaf.getName(), newTaf.getDescription(), newTaf.getStartDate().toString(), newTaf.getEndDate().toString());

        // Add the manager(s)
        tafManagerService.addTAFManager(user, taf);
        for (Long managerId : newTaf.getManagers()) {
            User otherUser = userService.findById(managerId).orElseThrow(() -> new IllegalArgumentException("The User doesn't exist"));
            tafManagerService.addTAFManager(otherUser,taf);
        }

        // Create the calendar
        Calendar c = calendarService.addCalendar(taf);

        return ResponseEntity.ok(taf.getId());
    }

    @Operation(summary = "Delete a TAF")
    @DeleteMapping(value = "/taf/{tafId}")
    ResponseEntity<?> deleteTAF(@PathVariable Long tafId) {
        Boolean b = tafService.deleteTAF(tafId);
        if (b) {
            return ResponseEntity.ok("TAF deleted");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No TAF by this id found");
        }
    }

    @Operation(summary = "Create an UE")
    @PostMapping(value = "/ue")
    ResponseEntity<Long> addUE(@RequestBody UECreation newUE) {
        TAF taf = tafService.findById(newUE.getTafId()).orElseThrow(() -> new IllegalArgumentException("The TAF doesn't exist"));
        UE ue = ueService.addUE(newUE.getName(), newUE.getDescription(), taf);
        for (Long managerId : newUE.getManagers()) {
            User user = userService.findById(managerId).orElseThrow(() -> new IllegalArgumentException("The User doesn't exist"));
            ueManagerService.addUEManager(user, ue);
        }
        List<Planning> plannings = taf.getCalendars().getFirst().getConfigs();
        if (plannings!=null && !plannings.isEmpty()) {
            for (Planning p : plannings) {
                planningService.addADefaultConstraintsOfUE(p, ue);
            }
        }
        return ResponseEntity.ok(ue.getId());
    }

    @Operation(summary = "Delete an UE")
    @DeleteMapping(value = "/ue/{ueId}")
    ResponseEntity<?> deleteUE(@PathVariable Long ueId) {
        Boolean b = ueService.deleteUE(ueId);
        if (b) {
            return ResponseEntity.ok("UE deleted");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No UE by this id found");
        }
    }

    @Operation(summary = "Add a lecturer availabilities")
    @PutMapping(value = "/taf/{tafId}/lecturer_availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putLecturerUnavailability(@PathVariable Long tafId, @RequestBody List<UserUnavailabilityShort> userAvailabilities) {
        if (userAvailabilities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The body is empty");
        } 
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        String mail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByMail(mail);

        // If user not found, return error
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user = userOpt.get();

        user.setLastUpdatedAvailability(LocalDateTime.now());

        for (UserUnavailabilityShort userAvailability : userAvailabilities) {
            Optional<Slot> slotOpt = slotService.findById(userAvailability.getId());
            if (slotOpt.isPresent()) {
                Slot slot = slotOpt.get();
                Optional<UserUnavailability> userUnavailabilityOpt = userUnavailabilityService.findBySlotAndByUser(slot, user);
                if (userUnavailabilityOpt.isPresent()) {
                    userUnavailabilityService.deleteUserUnavailability(userUnavailabilityOpt.get().getId());
                }
                if (userAvailability.getStatus() == AvailabilityEnum.UNAVAILABLE) {
                    UserUnavailability userUnavailability = userUnavailabilityService.addUserUnavailability(slot, user, true);
                    userUnavailabilityService.save(userUnavailability);
                }
                if (userAvailability.getStatus() == AvailabilityEnum.UNPREFERRED) {
                    UserUnavailability userUnavailability = userUnavailabilityService.addUserUnavailability(slot, user, false);
                    userUnavailabilityService.save(userUnavailability);
                }
            }
        }
        
       return ResponseEntity.ok("ok");

    }

    @Operation(summary = "Get a lecturer availabilities")
    @GetMapping(value = "/taf/{tafId}/lecturer_availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLecturerUnavailability(@PathVariable Long tafId) {
        Optional<TAF> taf = tafService.findById(tafId);
        if (taf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        String mail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByMail(mail);

        // If user not found, return error
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user = userOpt.get();

        List<UserUnavailabilityShort> userAvailabilities = new ArrayList<>();

        // Get user's unavailabilities
        List<UserUnavailability> userUnavailabilities = userUnavailabilityService.findByUser(user);

        // Filter the ones corresponding to the taf
        userUnavailabilities = userUnavailabilities.stream()
                .filter(userUnavailability -> userUnavailability.getSlot().getCalendar().getTaf().getId().equals(tafId))
                .collect(Collectors.toList());
        for (UserUnavailability userUnavailability : userUnavailabilities) {
            AvailabilityEnum status = userUnavailability.getStrict() ? AvailabilityEnum.UNAVAILABLE : AvailabilityEnum.UNPREFERRED;
            System.out.println("USER UNAVAILABILITY SHORT :" + status);
            userAvailabilities.add(new UserUnavailabilityShort(userUnavailability.getSlot().getId(), status));
            System.out.println("USER UNAVAILABILITIES SHORT :" + userAvailabilities);
        }
        return ResponseEntity.ok(userAvailabilities);

    }

    @Operation(summary = "Get the configurations already made for an UE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of the configurations",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ConfigShort.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid TAF ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    @GetMapping(value = "/taf/{tafId}/configs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPlannings(@PathVariable Long tafId) {
        if (!tafService.existsById(tafId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("TAF not found");
        }
        List<ConfigShort> configs = new ArrayList<>();
        TAF taf = tafService.findById(tafId).get();
        List<Calendar> calendars= taf.getCalendars();
        if (calendars!=null && !calendars.isEmpty()) {
            for (Calendar calendar : calendars) {
                List<Planning> plannings = calendar.getPlannings();
                if (plannings!=null && !plannings.isEmpty()) {
                    for (Planning planning : plannings) {
                        if (planning.getStatus() == Status.CONFIG) {
                            System.out.println("iD " + planning.getId());
                            configs.add(new ConfigShort(planning));
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok(configs);
    }

    @Operation(summary = "Add a new configuration for the TAF")
    @PostMapping(value = "/taf/{tafId}/configs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPlanning(@PathVariable Long tafId, @RequestBody Config config) {
    	System.out.println("CREATE CONFIG !!!!!!!!!!!!!!!");
    	if (!tafService.existsById(tafId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("TAF not found");
        }
        if (config==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The body is empty");
        }

        TAF taf = tafService.findById(tafId).get();


        Calendar calendar;
        try {
            calendar = taf.getCalendars().getFirst();
        } catch (Exception e) {
            System.out.println("The calendar doesn't exist");
            throw new RuntimeException(e);
        }

        Planning planning = planningService.addPlanning(
				                calendar,
				                config.getName(),
				                config.isGlobalUnavailability(),
				                config.getWeightGlobalUnavailability(),
				                config.isLecturersUnavailability(),
				                config.getWeightLecturersUnavailability(),
				                config.isSynchronise(),
				                config.getWeightMaxTimeWithoutLesson(),
				                config.getUEInterlacing(),
				                config.isMiddayBreak(),
				                config.getStartMiddayBreak(),
				                config.getEndMiddayBreak(),
				                config.isMiddayGrouping(),
				                config.getWeightMiddayGrouping(),
				                config.isLessonBalancing(),
				                config.getWeightLessonBalancing(),
				                config.getWeightLessonGrouping(),
				                config.isLessonGrouping(),
				                config.getMaxSolveDuration()
				        );

        //Addition of the synchronisation's constraints
        List<ConstraintSynchroniseWithTAF> cSynchronisations = new ArrayList<>();
        if (config.getConstraintsSynchronisation()!=null && !config.getConstraintsSynchronisation().isEmpty()) {
            for (Config.CSyncrho cs : config.getConstraintsSynchronisation()) {
            	cSynchronisations.add(constraintSynchroniseWithTAFService.add(
                        planning,
                        planningService.findById(cs.getOtherPlanning()).orElseThrow(() -> new IllegalArgumentException("The Other Planning doesn't exist")),
                        cs.isEnabled()
                ));
            }
        }
        planning.setConstrainedSynchronisations(cSynchronisations);

        //Addition of the UEs constraints
        List<ConstraintsOfUE> cUEs = new ArrayList<>();
        int[] lessonGroupingNbLessons = {2,3};
        System.out.println("UE !!! " + taf.getUes() == null);
        if (taf.getUes()!=null) {
            System.out.println("UE !!! " + taf.getUes().size());
            for (UE ue : taf.getUes()) {
            	cUEs.add(constraintsOfUEService.add(
                        ue,
                        planning,
                        true,
                        6,
                        1,
                        false,
                        true,
                        2,
                        false,
                        12,
                        1,
                        lessonGroupingNbLessons
                ));
            }
        }
        planning.setConstraintsOfUEs(cUEs);
        planningService.save(planning);
        System.out.println("Comp !!!!" + planning.getConstraintsOfUEs().size());
        System.out.println("Comp !!!!" + planningService.findById(planning.getId()).get().getConstraintsOfUEs().size());
        return ResponseEntity.ok("New config added !");
    }
    

    // TAFs related to lecturer
    @GetMapping(value = "/lecturer/taf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLecturerTafs() {
        // Retrieving user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required", 401));
        }

        String mail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByMail(mail);

        // If user not found, return error
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", 404));
        }

        User user = userOpt.get();

        // Retrieving related TAF to lecturer
        List<TAF> relatedTafs = new ArrayList<TAF>();
        relatedTafs = user.getLessonLecturers().stream()
                .map(lecturer -> lecturer.getLesson().getUe().getTaf())
                .distinct()
                .collect(Collectors.toList());
        List<TAFShort> answer = new ArrayList<TAFShort>();
        for (TAF taf : relatedTafs) {
            answer.add(new TAFShort(taf.getId(), taf.getName(), taf.getDescription()));
        }
        return ResponseEntity.ok(answer);

    }

    // Information about lecturer's UEs and lessons
    @GetMapping(value = "/lecturer/lessons/{tafId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLecturerLessonsByTaf(@PathVariable Long tafId) {
        if (!tafService.existsById(tafId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("TAF not found");
        }
        // Retrieving user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required", 401));
        }

        String mail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByMail(mail);

        // If user not found, return error
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", 404));
        }

        User user = userOpt.get();

        // Retrieving user's UEs matching taf id
        List<UE> ues = user.getLessonLecturers().stream()
                .map(lecturer -> lecturer.getLesson().getUe())
                .distinct()
                .filter(lesson -> lesson.getTaf().getId() == tafId)
                .collect(Collectors.toList());

        List<UELecturerShort> answer = new ArrayList<UELecturerShort>();

        for (UE ue : ues) {
            List<UEManager> ueManagers = ue.getUEManagers();
            List<UserBrief> managers = new ArrayList<>();
            for (UEManager ueManager : ueManagers) {
                managers.add(new UserBrief(ueManager.getUser().getId(), ueManager.getUser().getFullName()));
            }
            List<Lesson> lessons = ue.getLessons();
            lessons = lessons.stream()
                    .filter(lesson -> lesson.getLessonLecturers().stream()
                            .anyMatch(lecturer -> lecturer.getId().getIdUser() == user.getId()))
                    .collect(Collectors.toList());
            List<LessonShortLecturer> lessonShorts = new ArrayList<>();
            for (Lesson lesson : lessons) {
                List<UserBrief> lecturers = lesson.getLessonLecturers().stream()
                        .filter(lecturer -> lecturer.getUser().getId() != user.getId())
                        .map(lecturer -> new UserBrief(lecturer.getUser().getId(), lecturer.getUser().getFullName()))
                        .collect(Collectors.toList());
                List<LessonSynchronised> synchronise = lesson.getSynchronizations().stream()
                        .map(s -> new LessonSynchronised(s.getLesson1().getId(), ue.getTaf().getName(), ue.getName(), s.getLesson1().getName()))
                        .collect(Collectors.toList());
                LessonShortLecturer lessonShort = new LessonShortLecturer(lesson.getId(), lesson.getName(), lesson.getDescription(), lecturers, synchronise);
                lessonShorts.add(lessonShort);
            }
            UELecturerShort ueLecturerShort = new UELecturerShort(ue.getId(), ue.getName(), ue.getDescription(), managers, lessonShorts);
            answer.add(ueLecturerShort);
        }

        return ResponseEntity.ok(answer);

    }

}
