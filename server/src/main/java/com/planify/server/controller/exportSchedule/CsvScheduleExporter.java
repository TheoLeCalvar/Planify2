package com.planify.server.controller.exportSchedule;

import com.planify.server.models.Planning;
import com.opencsv.CSVWriter;
import com.planify.server.models.ScheduledLesson;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class CsvScheduleExporter extends ScheduleExporter{

    @Override
    public String export(Planning planning) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer, ',', CSVWriter.NO_QUOTE_CHARACTER)) {
            // Top
            csvWriter.writeNext(new String[]{"Semaine",  "Jour", "Date", "H début", "H fin", "Durée", "Créneaux UE TAF", "Code UE", "Titre de l'activité", "Code activité", "Evaluation", "Contraintes", "Intervenants" });

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Body
            for (ScheduledLesson lesson : planning.getScheduledLessons()) {
                LocalDateTime date = lesson.getStart();
                csvWriter.writeNext(new String[]{
                        "S" + date.get(WeekFields.of(Locale.FRANCE).weekOfYear()),
                        date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE),
                        String.valueOf(date.getDayOfMonth()),
                        date.format(formatter),
                        lesson.getEnd().format(formatter),
                        String.valueOf(Duration.between(date, lesson.getEnd()).toMinutes()),
                        "",
                        lesson.getUE(),
                        lesson.getTitle(),
                        "",
                        "",
                        "",
                        lesson.getLecturers().stream().reduce("", (x,y) -> x + " " + y)
                });
            }

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV", e);
        }
    }
}
