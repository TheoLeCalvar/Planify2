// Import libraries and utility functions
import ICAL from "ical.js"; // Library for parsing ICS files
import { toast } from "react-toastify"; // Library for displaying toast notifications
import { JSONToCalendarEvent } from "./calendarEvent"; // Utility function to convert JSON data to calendar events

/**
 * importExclusionCalendar
 * This function allows users to import an exclusion calendar from an ICS file.
 * It parses the ICS file, applies a delay before and after each event, and updates the status of overlapping events.
 *
 * @param {Array} previousEvents - The list of existing calendar events.
 * @param {number} delay - The delay (in minutes) to apply before and after each imported event.
 * @param {string} changingStatus - The status to assign to overlapping events.
 * @returns {Promise<Array>} - A promise that resolves with the updated list of calendar events.
 */
export default function importExclusionCalendar(
  previousEvents,
  delay,
  changingStatus,
) {
  return new Promise((resolve, reject) => {
    // Dynamically create an input element of type "file"
    const input = document.createElement("input");
    input.type = "file";
    input.accept = ".ics"; // Restrict file selection to ICS files

    // Add an event listener for file selection
    input.onchange = (event) => {
      const file = event.target.files[0]; // Get the selected file
      if (!file) {
        // Display an error toast if no file is selected
        toast.error("Importation impossible : Aucun fichier sélectionné");
        reject("Aucun fichier sélectionné");
        return;
      }

      const reader = new FileReader(); // Create a FileReader to read the file

      // Read the file as text
      reader.onload = (e) => {
        try {
          const icsData = e.target.result; // Get the file content

          // Parse the ICS file using ical.js
          const jcalData = ICAL.parse(icsData);
          const component = new ICAL.Component(jcalData);
          const vevents = component.getAllSubcomponents("vevent"); // Extract all VEVENT components

          // Convert ICS events to a usable format
          const importedEvents = vevents.map((vevent) => {
            const event = new ICAL.Event(vevent);
            return {
              start: event.startDate.toJSDate(), // Convert start date to JavaScript Date
              end: event.endDate.toJSDate(), // Convert end date to JavaScript Date
            };
          });

          if (importedEvents.length === 0) {
            // Display a warning toast if no events are found
            toast.warn("Aucun événement trouvé dans le fichier");
            resolve(previousEvents); // Resolve with the existing events
            return;
          }

          // Add the delay before and after each imported event
          const delayInMs = delay * 60 * 1000; // Convert delay to milliseconds
          const extendedRanges = importedEvents.map((event) => ({
            start: new Date(event.start.getTime() - delayInMs), // Subtract delay from start time
            end: new Date(event.end.getTime() + delayInMs), // Add delay to end time
          }));

          // Check for overlaps and update the status of overlapping events
          const newEvents = previousEvents.map((prevEvent) => {
            const prevStart = new Date(prevEvent.start);
            const prevEnd = new Date(prevEvent.end);
            let newStatus = prevEvent.status;

            // Check if the event overlaps with any of the extended ranges
            const overlaps = extendedRanges.some(
              (range) => prevStart < range.end && prevEnd > range.start,
            );

            if (overlaps) {
              newStatus = changingStatus; // Update the status if overlapping
            }

            // Return the updated event
            return JSONToCalendarEvent({ ...prevEvent, status: newStatus });
          });

          // Display a success toast
          toast.success("Calendrier importé avec succès !");
          resolve(newEvents); // Resolve with the updated events
        } catch (error) {
          // Handle errors during ICS parsing
          toast.error("Importation impossible : données incompatibles");
          reject("Erreur lors de l'analyse du fichier ICS : " + error.message);
        }
      };

      // Handle file reading errors
      reader.onerror = () => {
        toast.error("Erreur lors de la lecture du fichier ICS");
        reject("Erreur de lecture du fichier.");
      };

      // Read the content of the file
      reader.readAsText(file);
    };

    // Simulate a click to open the file dialog
    input.click();
  });
}
