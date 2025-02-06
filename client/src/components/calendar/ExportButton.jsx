import React from "react";
import { Button } from "@mui/material";
import exportCalendar from "../../helper/exportCalendar";
import { constants } from "../../contants";
import { useContext } from "react";
import { CalendarContext } from "../../context/CalendarContext";

export default function ExportButton() {
  const { eventService, genericEventService } = useContext(CalendarContext);

  return (
    <Button
      variant="text"
      onClick={() => {
        exportCalendar(
          eventService.getAll(),
          genericEventService.getAll(),
          constants.CALENDAR.TYPES.AVAILABILITY,
        );
      }}
    >
      Exporter
    </Button>
  );
}
