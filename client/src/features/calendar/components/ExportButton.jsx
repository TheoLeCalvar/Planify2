import React from "react";
import { Button } from "@mui/material";
import exportCalendar from "../utils/exportCalendar";
import { constants } from "../../../config/constants";
import { useContext } from "react";
import { CalendarContext } from "../../../hooks/CalendarContext";

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
