// React imports
import React, { useState } from "react";

// Material-UI imports
import { Tabs, Tab, Typography, Stack } from "@mui/material";

// Local imports
import Calendar from "@/features/calendar/components/Calendar";
import GenericWeekCalendar from "@/features/calendar/components/GenericWeekCalendar";
import CalendarContextProvider from "@/hooks/CalendarContext";
import ImportButton from "@/features/calendar/components/ImportButton";
import ExportButton from "@/features/calendar/components/ExportButton";
import generateClassSlots from "@/features/calendar/utils/classSlot";
import ResetButton from "@/features/calendar/components/ResetButton";
import ImportExclusionButton from "@/features/calendar/components/ImportExclusionButton";
import { useOutletContext, useLoaderData, redirect } from "react-router-dom";
import SaveButton from "@/features/calendar/components/SaveButton";
import axiosInstance from "@/config/axiosConfig";
import { JSONToCalendarEvent } from "@/features/calendar/utils/calendarEvent";
import { toast } from "react-toastify";
import adaptCalendar from "@/features/calendar/utils/adaptCalendar";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/availability`);
  return response.data;
}

export async function action({ request, params }) {
  let data = await request.json();

  return await axiosInstance
    .put(`/taf/${params.idTAF}/availability`, data)
    .then(() => {
      toast.success("Disponibilité des créneaux de cours mise à jour");
      return redirect("..");
    })
    .catch(() => {
      toast.error(
        "Erreur lors de la mise à jour de la disponibilité des créneaux de cours",
      );
      return null;
    });
}

export default function LessonsAvailability() {
  const [tabIndex, setTabIndex] = useState(1);

  const data = useLoaderData();
  const context = useOutletContext();

  const handleChange = (event, newValue) => {
    setTabIndex(newValue);
  };

  const initialEvents =
    typeof data === "string"
      ? generateClassSlots(context.taf.startDate, context.taf.endDate)
      : adaptCalendar(
          data.map(JSONToCalendarEvent),
          context.taf.startDate,
          context.taf.endDate,
        );

  return (
    <div>
      <Stack direction="row" spacing={2} alignItems="center">
        <Typography variant="h4" gutterBottom>
          Disponibilité des créneaux de cours
        </Typography>
        <Tabs value={tabIndex} onChange={handleChange} centered>
          <Tab label="Semaine type" />
          <Tab label="Agenda" />
        </Tabs>
      </Stack>
      <CalendarContextProvider initialEvents={initialEvents}>
        <div hidden={tabIndex !== 0}>
          <GenericWeekCalendar />
        </div>
        <div hidden={tabIndex !== 1}>
          <Calendar />
        </div>
        <Stack spacing={2} direction={"row"}>
          <ResetButton />
          <ImportButton />
          <ExportButton />
          <ImportExclusionButton />
          <SaveButton />
        </Stack>
      </CalendarContextProvider>
    </div>
  );
}
