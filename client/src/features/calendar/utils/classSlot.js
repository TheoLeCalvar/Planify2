import dayjs from "dayjs";

import slotConfig from "@/config/slots.json";
import { JSONToCalendarEvent } from "./calendarEvent";
import { constants } from "@/config/constants";

export default function generateClassSlots(startDate, endDate, startId = 1) {
  const events = [];
  let currentId = startId;
  let currentDate = dayjs(startDate);

  while (currentDate.isBefore(endDate) || currentDate.isSame(endDate, "day")) {
    if (Object.keys(slotConfig).includes(currentDate.day().toString())) {
      // Monday to Friday
      const slots = slotConfig[currentDate.day()];

      slots.forEach((slot, index) => {
        events.push(
          JSONToCalendarEvent({
            id: currentId.toString(),
            inWeekId:
              currentDate.day().toString() + "_" + (index + 1).toString(),
            title: `Créneau ${index + 1}`,
            start: currentDate.format(`YYYY-MM-DD ${slot.start}`),
            end: currentDate.format(`YYYY-MM-DD ${slot.end}`),
            status: constants.CALENDAR.DEFAULT_STATUS,
          }),
        );
        currentId++;
      });
    }
    currentDate = currentDate.add(1, "day");
  }

  return events;
}
