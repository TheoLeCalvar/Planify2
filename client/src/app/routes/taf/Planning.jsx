// React imports
import React, { useMemo } from "react";
import { useLoaderData } from "react-router-dom";

// Material-UI imports
import { Typography } from "@mui/material";

// ScheduleX imports
import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react";
import { createEventModalPlugin } from "@schedule-x/event-modal";
import { createEventsServicePlugin } from "@schedule-x/events-service";
import { createCalendarControlsPlugin } from "@schedule-x/calendar-controls";
import "@schedule-x/theme-default/dist/index.css";

// Local imports
import { USE_MOCK_DATA } from "@/config/constants";
import axiosInstance from "@/config/axiosConfig";
import { constants } from "@/config/constants";
import {
  JSONToPlanningEvent,
  assignUniqueUDId,
} from "@/features/calendar/utils/calendarEvent";

export async function loader({ params }) {
  if (USE_MOCK_DATA) {
    return [
      {
        id: "1",
        start: "2025-01-01 08:00",
        end: "2025-01-01 09:15",
        UE: "IHM",
        title: "Cours Java 1" + params.idPlanning,
        description: "Première approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
      {
        id: "2",
        start: "2025-01-01 09:30",
        end: "2025-01-01 10:45",
        UE: "IHM2",
        title: "Cours Java 2",
        description: "Deuxième approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
      {
        id: "3",
        start: "2025-01-02 08:00",
        end: "2025-01-02 09:15",
        UE: "IHM3",
        title: "Cours Java 3",
        description: "Troisième approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
      {
        id: "4",
        start: "2025-01-03 08:00",
        end: "2025-01-03 09:15",
        UE: "IHM4",
        title: "Cours Java 4",
        description: "Quatrième approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
      {
        id: "5",
        start: "2025-01-03 09:30",
        end: "2025-01-03 10:45",
        UE: "IHM5",
        title: "Cours Java 5",
        description: "Cinquième approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
      {
        id: "6",
        start: "2025-01-01 18:00",
        end: "2025-01-01 19:15",
        UE: "IHM6",
        title: "Cours Java 6",
        description: "Sixième approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
      {
        id: "7",
        start: "2025-01-02 09:30",
        end: "2025-01-02 10:45",
        UE: "IHM7",
        title: "Cours Java 7",
        description: "Septième approche de Java",
        lecturers: ["Jacques Noyé", "Mario"],
      },
    ];
  }

  const response = await axiosInstance.get(
    `/solver/result/${params.idPlanning}`,
  );
  return response.data;
}

export default function TAFPlanning() {
  const initialEvents = useLoaderData();
  const eventModal = createEventModalPlugin();
  const eventsService = useMemo(() => createEventsServicePlugin(), []);
  const calendarControls = useMemo(() => createCalendarControlsPlugin(), []);

  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG,
      selectedDate: initialEvents[0].start.split(" ")[0],
      events: assignUniqueUDId(initialEvents).map(JSONToPlanningEvent),
    },
    [eventModal, eventsService, calendarControls],
  );

  if (eventsService?.set) {
    eventsService.set(assignUniqueUDId(initialEvents).map(JSONToPlanningEvent));
    initialEvents[0] &&
      calendarControls.setDate(initialEvents[0].start.split(" ")[0]);
  }

  return (
    <div>
      <Typography variant="h4" gutterBottom>
        Emploi du temps
      </Typography>
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
}
