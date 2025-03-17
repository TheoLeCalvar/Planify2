// React imports
import React from "react";

// Local imports
import SolverConfigSelectorComponent from "@/features/solver-config/components/SolverConfigSelector"; // Component for rendering the solver configuration selector
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests

/**
 * Loader function to fetch solver configurations for a specific TAF.
 * This function retrieves all configurations associated with a given TAF ID.
 *
 * @param {Object} params - Parameters passed to the loader, including `idTAF`.
 * @returns {Promise<Array>} - A promise that resolves to an array of solver configurations.
 */
export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/configs`); // Fetch configurations from the API
  return response.data; // Return the fetched data
}

/**
 * SolverConfigSelector component.
 * This component renders the `SolverConfigSelectorComponent`, which is responsible for displaying
 * and managing the selection of solver configurations.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function SolverConfigSelector() {
  return <SolverConfigSelectorComponent />; // Render the solver configuration selector component
}
