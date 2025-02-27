import React from "react";
import { Outlet, useNavigate, useOutletContext } from "react-router-dom";
import { Tab, Tabs } from "@mui/material";

export default function TAFAllSettings() {
  const tabs = [
    { label: "Paramètres généraux", path: "" },
    { label: "Contraintes de génération", path: "config" },
  ];

  const navigate = useNavigate();
  const context = useOutletContext();

  // Identifier l'onglet actif basé sur l'URL
  const currentTab = location.pathname.includes("config") ? 1 : 0;

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
      {tabs[currentTab].element}
      <Outlet context={context} />
    </>
  );
}
