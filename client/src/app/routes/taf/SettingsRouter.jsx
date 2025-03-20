// React imports
import React from "react";
import { Outlet, useNavigate, useOutletContext } from "react-router-dom"; // React Router hooks for navigation and context

// Material-UI imports
import { Paper, Tab, Tabs } from "@mui/material"; // Components for tab navigation

/**
 * TAFAllSettings component.
 * This component provides a tabbed interface for navigating between different settings pages of a TAF.
 * It uses React Router for navigation and Material-UI for the tab UI.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function TAFAllSettings() {
  /**
   * Tabs configuration.
   * Each tab has a label and a corresponding path for navigation.
   */
  const tabs = [
    { label: "Paramètres généraux", path: "" }, // General settings tab
    { label: "Contraintes de génération", path: "config" }, // Generation constraints tab
  ];

  const navigate = useNavigate(); // Hook for programmatic navigation
  const context = useOutletContext(); // Access context data from the parent route

  /**
   * Determine the active tab based on the current URL.
   * If the URL contains "config", the second tab is active; otherwise, the first tab is active.
   */
  const currentTab = location.pathname.includes("config") ? 1 : 0;

  /**
   * Handles tab change events.
   * Navigates to the corresponding route when a tab is selected.
   *
   * @param {Object} event - The event object from the tab change.
   * @param {number} newValue - The index of the newly selected tab.
   */
  const handleTabChange = (event, newValue) => {
    navigate(tabs[newValue].path); // Navigate to the selected tab's path
  };

  return (
    <>
      {/* Tabs for navigation */}
      <Tabs value={currentTab} onChange={handleTabChange} centered>
        {tabs.map((tab, index) => (
          <Tab key={index} label={tab.label} /> // Render each tab with its label
        ))}
      </Tabs>

      {/* Render the content of the selected tab */}
      {tabs[currentTab].element}

      {/* Render nested routes with additional context */}
      <Paper elevation={3} sx={{ padding: 2, marginTop: 2 }}>
        <Outlet context={context} />
      </Paper>
    </>
  );
}
