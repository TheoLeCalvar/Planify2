// Import utility functions and libraries
import { CalendarEventToJSON } from "./calendarEvent"; // Utility function to convert calendar events to JSON format
import { toast } from "react-toastify"; // Library for displaying toast notifications

/**
 * exportCalendar
 * This function exports calendar data to a JSON file and triggers a download in the browser.
 * It includes both the main calendar and the generic calendar data.
 *
 * @param {Array} calendar - The list of events from the main calendar.
 * @param {Array} genericCalendar - The list of events from the generic calendar.
 * @param {string} [type="availability"] - The type of calendar being exported (default is "availability").
 * @param {string} [filename="export-calendrier.json"] - The name of the exported JSON file (default is "export-calendrier.json").
 */
export default function exportCalendar(
  calendar,
  genericCalendar,
  type = "availability",
  filename = "export-calendrier.json",
) {
  // Prepare the JSON content for export
  const jsonContent = {
    calendarType: type, // Type of the calendar (e.g., "availability")
    exportTime: new Date().toISOString(), // Timestamp of the export
    dataCalendar: calendar.map(CalendarEventToJSON), // Convert main calendar events to JSON format
    dataGenericCalendar: genericCalendar.map(CalendarEventToJSON), // Convert generic calendar events to JSON format
  };

  // Create a JSON string and encode it for download
  const jsonString = `data:text/json;chatset=utf-8,${encodeURIComponent(
    JSON.stringify(jsonContent),
  )}`;

  // Create a temporary link element for triggering the download
  const link = document.createElement("a");
  link.href = jsonString; // Set the link's href to the JSON string
  link.download = filename; // Set the filename for the downloaded file

  // Display a success toast notification
  toast.success("Calendrier exporté avec succès !");

  // Trigger the download by programmatically clicking the link
  link.click();
}
