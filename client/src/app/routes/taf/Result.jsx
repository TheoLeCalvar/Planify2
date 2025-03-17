// React imports
import React from "react";
import { useLoaderData, useOutletContext } from "react-router-dom"; // Hooks for loading data and accessing context

// Material-UI imports
import { Button, Typography } from "@mui/material"; // UI components for styling

// ScheduleX imports
import "@schedule-x/theme-default/dist/index.css"; // ScheduleX theme styles

// Local imports
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests
import Planning from "@/components/Plannning"; // Custom component for displaying the schedule

/**
 * Loader function to fetch the planning result.
 * Fetches the basic result of the planning solver for a specific planning ID.
 *
 * @param {Object} params - Parameters passed to the loader, including `idPlanning`.
 * @returns {Promise<Object>} - A promise that resolves to the planning result data.
 */
export async function loader({ params }) {
  const response = await axiosInstance.get(
    `/solver/result/${params.idPlanning}/basic`, // API endpoint for fetching the planning result
  );
  return response.data; // Return the fetched data
}

/**
 * TAFPlanning component.
 * Displays the result of the planning solver, including status, messages, and the generated schedule.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function TAFPlanning() {
  const data = useLoaderData(); // Load data from the loader function
  const context = useOutletContext(); // Access context data from the parent route

  /**
   * Mapping of planning statuses to user-friendly labels.
   */
  const statusLabel = {
    GENERATED: "Généré",
    PROCESSING: "En cours de génération...",
    WAITING_TO_BE_PROCESSED: "En file d'attente...",
  };

  /**
   * Handles the export of the planning as a CSV file.
   * Fetches the CSV data from the API and triggers a download in the browser.
   */
  const handleExport = async () => {
    const response = await axiosInstance.get(
      `/solver/result/${context.planning.id}/csv`, // API endpoint for exporting the planning as CSV
      {
        responseType: "blob", // Response type for binary data
      },
    );
    const url = window.URL.createObjectURL(new Blob([response.data])); // Create a URL for the blob
    const link = document.createElement("a"); // Create a temporary link element
    link.href = url;
    link.setAttribute(
      "download",
      `planning_${context.planning.name}_${context.planning.timestamp}.csv`, // Set the filename for the download
    );
    document.body.appendChild(link); // Append the link to the document
    link.click(); // Trigger the download
  };

  return (
    <div>
      {/* Header section */}
      <Typography variant="h4" gutterBottom>
        Planning
      </Typography>

      {/* Display the planning status */}
      <Typography variant="body1" gutterBottom fontSize={20}>
        Statut : <strong>{statusLabel[data.status]}</strong>
      </Typography>

      {/* Display the planning message */}
      <Typography variant="body1" gutterBottom mb={4} fontStyle="italic">
        {data.message}
      </Typography>

      {/* Display the generation date */}
      <Typography variant="body1" gutterBottom>
        Date de génération : <strong>{context.planning.timestamp}</strong>
      </Typography>

      {/* Display the configuration name */}
      <Typography variant="body1" gutterBottom>
        Configuration : <strong>{context.planning.name}</strong>
      </Typography>

      {/* Display if an optimal solution was found */}
      {data.status === "GENERATED" && data.solutionOptimal && (
        <Typography variant="body1" gutterBottom>
          <strong>Solution optimale trouvée</strong>
        </Typography>
      )}
      {data.status === "GENERATED" && !data.solutionOptimal && (
        <Typography variant="body1" gutterBottom>
          Solution optimale non trouvée
        </Typography>
      )}

      {/* Display the generated schedule or a fallback message */}
      {data.scheduledLessons?.length > 0 ? (
        <>
          {/* Button to export the schedule as CSV */}
          <Button onClick={handleExport} variant="contained" sx={{ mb: 2 }}>
            Exporter CSV
          </Button>

          {/* Render the schedule using the Planning component */}
          <Planning initialEvents={data.scheduledLessons} />
        </>
      ) : (
        <Typography variant="h6" fontStyle="italic" mt={5}>
          Pas d&apos;emploi du temps généré
        </Typography>
      )}
    </div>
  );
}
