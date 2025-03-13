// React imports
import React from "react";
import { useLoaderData, useOutletContext } from "react-router-dom";

// Material-UI imports

// ScheduleX imports
import "@schedule-x/theme-default/dist/index.css";

// Local imports
import axiosInstance from "@/config/axiosConfig";
import { Button, Typography } from "@mui/material";
import Planning from "@/components/Plannning";

export async function loader({ params }) {
  const response = await axiosInstance.get(
    `/solver/result/${params.idPlanning}/basic`,
  );
  return response.data;
}

export default function TAFPlanning() {
  const data = useLoaderData();
  const context = useOutletContext();

  const statusLabel = {
    GENERATED: "Généré",
    PROCESSING: "En cours de génération...",
    WAITING_TO_BE_PROCESSED: "En file d'attente...",
  };

  const handleExport = async () => {
    const response = await axiosInstance.get(
      `/solver/result/${context.planning.id}/csv`,
      {
        responseType: "blob",
      },
    );
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute(
      "download",
      `planning_${context.planning.name}_${context.planning.timestamp}.csv`,
    );
    document.body.appendChild(link);
    link.click();
  };

  return (
    <div>
      <Typography variant="h4" gutterBottom>
        Planning
      </Typography>
      <Typography variant="body1" gutterBottom fontSize={20}>
        Statut : <strong>{statusLabel[data.status]}</strong>
      </Typography>
      <Typography variant="body1" gutterBottom mb={4} fontStyle="italic">
        {data.message}
      </Typography>
      <Typography variant="body1" gutterBottom>
        Date de génération : <strong>{context.planning.timestamp}</strong>
      </Typography>
      <Typography variant="body1" gutterBottom>
        Configuration : <strong>{context.planning.name}</strong>
      </Typography>
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

      {data.scheduledLessons?.length > 0 ? (
        <>
          <Button onClick={handleExport} variant="contained" sx={{ mb: 2 }}>
            Exporter CSV
          </Button>
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
