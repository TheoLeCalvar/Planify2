import { useState } from "react";
import Calendar from "../../components/calendar/Calendar";
import GenericWeekCalendar from "../../components/calendar/GenericWeekCalendar";
import { Tabs, Tab, Typography, Stack } from "@mui/material";
import CalendarContextProvider from "../../context/CalendarContext";
import ImportButton from "../../components/calendar/ImportButton";
import ExportButton from "../../components/calendar/ExportButton";
import generateClassSlots from "../../helper/classSlot";
import ResetButton from "../../components/calendar/ResetButton";
import ImportExclusionButton from "../../components/calendar/ImportExclusionButton";
import { useOutletContext, useLoaderData } from "react-router-dom";
import SaveButton from "../../components/calendar/SaveButton";

export async function loader({ params }) {
  const mockData = null;

  return mockData;
}

export async function action({ request }) {
  let data = await request.json();
  console.log(data);
  await new Promise((res) => setTimeout(res, 1000));
  return { ok: true };
}

export default function LessonsAvailability() {
    const [tabIndex, setTabIndex] = useState(1);

    const data = useLoaderData();
    const context = useOutletContext();

    const handleChange = (event, newValue) => {
        setTabIndex(newValue);
    };

    const initialEvents = data ?? generateClassSlots(
        context.taf.startDate,
        context.taf.endDate
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
