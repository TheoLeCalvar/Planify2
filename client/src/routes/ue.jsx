import React from "react";
import { Outlet, useLoaderData, useOutletContext } from "react-router-dom";
import { useNavigate, useLocation } from "react-router-dom";
import { Tabs, Tab } from "@mui/material";
import { USE_MOCK_DATA } from "../contants";
import axiosInstance from "../services/axiosConfig";

export async function loader({ params }) {
  if (USE_MOCK_DATA) {
    const mockTaf = [
      {
        id: params.idUE,
        name: "Programmation polyglotte",
        description: "TAF is a student association.",
        managers: ["Théo Le Calvar"],
      },
      {
        id: "2",
        name: "Programmation fonctionnelle",
        description: "This is another student association.",
        managers: ["John Doe"],
      },
      {
        id: "3",
        name: "Conférences",
        description: "Yet another student association.",
        managers: ["Alice Brown"],
      },
    ];

    return mockTaf[params.idUE - 1];
  }

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
    { label: "Cours", path: "lessons" },
  ];

  // Identifier l'onglet actif basé sur l'URL
  const tabIndex = tabs.findIndex(
    (tab) => tab.path === location.pathname.split("/").pop(),
  );
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
      <h1>{ue.name}</h1>
      <Outlet context={{ ...context, ue }} />
    </>
  );
}
