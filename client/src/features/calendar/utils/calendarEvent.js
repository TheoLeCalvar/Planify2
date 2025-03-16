// Import localization and constants
import locale from "@/config/locale.json";
import { constants } from "@/config/constants";

// Import Day.js for date manipulation
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek"; // Plugin for ISO week handling
dayjs.extend(isoWeek); // Extend Day.js with the ISO week plugin

/**
 * Converts a JSON event object to a calendar event object.
 *
 * @param {Object} event - The JSON event object.
 * @returns {Object} - The converted calendar event object.
 */
export function JSONToCalendarEvent(event) {
  return {
    id: event.id,
    inWeekId: event.inWeekId,
    title: "Créneau " + event.inWeekId.split("_")[1], // Generate a title based on the inWeekId
    start: event.start,
    end: event.end,
    status: event.status,
    people: [locale.slotStatus[event.status]], // Localized status
    _options: {
      additionalClasses: [constants.CALENDAR.SLOT_COLOR[event.status]], // Add CSS class for the status
    },
  };
}

/**
 * Converts a JSON event object to a planning event object.
 *
 * @param {Object} event - The JSON event object.
 * @returns {Object} - The converted planning event object.
 */
export function JSONToPlanningEvent(event) {
  return {
    id: event.id,
    title: event.ue + " - " + event.title, // Combine UE and title for the event
    description: event.description,
    start: event.start,
    end: event.end,
    people: event.lecturers, // List of lecturers
    calendarId: event.uniqueUEId.toString(), // Unique calendar ID
  };
}

/**
 * Assigns a unique ID to each UE (teaching unit) in the list of events.
 *
 * @param {Array} events - The list of events.
 * @returns {Array} - The updated list of events with unique UE IDs.
 */
export function assignUniqueUDId(events) {
  // Create a mapping between UE and a unique ID
  const ueToIdMap = {};
  let currentId = 1;

  // Iterate over the list of events and assign a unique ID to each UE
  events.forEach((event) => {
    if (!ueToIdMap[event.ue]) {
      ueToIdMap[event.ue] = currentId;
      currentId++;
    }
    // Add the unique ID to the event
    event.uniqueUEId = ueToIdMap[event.ue];
  });

  return events;
}

/**
 * Converts a calendar event object to a JSON object.
 *
 * @param {Object} event - The calendar event object.
 * @returns {Object} - The converted JSON object.
 */
export function CalendarEventToJSON(event) {
  return {
    id: event.id,
    inWeekId: event.inWeekId,
    start: event.start,
    end: event.end,
    status: event.status,
  };
}

/**
 * Converts a list of calendar events to a generic format.
 * This is useful for standardizing events across different calendars.
 *
 * @param {Array} events - The list of calendar events.
 * @returns {Array} - The list of events in a generic format.
 */
export function convertCalendarToGeneric(events) {
  // Clone the events and filter out duplicates based on inWeekId
  const genericInitialEvents = structuredClone(events).filter(
    (v, i, a) => a.findIndex((t) => t.inWeekId === v.inWeekId) === i,
  );

  /**
   * Converts a date to a generic format based on ISO week.
   *
   * @param {string} date - The date to convert.
   * @returns {string} - The converted date in "YYYY-MM-DD HH:mm" format.
   */
  const convertDate = (date) => {
    const inputDate = dayjs(date); // Parse the date
    const weekdayOffset = inputDate.isoWeekday() - 1; // Offset for the day of the week (Monday=0, Sunday=6)

    return dayjs("2000-01-03 00:00") // Start of the first ISO week in 2000
      .add(weekdayOffset, "day") // Add the same day of the week
      .hour(inputDate.hour()) // Preserve the hour
      .minute(inputDate.minute()) // Preserve the minutes
      .format("YYYY-MM-DD HH:mm"); // Format the date
  };

  // Convert the start and end dates of each event to the generic format
  genericInitialEvents.forEach((event) => {
    event.start = convertDate(event.start);
    event.end = convertDate(event.end);
  });

  return genericInitialEvents;
}
