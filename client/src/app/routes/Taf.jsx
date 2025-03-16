// React imports
import React from "react";
import { Outlet, useLoaderData } from "react-router-dom"; // React Router hooks for nested routes and data loading

// Local imports
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests

/**
 * Loader function for fetching TAF data.
 * Retrieves the details of a specific TAF based on its ID.
 *
 * @param {Object} params - Parameters passed to the loader, including `idTAF`.
 * @returns {Promise<Object>} - A promise that resolves to the TAF data.
 */
export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}`); // Fetch TAF details
  return { taf: response.data }; // Return the TAF data
}

/**
 * Loader function for fetching TAF and lessons data for lecturers.
 * Retrieves the details of a specific TAF and the associated lessons for a lecturer.
 *
 * @param {Object} params - Parameters passed to the loader, including `idTAF`.
 * @returns {Promise<Object>} - A promise that resolves to an object containing TAF and lessons data.
 */
export async function loaderLecturer({ params }) {
  const [taf, lessons] = await Promise.all([
    axiosInstance.get(`/taf/${params.idTAF}`), // Fetch TAF details
    axiosInstance.get(`/lecturer/lessons/${params.idTAF}`), // Fetch lessons for the lecturer
  ]);
  return { taf: taf.data, lessons: lessons.data }; // Return the TAF and lessons data
}

/**
 * TAF component.
 * This component serves as a layout for TAF-related routes.
 * It uses React Router's `Outlet` to render nested routes and provides TAF data as context.
 *
 * @returns {JSX.Element} - The rendered TAF component.
 */
export default function TAF() {
  const data = useLoaderData(); // Load data from the loader

  return (
    <>
      {/* Render nested routes with the TAF data as context */}
      <Outlet context={data} />
    </>
  );
}
