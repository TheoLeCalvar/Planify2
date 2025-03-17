// Import Day.js for date manipulation
import dayjs from "dayjs";

// Import utility functions and plugins
import generateClassSlots from "./classSlot"; // Utility to generate class slots
import isSameOrBefore from "dayjs/plugin/isSameOrBefore"; // Plugin to check if a date is the same or before another
import isSameOrAfter from "dayjs/plugin/isSameOrAfter"; // Plugin to check if a date is the same or after another

// Extend Day.js with the imported plugins
dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

/**
 * adaptCalendar
 * This function adapts a calendar by:
 * - Sorting events by their start date.
 * - Adding missing events before or after the current calendar range.
 * - Filtering out events that are outside the specified date range.
 *
 * @param {Array} baseCalendarEvents - The initial list of calendar events.
 * @param {string} startDate - The start date of the desired calendar range (YYYY-MM-DD).
 * @param {string} endDate - The end date of the desired calendar range (YYYY-MM-DD).
 *
 * @returns {Array} - The adapted list of calendar events within the specified range.
 */
export default function adaptCalendar(baseCalendarEvents, startDate, endDate) {
  // Sort events by their start date
  const sortedEvents = baseCalendarEvents.sort((a, b) => {
    return new Date(a.start).getTime() - new Date(b.start).getTime();
  });

  // Determine the current start and end dates of the calendar
  const currentStartDate = sortedEvents[0].start.split(" ")[0];
  const currentEndDate =
    sortedEvents[sortedEvents.length - 1].end.split(" ")[0];

  // Find the maximum event ID in the current calendar
  const maxId = sortedEvents.reduce((acc, event) => {
    return Math.max(acc, parseInt(event.id));
  }, 0);

  // Debugging information
  console.log(
    "currentStartDate",
    currentStartDate,
    "startDate",
    startDate,
    "currentEndDate",
    currentEndDate,
    "endDate",
    endDate,
    "maxId",
    maxId,
  );

  // Add missing events before the current calendar range
  if (dayjs(startDate).isBefore(dayjs(currentStartDate))) {
    const newEvents = generateClassSlots(
      startDate, // Start date for the new events
      dayjs(currentStartDate).subtract(1, "day").format("YYYY-MM-DD"), // End date for the new events
      maxId + 1, // Starting ID for the new events
    );
    sortedEvents.push(...newEvents); // Add the new events to the sorted list
  }

  // Add missing events after the current calendar range
  if (dayjs(endDate).isAfter(dayjs(currentEndDate))) {
    const newEvents = generateClassSlots(
      dayjs(currentEndDate).add(1, "day").format("YYYY-MM-DD"), // Start date for the new events
      endDate, // End date for the new events
      maxId + 1 + sortedEvents.length, // Starting ID for the new events
    );
    sortedEvents.push(...newEvents); // Add the new events to the sorted list
  }

  // Filter out events that are not within the specified date range
  const filteredEvents = sortedEvents.filter((event) => {
    return (
      dayjs(event.start.split(" ")[0]).isSameOrAfter(startDate) &&
      dayjs(event.end.split(" ")[0]).isSameOrBefore(endDate)
    );
  });

  // Return the filtered list of events
  return filteredEvents;
}
