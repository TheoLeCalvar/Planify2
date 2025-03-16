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
import ResetButton from "@/features/calendar/components/ResetButton";
import ImportExclusionButton from "@/features/calendar/components/ImportExclusionButton";
import { useLoaderData, redirect } from "react-router-dom";
import SaveButton from "@/features/calendar/components/SaveButton";
import axiosInstance from "@/config/axiosConfig";
import { JSONToCalendarEvent } from "@/features/calendar/utils/calendarEvent";
import { toast } from "react-toastify";

export async function loader({ params }) {
  const [response, lecturer] = await Promise.all([
    axiosInstance.get(`/taf/${params.idTAF}/availability`),
    axiosInstance.get(`/taf/${params.idTAF}/lecturer_availability`),
  ]);
  return { tafPlanning: response.data, lecturerPlanning: lecturer.data };
}

export async function action({ request, params }) {
  let data = await request.json();

  return await axiosInstance
    .put(`/taf/${params.idTAF}/lecturer_availability`, data)
    .then(() => {
      toast.success("Disponibilités mises à jour");
      return redirect("..");
    })
    .catch(() => {
      toast.error("Erreur lors de la mise à jour des disponibilités");
      return null;
    });
}

export default function LecturerAvailability() {
  const [tabIndex, setTabIndex] = useState(1);

  const { tafPlanning, lecturerPlanning } = useLoaderData();

  const handleChange = (event, newValue) => {
    setTabIndex(newValue);
  };

  const initialEvents =
    typeof tafPlanning === "string"
      ? null
      : tafPlanning
          .filter((event) => event.status != "UNAVAILABLE")
          .map((event) => {
            let newStatus = lecturerPlanning.find(
              (lesson) => lesson.id == event.id,
            )?.status;
            return { ...event, status: newStatus || event.status };
          })
          .map(JSONToCalendarEvent);

  return (
    <div>
      {initialEvents?.length > 0 ? (
        <>
          <Stack direction="row" spacing={2} alignItems="center">
            <Typography variant="h4" gutterBottom>
              Vos disponibilités
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
        </>
      ) : (
        <Typography variant="h6" gutterBottom>
          {
            "Le calendrier de la TAF n'est pas disponible. Demandez au responsable de TAF de le configurer."
          }
        </Typography>
      )}
    </div>
  );
}
