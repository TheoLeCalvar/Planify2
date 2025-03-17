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

/**
 * Loader function to fetch availability data for lessons.
 * Fetches the availability of class slots for a specific TAF.
 *
 * @param {Object} params - Parameters passed to the loader, including `idTAF`.
 * @returns {Promise<Object>} - A promise that resolves to the availability data.
 */
export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/availability`);
  return response.data;
}

/**
 * Action function to update the availability of class slots.
 * Sends a PUT request to update the availability and handles success or error cases.
 *
 * @param {Object} request - The HTTP request object.
 * @param {Object} params - Parameters passed to the action, including `idTAF`.
 * @returns {Object|null} - Redirects to the parent route on success or returns null on failure.
 */
export async function action({ request, params }) {
  let data = await request.json();

  return await axiosInstance
    .put(`/taf/${params.idTAF}/availability`, data)
    .then(() => {
      toast.success("Disponibilité des créneaux de cours mise à jour"); // Show success notification
      return redirect(".."); // Redirect to the parent route
    })
    .catch(() => {
      toast.error(
        "Erreur lors de la mise à jour de la disponibilité des créneaux de cours",
      ); // Show error notification
      return null; // Return null on failure
    });
}

/**
 * LessonsAvailability component.
 * Displays and manages the availability of class slots using a calendar interface.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function LessonsAvailability() {
  const [tabIndex, setTabIndex] = useState(1); // State to manage the active tab index

  const data = useLoaderData(); // Load data from the loader function
  const context = useOutletContext(); // Access context data from the parent route

  /**
   * Handles tab change events.
   *
   * @param {Object} event - The event object.
   * @param {number} newValue - The new tab index.
   */
  const handleChange = (event, newValue) => {
    setTabIndex(newValue);
  };

  /**
   * Processes the initial events for the calendar.
   * If the data is a string, it generates class slots based on the TAF's start and end dates.
   * Otherwise, it adapts the calendar data to fit the TAF's date range.
   */
  const initialEvents =
    typeof data === "string"
      ? generateClassSlots(context.taf.startDate, context.taf.endDate) // Generate class slots
      : adaptCalendar(
          data.map(JSONToCalendarEvent), // Convert JSON data to calendar events
          context.taf.startDate,
          context.taf.endDate,
        );

  return (
    <div>
      {/* Header section with title and tabs */}
      <Stack direction="row" spacing={2} alignItems="center">
        <Typography variant="h4" gutterBottom>
          Disponibilité des créneaux de cours
        </Typography>
        {/* Tabs to switch between "Semaine type" and "Agenda" views */}
        <Tabs value={tabIndex} onChange={handleChange} centered>
          <Tab label="Semaine type" />
          <Tab label="Agenda" />
        </Tabs>
      </Stack>

      {/* Calendar context provider to manage calendar state */}
      <CalendarContextProvider initialEvents={initialEvents}>
        {/* Generic week calendar view */}
        <div hidden={tabIndex !== 0}>
          <GenericWeekCalendar />
        </div>
        {/* Full calendar view */}
        <div hidden={tabIndex !== 1}>
          <Calendar />
        </div>

        {/* Action buttons for calendar operations */}
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
