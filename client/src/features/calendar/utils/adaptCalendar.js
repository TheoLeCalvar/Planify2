import dayjs from "dayjs";
import generateClassSlots from "./classSlot";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";

dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

export default function adaptCalendar(baseCalendarEvents, startDate, endDate) {
  const sortedEvents = baseCalendarEvents.sort((a, b) => {
    return new Date(a.start).getTime() - new Date(b.start).getTime();
  });
  const currentStartDate = sortedEvents[0].start.split(" ")[0];
  const currentEndDate =
    sortedEvents[sortedEvents.length - 1].end.split(" ")[0];
  const maxId = sortedEvents.reduce((acc, event) => {
    return Math.max(acc, parseInt(event.id));
  }, 0);

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

  // Add missing events before current calendar
  if (dayjs(startDate).isBefore(dayjs(currentStartDate))) {
    const newEvents = generateClassSlots(
      startDate,
      dayjs(currentStartDate).subtract(1, "day").format("YYYY-MM-DD"),
      maxId + 1,
    );
    sortedEvents.push(...newEvents);
  }
  // Add missing events after current calendar
  if (dayjs(endDate).isAfter(dayjs(currentEndDate))) {
    const newEvents = generateClassSlots(
      dayjs(currentEndDate).add(1, "day").format("YYYY-MM-DD"),
      endDate,
      maxId + 1 + sortedEvents.length,
    );
    sortedEvents.push(...newEvents);
  }

  // Remove events that are not within dates
  const filteredEvents = sortedEvents.filter((event) => {
    return (
      dayjs(event.start.split(" ")[0]).isSameOrAfter(startDate) &&
      dayjs(event.end.split(" ")[0]).isSameOrBefore(endDate)
    );
  });

  return filteredEvents;
}
