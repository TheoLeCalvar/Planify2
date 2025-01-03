import locale from "../config/locale.json";
import { constants } from '../contants'

import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
dayjs.extend(isoWeek);


export function JSONToCalendarEvent(event){
    return {
        id: event.id,
        inWeekId: event.inWeekId,
        title: event.title,
        start: event.start,
        end: event.end,
        status: event.status,
        people: [locale.slotStatus[event.status]],
        _options: {
            additionalClasses: [constants.CALENDAR.SLOT_COLOR[event.status]],
        }
    }
}

export function CalendarEventToJSON(event){
    return {
        id: event.id,
        inWeekId: event.inWeekId,
        title: event.title,
        start: event.start,
        end: event.end,
        status: event.status
    }
}

export function convertCalendarToGeneric(events){
    const genericInitialEvents = events.filter(
        (v, i, a) => a.findIndex((t) => t.inWeekId === v.inWeekId) === i
    );

    const convertDate = (date) => {
        const inputDate = dayjs(date); // Parser la date
        const weekdayOffset = inputDate.isoWeekday() - 1; // Décalage du jour dans la semaine (lundi=0, dimanche=6)

        return dayjs("2000-01-03 00:00") // Début de la première semaine ISO 2000
            .add(weekdayOffset, "day") // Ajouter le même jour de la semaine
            .hour(inputDate.hour()) // Conserver l'heure
            .minute(inputDate.minute()) // Conserver les minutes
            .format('YYYY-MM-DD HH:mm'); // Formater la date
    };

    genericInitialEvents.forEach((event) => {
        event.start = convertDate(event.start);
        event.end = convertDate(event.end);
    });

    return genericInitialEvents;
}