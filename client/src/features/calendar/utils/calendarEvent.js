import locale from "@/config/locale.json";
import { constants } from "@/config/constants";

import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
dayjs.extend(isoWeek);

export function JSONToCalendarEvent(event) {
  return {
    id: event.id,
    inWeekId: event.inWeekId,
    title: "Créneau " + event.inWeekId.split("_")[1],
    start: event.start,
    end: event.end,
    status: event.status,
    people: [locale.slotStatus[event.status]],
    _options: {
      additionalClasses: [constants.CALENDAR.SLOT_COLOR[event.status]],
    },
  };
}

export function JSONToPlanningEvent(event) {
  return {
    id: event.id,
    title: event.ue + " - " + event.title,
    description: event.description,
    start: event.start,
    end: event.end,
    people: event.lecturers,
    calendarId: event.uniqueUEId.toString(),
  };
}

export function assignUniqueUDId(events) {
  // Créer un mapping entre UE et un identifiant unique
  const ueToIdMap = {};
  let currentId = 1;

  // Itérer sur la liste des événements et associer un ID unique à chaque UE
  events.forEach((event) => {
    if (!ueToIdMap[event.ue]) {
      ueToIdMap[event.ue] = currentId;
      currentId++;
    }
    // Ajouter l'ID unique dans l'événement
    event.uniqueUEId = ueToIdMap[event.ue];
  });

  return events;
}

export function CalendarEventToJSON(event) {
  return {
    id: event.id,
    inWeekId: event.inWeekId,
    start: event.start,
    end: event.end,
    status: event.status,
  };
}

export function convertCalendarToGeneric(events) {
  const genericInitialEvents = structuredClone(events).filter(
    (v, i, a) => a.findIndex((t) => t.inWeekId === v.inWeekId) === i,
  );

  const convertDate = (date) => {
    const inputDate = dayjs(date); // Parser la date
    const weekdayOffset = inputDate.isoWeekday() - 1; // Décalage du jour dans la semaine (lundi=0, dimanche=6)

    return dayjs("2000-01-03 00:00") // Début de la première semaine ISO 2000
      .add(weekdayOffset, "day") // Ajouter le même jour de la semaine
      .hour(inputDate.hour()) // Conserver l'heure
      .minute(inputDate.minute()) // Conserver les minutes
      .format("YYYY-MM-DD HH:mm"); // Formater la date
  };

  genericInitialEvents.forEach((event) => {
    event.start = convertDate(event.start);
    event.end = convertDate(event.end);
  });

  return genericInitialEvents;
}
