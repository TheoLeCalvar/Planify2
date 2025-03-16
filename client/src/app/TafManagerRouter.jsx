// React imports
import React from "react";
import { createBrowserRouter } from "react-router-dom"; // React Router function for creating a browser-based router

// Routes components
import LoginPage from "./routes/Login"; // Login page component
import AppBar from "./routes/layout/AppBar"; // AppBar layout component
import SideBar from "./routes/layout/SideBar"; // SideBar layout component
import ErrorPage from "./routes/ErrorPage"; // Error page component
import LessonsAvailability from "./routes/taf/LessonsAvailability"; // Lessons availability component
import TAF from "./routes/Taf"; // TAF route component
import TAFSettings from "./routes/taf/Settings"; // TAF settings component
import TAFResults from "./routes/taf/ResultsRouter"; // TAF results router
import TAFPlanning from "./routes/taf/Result"; // TAF planning result component
import UE from "./routes/Ue"; // UE route component
import General from "./routes/ue/General"; // General UE information component
import Settings from "./routes/ue/Settings"; // UE settings component
import Lessons from "./routes/ue/Lessons"; // UE lessons component
import TAFAllSettings from "./routes/taf/SettingsRouter"; // TAF settings router
import SolverConfigSelector from "./routes/taf/SolverConfigSelector"; // Solver configuration selector component
import ContentPadding from "./routes/layout/ContentPadding"; // Layout component for padding
import SolverConfig from "./routes/taf/SolverConfig"; // Solver configuration component
import GeneratePlanning from "./routes/GeneratePlanning"; // Generate planning component
import RegisterPage from "./routes/Register"; // Registration page component
import NavigateRoot from "./routes/NavigateRoot"; // Root navigation component

// Loader functions
import { loader as TAFLoader } from "./routes/Taf"; // Loader for fetching TAF data
import { loader as AppBarLoader } from "./routes/layout/AppBar"; // Loader for fetching AppBar data
import { loader as UELoader } from "./routes/Ue"; // Loader for fetching UE data
import { loader as LessonsLoader } from "./routes/ue/Lessons"; // Loader for fetching UE lessons data
import { loader as LessonsAvailabilityLoader } from "./routes/taf/LessonsAvailability"; // Loader for fetching lessons availability data
import { loader as TAFResultsLoader } from "./routes/taf/Result"; // Loader for fetching TAF results
import { loader as SolverConfigSelectorLoader } from "./routes/taf/SolverConfigSelector"; // Loader for fetching solver configurations
import { loader as SolverConfigLoader } from "./routes/taf/SolverConfig"; // Loader for fetching solver configuration details
import { loader as TAFGeneratePlanningLoader } from "./routes/GeneratePlanning"; // Loader for fetching planning generation data

// Action functions
import { action as editUEAction } from "./routes/ue/Settings"; // Action for editing UE settings
import { action as editLessonsAction } from "./routes/ue/Lessons"; // Action for editing UE lessons
import { action as editTAFCalendarAction } from "./routes/taf/LessonsAvailability"; // Action for editing TAF calendar
import { action as editTAFSettingsAction } from "./routes/taf/Settings"; // Action for editing TAF settings
import { action as createNewUserAction } from "@/components/CreateUser"; // Action for creating a new user
import { action as editTAFConfigAction } from "./routes/taf/SolverConfig"; // Action for editing solver configurations

/**
 * Define UE nested routes.
 * These routes handle UE-related pages, such as general information, settings, lessons, and solver configurations.
 */
const ueRoutes = [
  {
    index: true,
    element: <General />, // General UE information
  },
  {
    path: "settings",
    element: <Settings />, // UE settings
    action: editUEAction, // Action for editing UE settings
  },
  {
    path: "lessons",
    element: <Lessons />, // UE lessons
    loader: LessonsLoader, // Loader for fetching lessons data
    action: editLessonsAction, // Action for editing lessons
  },
  {
    path: "config",
    element: <SolverConfigSelector />, // Solver configuration selector
    loader: SolverConfigSelectorLoader, // Loader for fetching solver configurations
    children: [
      {
        path: "new",
        element: <SolverConfig />, // New solver configuration
        loader: SolverConfigLoader, // Loader for fetching configuration details
        action: editTAFConfigAction, // Action for editing configurations
      },
      {
        path: ":idConfig",
        element: <SolverConfig />, // Edit existing solver configuration
        loader: SolverConfigLoader, // Loader for fetching configuration details
        action: editTAFConfigAction, // Action for editing configurations
      },
    ],
  },
];

