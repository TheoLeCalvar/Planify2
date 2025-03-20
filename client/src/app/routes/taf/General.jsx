// React imports
import { Typography } from "@mui/material";
import React from "react";
import { useOutletContext } from "react-router-dom"; // React Router hooks for nested routes and context

import dayjs from "dayjs";
import EmptyStateMessage from "../EmptyPage";

/**
 * UEGeneral component.
 * This component displays general information about a specific UE (Unité d'Enseignement).
 * It retrieves the UE data from the parent route's context and renders its name and description.
 * Additionally, it renders nested routes using the `Outlet` component.
 *
 * @returns {JSX.Element} - The rendered component.
 */
const TAFGeneral = () => {
  const context = useOutletContext(); // Access context data from the parent route

  const taf = context.taf; // Retrieve UE data from the context

  return (
    <>
      <Typography variant="h3" gutterBottom>
        Informations générales
      </Typography>
      <Typography variant="h4">{taf.name}</Typography>
      <Typography variant="body1" sx={{ marginBottom: 2 }}>
        {taf.description}
      </Typography>
      <Typography variant="h5">Responsables de TAF</Typography>
      <Typography variant="body1" sx={{ marginBottom: 2 }}>
        {taf.managers.map((m) => m.name).join(", ") || "Aucun responsable"}
      </Typography>
      <Typography variant="h5">Dates</Typography>
      <Typography variant="body1" sx={{ marginBottom: 2 }}>
        Du {dayjs(taf.startDate).format("DD/MM/YYYY")} au{" "}
        {dayjs(taf.endDate).format("DD/MM/YYYY")}
      </Typography>
      <Typography variant="h5" sx={{ marginBottom: 2 }}>
        {taf.resultPlanning.length > 0
          ? `${taf.resultPlanning.length} plannings généré(s)`
          : "Pas de planning généré"}
      </Typography>
      <Typography variant="h5" sx={{ marginBottom: 2 }}>
        {taf.tafSynchronised.length > 0
          ? `${taf.tafSynchronised.length} TAF synchronisée(s)`
          : "Pas de TAF synchronisée"}
      </Typography>
      <Typography variant="h5" sx={{ marginBottom: 2 }}>
        {taf.UE.length > 0
          ? `${taf.UE.length} Unités d'Enseignement`
          : "Pas d'Unités d'Enseignement"}
      </Typography>
      <EmptyStateMessage
        messagePrimary={"Sélectionnez un onglet ou une UE"}
        messageSecondary={"ou créez-en une nouvelle."}
        centerPage={false}
      />
    </>
  );
};

export default TAFGeneral;
