// React imports
import React from "react";

// Data imports
import axiosInstance from "@/config/axiosConfig";

// Custom components
import AppBarComponent from "@/features/app-bar/components/AppBarComponent";

/**
 * Loader function to fetch TAF data.
 * This function is used to load data for the AppBar component.
 *
 * @returns {Promise<Object>} - A promise that resolves to the TAF data.
 */
export async function loader() {
  const response = await axiosInstance.get("/taf"); // Fetch TAF data from the API
  return response.data; // Return the data
}

/**
 * Loader function to fetch lecturer-specific TAF data.
 * This function is used to load data for lecturers in the AppBar component.
 *
 * @returns {Promise<Object>} - A promise that resolves to the lecturer's TAF data.
 */
export async function loaderLecturer() {
  const response = await axiosInstance.get("/lecturer/taf"); // Fetch lecturer-specific TAF data
  return response.data; // Return the data
}

/**
 * AppBar component.
 * This component renders the application bar using the `AppBarComponent`.
 *
 * @returns {JSX.Element} - The rendered AppBar component.
 */
const AppBar = () => {
  return <AppBarComponent />; // Render the custom AppBar component
};

export default AppBar;
