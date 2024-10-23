export function calendarToJson(calendar){
    const result = calendar.map((event) => {
        return {
            id: event.id,
            title: event.title,
            start: event.start,
            end: event.end,
            status: event.status,
        }
    })
    return result
}

export default function exportCalendar(calendar, type="availability", filename="export-calendrier.json"){
    const jsonContent = {
        calendarType: type,
        exportTime: new Date().toISOString(),
        data: calendarToJson(calendar)
    }

    const jsonString = `data:text/json;chatset=utf-8,${encodeURIComponent(JSON.stringify(jsonContent))}`;
    const link = document.createElement("a");
    link.href = jsonString;
    link.download = filename;

    link.click();
}