/**
 * Define TAF nested routes (displayed within the SideBar).
 * These routes handle TAF-related pages, such as calendar, settings, planning generation, results, and UE management.
 */
const tafRoutes = [
  {
    path: "calendar",
    element: <LessonsAvailability />, // TAF calendar for lessons availability
    loader: LessonsAvailabilityLoader, // Loader for fetching calendar data
    action: editTAFCalendarAction, // Action for editing calendar
  },
  {
    path: "settings",
    element: <TAFAllSettings />, // TAF settings router
    children: [
      {
        element: <TAFSettings />, // Default TAF settings
        index: true,
        action: editTAFSettingsAction, // Action for editing TAF settings
      },
      {
        path: "config",
        element: <SolverConfigSelector />, // Solver configuration selector
        loader: SolverConfigSelectorLoader, // Loader for fetching solver configurations
        children: [
          {
            path: "new",
            element: <SolverConfig />, // New solver configuration
            loader: SolverConfigLoader, // Loader for fetching configuration details
            action: editTAFConfigAction, // Action for editing configurations
          },
          {
            path: ":idConfig",
            element: <SolverConfig />, // Edit existing solver configuration
            loader: SolverConfigLoader, // Loader for fetching configuration details
            action: editTAFConfigAction, // Action for editing configurations
          },
        ],
      },
    ],
  },
  {
    path: "generate",
    element: <GeneratePlanning />, // Planning generation page
    loader: TAFGeneratePlanningLoader, // Loader for fetching planning generation data
  },
  {
    path: "results",
    element: <TAFResults />, // TAF results router
    children: [
      {
        path: ":idPlanning",
        element: <TAFPlanning />, // Specific planning result
        loader: TAFResultsLoader, // Loader for fetching planning result data
      },
    ],
  },
  {
    path: "ue/:idUE",
    element: <UE />, // UE management
    loader: UELoader, // Loader for fetching UE data
    children: ueRoutes, // Nested UE routes
  },
  {
    path: "ue/new",
    element: <Settings />, // Create a new UE
    action: editUEAction, // Action for creating a new UE
  },
];

/**
 * Define the TAF route (with a nested SideBar).
 * This route handles TAF-related pages for TAF managers.
 */
const tafRoute = [
  {
    path: "taf/:idTAF",
    element: <TAF />, // Main TAF component
    loader: TAFLoader, // Loader for fetching TAF data
    children: [
      {
        path: "",
        element: <SideBar />, // SideBar layout
        children: tafRoutes, // Nested TAF routes
      },
    ],
  },
  {
    path: "",
    element: <ContentPadding />, // Layout with padding
    children: [
      {
        path: "taf/new",
        element: <TAFSettings />, // Create a new TAF
        action: editTAFSettingsAction, // Action for creating a new TAF
      },
    ],
  },
];

/**
 * Create the main router for the application.
 * This router defines the routes for TAF managers, including login, registration, and TAF-related pages.
 */
export const router = createBrowserRouter([
  {
    path: "/",
    element: <NavigateRoot />, // Root navigation component
    errorElement: <ErrorPage />, // Error page for handling route errors
    children: [
      {
        path: "",
        element: <AppBar />, // AppBar layout
        loader: AppBarLoader, // Loader for fetching AppBar data
        children: tafRoute, // Nested TAF routes
      },
      {
        path: "login",
        element: <LoginPage />, // Login page
      },
      {
        path: "register",
        element: <RegisterPage />, // Registration page
      },
      {
        path: "createUser",
        action: createNewUserAction, // Action for creating a new user
      },
    ],
  },
]);

export default router;
