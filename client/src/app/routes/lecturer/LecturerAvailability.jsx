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

/**
 * Loader function to fetch data for the LecturerAvailability component.
 * Fetches both TAF planning and lecturer availability data.
 *
 * @param {Object} params - Parameters passed to the loader, including `idTAF`.
 * @returns {Object} - An object containing `tafPlanning` and `lecturerPlanning`.
 */
export async function loader({ params }) {
  const [response, lecturer] = await Promise.all([
    axiosInstance.get(`/taf/${params.idTAF}/availability`), // Fetch TAF planning
    axiosInstance.get(`/taf/${params.idTAF}/lecturer_availability`), // Fetch lecturer availability
  ]);
  return { tafPlanning: response.data, lecturerPlanning: lecturer.data };
}

/**
 * Action function to handle updates to lecturer availability.
 * Sends a PUT request to update the availability and handles success or error cases.
 *
 * @param {Object} request - The HTTP request object.
 * @param {Object} params - Parameters passed to the action, including `idTAF`.
 * @returns {Object|null} - Redirects to the parent route on success or returns null on failure.
 */
export async function action({ request, params }) {
  let data = await request.json();

  return await axiosInstance
    .put(`/taf/${params.idTAF}/lecturer_availability`, data) // Update lecturer availability
    .then(() => {
      toast.success("Disponibilités mises à jour"); // Show success notification
      return redirect(".."); // Redirect to the parent route
    })
    .catch(() => {
      toast.error("Erreur lors de la mise à jour des disponibilités"); // Show error notification
      return null; // Return null on failure
    });
}

/**
 * LecturerAvailability component.
 * Displays and manages the lecturer's availability using a calendar interface.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function LecturerAvailability() {
  const [tabIndex, setTabIndex] = useState(1); // State to manage the active tab index

  // Load data from the loader function
  const { tafPlanning, lecturerPlanning } = useLoaderData();

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
   * Filters out unavailable events and maps them to calendar events.
   */
  const initialEvents =
    typeof tafPlanning === "string"
      ? null // If tafPlanning is a string, return null
      : tafPlanning
          .filter((event) => event.status != "UNAVAILABLE") // Exclude unavailable events
          // Apply lecturer availability status to events
          .map((event) => {
            let newStatus = lecturerPlanning.find(
              (lesson) => lesson.id == event.id, // Match event with lecturer planning
            )?.status;
            return { ...event, status: newStatus || event.status }; // Update status if available
          })
          .map(JSONToCalendarEvent); // Convert to calendar event format

  return (
    <div>
      {initialEvents?.length > 0 ? ( // Check if there are events to display
        <>
          <Stack direction="row" spacing={2} alignItems="center">
            <Typography variant="h4" gutterBottom>
              Vos disponibilités
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
        </>
      ) : (
        // Message displayed if no events are available
        <Typography variant="h6" gutterBottom>
          {
            "Le calendrier de la TAF n'est pas disponible. Demandez au responsable de TAF de le configurer."
          }
        </Typography>
      )}
    </div>
  );
}
