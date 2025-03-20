// React imports
import React from "react";
import { Outlet, useLoaderData, useOutletContext } from "react-router-dom"; // React Router hooks for nested routes and data loading
import { useNavigate, useLocation } from "react-router-dom"; // Hooks for navigation and accessing the current location

// Material-UI imports
import { Tabs, Tab, Typography, Paper } from "@mui/material"; // Components for tab navigation and typography

// Local imports
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests

/**
 * Loader function for fetching UE data.
 * Retrieves the details of a specific UE based on its ID.
 *
 * @param {Object} params - Parameters passed to the loader, including `idUE`.
 * @returns {Promise<Object>} - A promise that resolves to the UE data.
 */
export async function loader({ params }) {
  const response = await axiosInstance.get(`/ue/${params.idUE}`); // Fetch UE details
  return response.data; // Return the UE data
}

/**
 * UE component.
 * This component serves as a layout for UE-related routes.
 * It provides a tabbed navigation interface for different sections of the UE
 * and renders nested routes using React Router's `Outlet`.
 *
 * @returns {JSX.Element} - The rendered UE component.
 */
export default function UE() {
  const ue = useLoaderData(); // Load UE data from the loader
  const context = useOutletContext(); // Access context data from the parent route

  const navigate = useNavigate(); // Hook for programmatic navigation
  const location = useLocation(); // Hook for accessing the current location

  // Define the tabs and their corresponding paths
  const tabs = [
    { label: "Général", path: "" }, // General information tab
    { label: "Paramètres", path: "settings" }, // Settings tab
    { label: "Contraintes de génération", path: "config" }, // Generation constraints tab
    { label: "Cours", path: "lessons" }, // Lessons tab
  ];

  // Determine the active tab based on the current URL
  const tabIndex = location.pathname.includes("config")
    ? 2
    : tabs.findIndex((tab) => tab.path === location.pathname.split("/").pop());
  const currentTab = tabIndex === -1 ? 0 : tabIndex; // Default to the first tab if no match is found

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
      {/* Tab navigation */}
      <Tabs value={currentTab} onChange={handleTabChange} centered>
        {tabs.map((tab, index) => (
          <Tab key={index} label={tab.label} /> // Render each tab with its label
        ))}
      </Tabs>

      {/* Display the UE name */}
      <Typography variant="h4" gutterBottom mt={2}>
        Unité d&apos;enseignement : {ue.name}
      </Typography>

      {/* Render nested routes with the UE data as context */}
      <Paper elevation={3} sx={{ padding: 2, marginTop: 2 }}>
        <Outlet context={{ ...context, ue }} />
      </Paper>
    </>
  );
}
