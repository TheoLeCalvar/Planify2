// React imports
import React from "react";
import { toast } from "react-toastify"; // Notifications for success and error messages
import { redirect } from "react-router-dom"; // React Router function for redirection

// Data imports
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests

// Date imports
import dayjs from "dayjs"; // Library for date manipulation

// Custom components
import SolverConfigComponent from "@/features/solver-config/components/SolverConfig"; // Component for rendering the solver configuration form
import {
  globalConfigSections,
  ueConfigSections,
} from "@/features/solver-config/utils/solverConfig"; // Configuration sections for global and UE-specific settings

/**
 * Loader function to fetch configuration data.
 * If an `idConfig` is provided, it fetches the configuration data for editing.
 * Otherwise, it returns an empty object for creating a new configuration.
 *
 * @param {Object} params - Parameters passed to the loader, including `idConfig`.
 * @returns {Promise<Object>} - A promise that resolves to the configuration data.
 */
export async function loader({ params }) {
  if (params.idConfig) {
    const response = await axiosInstance.get(`/config/${params.idConfig}`); // Fetch configuration data
    const responseData = response.data;

    // Convert all fields to strings for consistent handling in the form
    Object.keys(responseData).forEach((key) => {
      responseData[key] = String(responseData[key]);
    });

    return response.data;
  } else {
    return {}; // Return an empty object for new configurations
  }
}

/**
 * Converts raw data fields to their appropriate types based on the field type.
 *
 * @param {any} value - The raw value of the field.
 * @param {string} type - The type of the field (e.g., "boolean", "number", "time").
 * @returns {any} - The value converted to the appropriate type.
 */
const rawDataFieldToType = (value, type) => {
  switch (type) {
    case "boolean":
      return value === "true"; // Convert to boolean
    case "number":
      return parseInt(value, 10); // Convert to number
    case "time":
      return dayjs(value).format("HH:mm"); // Format time using DayJS
    default:
      return value; // Return the value as-is for other types
  }
};

/**
 * Converts raw data to typed data based on the configuration sections.
 *
 * @param {Object} rawData - The raw data from the form.
 * @param {Array} configSections - The configuration sections defining field types.
 * @returns {Object} - The typed data ready for submission.
 */
const rawDataToTypedData = (rawData, configSections) =>
  configSections.reduce((acc, section) => {
    section.fields.forEach((field) => {
      acc[field.name] = rawDataFieldToType(rawData[field.name], field.type); // Convert each field to its appropriate type
    });
    return acc;
  }, {});

/**
 * Action function to handle form submission for creating or updating a configuration.
 * Sends a POST or PATCH request depending on whether an `idConfig` is provided.
 *
 * @param {Object} request - The HTTP request object.
 * @param {Object} params - Parameters passed to the action, including `idConfig` and `idUE`.
 * @returns {Object|null} - Redirects on success or returns null on failure.
 */
export async function action({ request, params }) {
  const rawData = await request.json(); // Parse the raw data from the request

  let data;
  if (!params.idUE) {
    // Process global configuration data
    data = rawDataToTypedData(rawData, globalConfigSections);
  } else {
    // Process UE-specific configuration data
    data = {
      ue: parseInt(params.idUE), // Add the UE ID to the data
      ...rawDataToTypedData(rawData, ueConfigSections),
    };
  }

  if (params.idConfig) {
    // Update an existing configuration
    return await axiosInstance
      .patch(`/config/${params.idConfig}`, data) // Send a PATCH request
      .then(() => {
        toast.success("Configuration mise à jour"); // Show success notification
        return redirect("../.."); // Redirect to the parent route
      })
      .catch(() => {
        toast.error("Erreur lors de la mise à jour de la configuration"); // Show error notification
        return null; // Return null on failure
      });
  } else {
    // Create a new configuration
    return await axiosInstance
      .post(`/taf/${params.idTAF}/configs`, data) // Send a POST request
      .then(() => {
        toast.success("Configuration créée"); // Show success notification
        return redirect(".."); // Redirect to the parent route
      })
      .catch(() => {
        toast.error("Erreur lors de la création de la configuration"); // Show error notification
        return null; // Return null on failure
      });
  }
}

/**
 * SolverConfig component.
 * This component renders the solver configuration form using the `SolverConfigComponent`.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function SolverConfig() {
  return <SolverConfigComponent />; // Render the solver configuration form
}
