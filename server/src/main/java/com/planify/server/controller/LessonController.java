package com.planify.server.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.planify.server.controller.returnsClass.*;
import com.planify.server.models.*;
import com.planify.server.service.*;
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
    private TAFManagerService tafManagerService;

    @Autowired
    private UEManagerService ueManagerService;

    @Autowired
    private UserUnavailabilityService userUnavailabilityService;


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

    // Get the list of TAF
    @GetMapping(value = "/alltaf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllTAFs() {
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
        List<PlanningReturn> resultPlanning = new ArrayList<PlanningReturn>();
        List<Calendar> calendars = realTaf.getCalendars();
        if (calendars!=null && !calendars.isEmpty()) {
            for (Calendar calendar : calendars) {
                List<Planning> plannings = planningService.findByCalendar(calendar);
                if (plannings!=null) {
                    for (Planning planning : plannings) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatted = planning.getTimestamp().format(formatter);
                        resultPlanning.add(new PlanningReturn(planning.getId(), formatted));
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
                realTaf.getTafManagers().stream().map(manager -> manager.getUser().getFullName())
                        .collect(Collectors.toList()),
                realTaf.getBeginDate(),
                realTaf.getEndDate(),
                resultPlanning);
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
                    .body(new ErrorResponse("The body is empty", 404));
        } else {
            if (ueService.existsById(ueId)) {

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
        //List<Long> blockDependenciesSave = new ArrayList<>();

        for (Block block : blocks) {
            //List<Long> blockDependencies = new ArrayList<>();
            //blockDependencies.addAll(blockDependenciesSave);
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
    @PutMapping(value = "/ue/{ueId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyUE(@PathVariable Long ueId, @RequestBody UECreation newUE) {
        Optional<UE> oue = ueService.findById(ueId);
        if (oue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No UE with this id was found", 404));
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
    @PutMapping(value = "/taf/{tafId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyTAF(@PathVariable Long tafId, @RequestBody TAFShort newTaf) {
        Optional<TAF> otaf = tafService.findById(tafId);
        if (otaf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No TAF with this id was found", 404));
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

    @PutMapping(value = "/taf/{tafId}/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putSlotInTaf(@PathVariable Long tafId, @RequestBody List<SlotShort> slots) {
        if (slots.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("The body is empty", 400));
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
                    calendar = new Calendar(taf);
                }

                calendarService.save(calendar);
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
                        .body(new ErrorResponse("No TAF with this id was found", 404));
            }

        }
    }

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

    @PostMapping(value = "/taf")
    ResponseEntity<String> addTAF(@RequestBody TAFCreation newTaf) {
        TAF taf = tafService.addTAF(newTaf.getName(), newTaf.getDescription(), newTaf.getStartDate().toString(), newTaf.getEndDate().toString());
        for (Long managerId : newTaf.getManagers()) {
            User user = userService.findById(managerId).orElseThrow(() -> new IllegalArgumentException("The User doesn't exist"));
            tafManagerService.addTAFManager(user,taf);
        }
        return ResponseEntity.ok("Taf had been added");
    }

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

    @PostMapping(value = "/ue")
    ResponseEntity<String> addUE(@RequestBody UECreation newUE) {
        TAF taf = tafService.findById(newUE.getTafId()).orElseThrow(() -> new IllegalArgumentException("The TAF doesn't exist"));
        UE ue = ueService.addUE(newUE.getName(), newUE.getDescription(), taf);
        for (Long managerId : newUE.getManagers()) {
            User user = userService.findById(managerId).orElseThrow(() -> new IllegalArgumentException("The User doesn't exist"));
            ueManagerService.addUEManager(user, ue);
        }
        return ResponseEntity.ok("UE had been added");
    }

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

    @PutMapping(value = "/taf/{tafId}/lecturer_availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putLecturerUnavailability(@PathVariable Long tafId, @RequestBody List<UserUnavailabilityShort> userAvailabilities) {
        if (userAvailabilities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("The body is empty", 400));
        } 
        
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

        for (UserUnavailabilityShort userAvailability : userAvailabilities) {
            Optional<Slot> slotOpt = slotService.findById(userAvailability.getId());
            if (slotOpt.isPresent()) {
                Slot slot = slotOpt.get();
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

}
