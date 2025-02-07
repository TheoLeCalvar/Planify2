import React, { useState } from "react";
import Calendar from "../../../features/calendar/components/Calendar";
import GenericWeekCalendar from "../../../features/calendar/components/GenericWeekCalendar";
import { Tabs, Tab, Typography, Stack } from "@mui/material";
import CalendarContextProvider from "../../../hooks/CalendarContext";
import ImportButton from "../../../features/calendar/components/ImportButton";
import ExportButton from "../../../features/calendar/components/ExportButton";
import generateClassSlots from "../../../features/calendar/utils/classSlot";
import ResetButton from "../../../features/calendar/components/ResetButton";
import ImportExclusionButton from "../../../features/calendar/components/ImportExclusionButton";
import { useOutletContext, useLoaderData } from "react-router-dom";
import SaveButton from "../../../features/calendar/components/SaveButton";
import { USE_MOCK_DATA } from "../../../config/constants";
import axiosInstance from "../../../config/axiosConfig";
import { JSONToCalendarEvent } from "../../../features/calendar/utils/calendarEvent";

export async function loader({ params }) {
  if (USE_MOCK_DATA) {
    return null;
  }

  const response = await axiosInstance.get(`/taf/${params.idTAF}/availability`);
  return response.data;
}

export async function action({ request, params }) {
  let data = await request.json();

  if (USE_MOCK_DATA) {
    await new Promise((res) => setTimeout(res, 1000));
    return { ok: true };
  }

  const result = await axiosInstance.put(
    `/taf/${params.idTAF}/availability`,
    data,
  );
  return { ok: result.status === 200 };
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
      : data.map(JSONToCalendarEvent);

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
