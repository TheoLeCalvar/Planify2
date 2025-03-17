// Import utility functions and libraries
import { JSONToCalendarEvent } from "./calendarEvent"; // Utility function to convert JSON data to calendar events
import { toast } from "react-toastify"; // Library for displaying toast notifications

/**
 * importCalendar
 * This function allows users to import calendar data from a JSON file.
 * It validates the file type and structure, converts the data to calendar events, and resolves the imported data.
 *
 * @param {string} acceptedType - The expected type of calendar (e.g., "availability").
 * @returns {Promise<Object>} - A promise that resolves with the imported calendar data (events and generic events).
 */
export default function importCalendar(acceptedType) {
  return new Promise((resolve, reject) => {
    // Dynamically create an input element of type "file"
    const input = document.createElement("input");
    input.type = "file";
    input.accept = ".json"; // Restrict file selection to JSON files

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
          const jsonData = JSON.parse(e.target.result); // Parse the file content as JSON

          // Validate the calendar type
          if (jsonData.calendarType !== acceptedType) {
            toast.error(
              "Importation impossible : type de calendrier incompatible",
            );
            reject("Mauvais type de calendrier");
            return;
          }

          // Convert the JSON data to calendar events
          const events = jsonData.dataCalendar.map(JSONToCalendarEvent);
          const genericEvents =
            jsonData.dataGenericCalendar.map(JSONToCalendarEvent);

          // Display a success toast
          toast.success("Calendrier importé avec succès !");

          // Resolve the promise with the imported events
          resolve({ events, genericEvents });
        } catch (error) {
          // Handle JSON parsing errors
          toast.error("Importation impossible : données incompatible");
          reject("Erreur lors de la lecture du fichier JSON : " + error);
        }
      };

      // Handle file reading errors
      reader.onerror = () => {
        toast.error("Erreur lors de la lecture du fichier JSON");
        reject("Erreur de lecture du fichier.");
      };

      // Read the content of the file
      reader.readAsText(file);
    };

    // Simulate a click to open the file dialog
    input.click();
  });
}
