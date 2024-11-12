import locale from "../config/locale.json";
import { constants } from '../contants'


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