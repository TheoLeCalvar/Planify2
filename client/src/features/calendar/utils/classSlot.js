// Import Day.js for date manipulation
import dayjs from "dayjs";

// Import configuration and utility functions
import slotConfig from "@/config/slots.json"; // Configuration for time slots
import { JSONToCalendarEvent } from "./calendarEvent"; // Utility function to convert JSON to calendar events
import { constants } from "@/config/constants"; // Application constants

/**
 * generateClassSlots
 * This function generates a list of class slots (events) for a given date range.
 * It uses the slot configuration to create events for each day within the range.
 *
 * @param {string} startDate - The start date of the range (YYYY-MM-DD).
 * @param {string} endDate - The end date of the range (YYYY-MM-DD).
 * @param {number} [startId=1] - The starting ID for the generated events.
 *
 * @returns {Array} - A list of generated class slot events.
 */
export default function generateClassSlots(startDate, endDate, startId = 1) {
  const events = []; // Array to store the generated events
  let currentId = startId; // Initialize the event ID counter
  let currentDate = dayjs(startDate); // Initialize the current date to the start date

  // Loop through each day in the date range
  while (currentDate.isBefore(endDate) || currentDate.isSame(endDate, "day")) {
    // Check if the current day has configured slots (e.g., Monday to Friday)
    if (Object.keys(slotConfig).includes(currentDate.day().toString())) {
      const slots = slotConfig[currentDate.day()]; // Get the slots for the current day

      // Generate events for each slot
      slots.forEach((slot, index) => {
        events.push(
          JSONToCalendarEvent({
            id: currentId.toString(), // Assign a unique ID to the event
            inWeekId:
              currentDate.day().toString() + "_" + (index + 1).toString(), // Generate an in-week ID
            title: `Créneau ${index + 1}`, // Title of the event
            start: currentDate.format(`YYYY-MM-DD ${slot.start}`), // Start time of the event
            end: currentDate.format(`YYYY-MM-DD ${slot.end}`), // End time of the event
            status: constants.CALENDAR.DEFAULT_STATUS, // Default status for the event
          }),
        );
        currentId++; // Increment the event ID counter
      });
    }
    // Move to the next day
    currentDate = currentDate.add(1, "day");
  }

  return events; // Return the list of generated events
}
