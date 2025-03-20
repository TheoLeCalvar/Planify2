// React imports
import React from "react";
import { createBrowserRouter } from "react-router-dom"; // React Router function for creating a browser-based router

// Routes components
import LoginPage from "./routes/Login"; // Login page component
import AppBar from "./routes/layout/AppBar"; // AppBar layout component
import SideBar from "./routes/layout/SideBar"; // SideBar layout component
import ErrorPage from "./routes/ErrorPage"; // Error page component
import TAF from "./routes/Taf"; // TAF route component
import RegisterPage from "./routes/Register"; // Registration page component
import NavigateRoot from "./routes/NavigateRoot"; // Root navigation component

// Loader functions
import { loaderLecturer as TAFLoader } from "./routes/Taf"; // Loader for fetching TAF data
import { loaderLecturer as AppBarLoader } from "./routes/layout/AppBar"; // Loader for fetching AppBar data
import { loader as AvailibilityLoader } from "./routes/lecturer/LecturerAvailability"; // Loader for fetching lecturer availability data
import { action as AvailibilityAction } from "./routes/lecturer/LecturerAvailability"; // Action for handling lecturer availability updates
import LecturerAvailability from "./routes/lecturer/LecturerAvailability"; // Lecturer availability component
import EmptyStateMessage from "./routes/EmptyPage";

/**
 * Define the TAF route (with a nested SideBar).
 * This route handles the TAF-related pages for lecturers, including their availability.
 */
const tafRoute = [
  {
    path: "",
    element: (
      <EmptyStateMessage
        messagePrimary={"Sélectionnez une TAF"}
        messageSecondary={""}
      />
    ), // Empty state message
  },
  {
    path: "taf/:idTAF", // Dynamic route for a specific TAF
    element: <TAF />, // Main TAF component
    loader: TAFLoader, // Loader function for fetching TAF data
    action: AvailibilityAction, // Action function for handling availability updates
    children: [
      {
        path: "", // Default child route
        element: <SideBar />, // SideBar layout component
        children: [
          {
            path: "", // Default child route of SideBar
            element: <LecturerAvailability />, // Lecturer availability component
            loader: AvailibilityLoader, // Loader function for fetching availability data
          },
        ],
      },
    ],
  },
];

/**
 * Create the main router for the application.
 * This router defines the routes for lecturers, including login, registration, and TAF-related pages.
 */
export const router = createBrowserRouter([
  {
    path: "/", // Root path
    element: <NavigateRoot />, // Root navigation component
    errorElement: <ErrorPage />, // Error page for handling route errors
    children: [
      {
        path: "", // Default child route
        element: <AppBar />, // AppBar layout component
        loader: AppBarLoader, // Loader function for fetching AppBar data
        children: tafRoute, // Nested TAF routes
      },
      {
        path: "login", // Login route
        element: <LoginPage />, // Login page component
      },
      {
        path: "register", // Registration route
        element: <RegisterPage />, // Registration page component
      },
    ],
  },
]);

export default router;
