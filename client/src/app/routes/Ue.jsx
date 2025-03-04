// React imports
import React from "react";
import { Outlet, useLoaderData, useOutletContext } from "react-router-dom";
import { useNavigate, useLocation } from "react-router-dom";

// Material-UI imports
import { Tabs, Tab, Typography } from "@mui/material";

// Local imports
import axiosInstance from "@/config/axiosConfig";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/ue/${params.idUE}`);
  return response.data;
}

export default function UE() {
  const ue = useLoaderData();
  const context = useOutletContext();

  const navigate = useNavigate();
  const location = useLocation();

  // Définition des routes et de leurs labels
  const tabs = [
    { label: "Général", path: "" },
    { label: "Paramètres", path: "settings" },
    { label: "Contraintes de génération", path: "config" },
    { label: "Cours", path: "lessons" },
  ];

  // Identifier l'onglet actif basé sur l'URL
  const tabIndex = location.pathname.includes("config")
    ? 2
    : tabs.findIndex((tab) => tab.path === location.pathname.split("/").pop());
  const currentTab = tabIndex === -1 ? 0 : tabIndex;

  // Changer l'URL lorsque l'utilisateur sélectionne un onglet
  const handleTabChange = (event, newValue) => {
    navigate(tabs[newValue].path);
  };

  return (
    <>
      <Tabs value={currentTab} onChange={handleTabChange} centered>
        {tabs.map((tab, index) => (
          <Tab key={index} label={tab.label} />
        ))}
      </Tabs>
      <Typography variant="h4" gutterBottom mt={2}>
        Unité d&apos;enseignement : {ue.name}
      </Typography>
      <Outlet context={{ ...context, ue }} />
    </>
  );
}
