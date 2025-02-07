// React imports
import React from "react";
import { createBrowserRouter } from "react-router-dom";

// Routes components
import Root from "./routes/root";
import LoginPage from "./routes/login";
import AppBar from "./routes/layout/appBar";
import SideBar from "./routes/layout/sideBar";
import ErrorPage from "./routes/error-page";
import LessonsAvailability from "./routes/taf/lessonsAvailability";
import TAF from "./routes/taf";
import TAFSettings from "./routes/taf/settings";
import TAFResults from "./routes/taf/results";
import TAFPlanning from "./routes/taf/planning";
import UE from "./routes/ue";
import General from "./routes/ue/general";
import Settings from "./routes/ue/settings";
import Lessons from "./routes/ue/lessons";

// Loader functions
import { loader as TAFLoader } from "./routes/taf";
import { loader as AppBarLoader } from "./routes/layout/appBar";
import { loader as UELoader } from "./routes/ue";
import { loader as LessonsLoader } from "./routes/ue/lessons";
import { loader as LessonsAvailabilityLoader } from "./routes/taf/lessonsAvailability";
import { loader as TAFResultsLoader } from "./routes/taf/planning";
import { action as editUEAction } from "./routes/ue/settings";
import { action as editLessonsAction } from "./routes/ue/lessons";
import { action as editTAFCalendarAction } from "./routes/taf/lessonsAvailability";
import { action as editTAFSettingsAction } from "./routes/taf/settings";
import { action as createNewUserAction } from "../components/createUser";

// Define UE nested routes
const ueRoutes = [
  {
    index: true,
    element: <General />,
  },
  {
    path: "settings",
    element: <Settings />,
    action: editUEAction,
  },
  {
    path: "lessons",
    element: <Lessons />,
    loader: LessonsLoader,
    action: editLessonsAction,
    children: [
      {
        path: "createUser",
        action: createNewUserAction,
      },
    ],
  },
];

// Define TAF nested routes (displayed within the SideBar)
const tafRoutes = [
  {
    path: "calendar",
    element: <LessonsAvailability />,
    loader: LessonsAvailabilityLoader,
    action: editTAFCalendarAction,
  },
  {
    path: "settings",
    element: <TAFSettings />,
    action: editTAFSettingsAction,
  },
  {
    path: "results",
    element: <TAFResults />,
    children: [
      {
        path: ":idPlanning",
        element: <TAFPlanning />,
        loader: TAFResultsLoader,
      },
    ],
  },
  {
    path: "ue/:idUE",
    element: <UE />,
    loader: UELoader,
    children: ueRoutes,
  },
];

// Define the TAF route (with a nested SideBar)
const tafRoute = {
  path: "taf/:idTAF",
  element: <TAF />,
  loader: TAFLoader,
  children: [
    {
      path: "",
      element: <SideBar />,
      children: tafRoutes,
    },
  ],
};

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "",
        element: <AppBar />,
        loader: AppBarLoader,
        children: [tafRoute],
      },
      {
        path: "login",
        element: <LoginPage />,
      },
    ],
  },
]);
