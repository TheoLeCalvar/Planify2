import { CalendarEventToJSON } from "./calendarEvent"

export default function exportCalendar(calendar, genericCalendar, type="availability", filename="export-calendrier.json"){
    const jsonContent = {
        calendarType: type,
        exportTime: new Date().toISOString(),
        dataCalendar: calendar.map(CalendarEventToJSON),
        dataGenericCalendar: genericCalendar.map(CalendarEventToJSON)
    }

    const jsonString = `data:text/json;chatset=utf-8,${encodeURIComponent(JSON.stringify(jsonContent))}`;
    const link = document.createElement("a");
    link.href = jsonString;
    link.download = filename;

    link.click();
}