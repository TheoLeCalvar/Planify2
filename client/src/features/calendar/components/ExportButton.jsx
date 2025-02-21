// React imports
import React from "react";
import { useContext } from "react";

// Material-UI imports
import { Button } from "@mui/material";

// Local imports
import exportCalendar from "../utils/exportCalendar";
import { constants } from "@/config/constants";
import { CalendarContext } from "@/hooks/CalendarContext";

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
