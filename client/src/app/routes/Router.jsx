// React imports
import React, { useContext } from "react";

// Context imports
import { ProfileContext } from "@/hooks/ProfileContext"; // Provides user profile context

// React Router imports
import { RouterProvider } from "react-router-dom"; // Component for rendering a router

// Routers for different user profiles
import TafManagerRouter from "../TafManagerRouter"; // Router for TAF managers
import LecturerRouter from "../LecturerRouter"; // Router for lecturers

/**
 * Router component.
 * This component dynamically selects and renders the appropriate router
 * based on the user's profile (e.g., "taf_manager" or "lecturer").
 *
 * @returns {JSX.Element} - The rendered router component.
 */
export default function Router() {
  const { profile } = useContext(ProfileContext); // Access the user's profile from the context

  return (
    <>
      {/* Render the router based on the user's profile */}
      {profile === "taf_manager" ? (
        <RouterProvider router={TafManagerRouter} /> // Router for TAF managers
      ) : (
        <RouterProvider router={LecturerRouter} /> // Router for lecturers
      )}
    </>
  );
}
