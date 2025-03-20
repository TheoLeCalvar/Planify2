// React imports
import { Typography } from "@mui/material";
import React from "react";
import { useOutletContext } from "react-router-dom"; // React Router hooks for nested routes and context
import EmptyStateMessage from "../EmptyPage";

/**
 * UEGeneral component.
 * This component displays general information about a specific UE (Unité d'Enseignement).
 * It retrieves the UE data from the parent route's context and renders its name and description.
 * Additionally, it renders nested routes using the `Outlet` component.
 *
 * @returns {JSX.Element} - The rendered component.
 */
const UEGeneral = () => {
  const context = useOutletContext(); // Access context data from the parent route

  const ue = context.ue; // Retrieve UE data from the context

  return (
    <>
      <Typography variant="h3" gutterBottom>
        Informations générales
      </Typography>
      <Typography variant="h4">{ue.name}</Typography>
      <Typography variant="body1" sx={{ marginBottom: 2 }}>
        {ue.description}
      </Typography>
      <Typography variant="h5">Responsables de l&apos;UE</Typography>
      <Typography variant="body1" sx={{ marginBottom: 2 }}>
        {ue.managers.map((m) => m.name).join(", ") || "Aucun responsable"}
      </Typography>
      <EmptyStateMessage
        messagePrimary={"Sélectionnez un onglet"}
        messageSecondary={"pour éditer l'UE."}
        centerPage={false}
      />
    </>
  );
};

export default UEGeneral;
