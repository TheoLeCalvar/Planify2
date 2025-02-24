// React imports
import React from "react";
import { createBrowserRouter } from "react-router-dom";

// Routes components
import Root from "./routes/Root";
import LoginPage from "./routes/Login";
import AppBar from "./routes/layout/AppBar";
import SideBar from "./routes/layout/SideBar";
import ErrorPage from "./routes/ErrorPage";
import LessonsAvailability from "./routes/taf/LessonsAvailability";
import TAF from "./routes/Taf";
import TAFSettings from "./routes/taf/Settings";
import TAFResults from "./routes/taf/Results";
import TAFPlanning from "./routes/taf/Planning";
import UE from "./routes/Ue";
import General from "./routes/ue/General";
import Settings from "./routes/ue/Settings";
import Lessons from "./routes/ue/Lessons";
import TAFAllSettings from "./routes/taf/SettingsRouter";
import TAFConfigs from "./routes/taf/Configs";
import ContentPadding from "./routes/layout/ContentPadding";

// Loader functions
import { loader as TAFLoader } from "./routes/Taf";
import { loader as AppBarLoader } from "./routes/layout/AppBar";
import { loader as UELoader } from "./routes/Ue";
import { loader as LessonsLoader } from "./routes/ue/Lessons";
import { loader as LessonsAvailabilityLoader } from "./routes/taf/LessonsAvailability";
import { loader as TAFResultsLoader } from "./routes/taf/Planning";
import { loader as TAFConfigsLoader } from "./routes/taf/Configs";
import { loader as TAFConfigLoader } from "./routes/taf/Config";
import { action as editUEAction } from "./routes/ue/Settings";
import { action as editLessonsAction } from "./routes/ue/Lessons";
import { action as editTAFCalendarAction } from "./routes/taf/LessonsAvailability";
import { action as editTAFSettingsAction } from "./routes/taf/Settings";
import { action as createNewUserAction } from "@/components/CreateUser";
import { action as editTAFConfigAction } from "./routes/taf/Config";
import TAFConfig from "./routes/taf/Config";

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
  },
  {
    path: "config",
    element: <TAFConfigs />,
    loader: TAFConfigsLoader,
    children: [
      {
        path: "new",
        element: <TAFConfig />,
        action: editTAFConfigAction,
      },
      {
        path: ":idConfig",
        element: <TAFConfig />,
        loader: TAFConfigLoader,
        action: editTAFConfigAction,
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
    element: <TAFAllSettings />,
    children: [
      {
        element: <TAFSettings />,
        index: true,
        action: editTAFSettingsAction,
      },
      {
        path: "config",
        element: <TAFConfigs />,
        loader: TAFConfigsLoader,
        children: [
          {
            path: "new",
            element: <TAFConfig />,
            action: editTAFConfigAction,
          },
          {
            path: ":idConfig",
            element: <TAFConfig />,
            loader: TAFConfigLoader,
            action: editTAFConfigAction,
          },
        ],
      },
    ],
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
  {
    path: "ue/new",
    element: <Settings />,
    action: editUEAction,
  },
];

// Define the TAF route (with a nested SideBar)
const tafRoute = [
  {
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
  },
  {
    path: "",
    element: <ContentPadding />,
    children: [
      {
        path: "taf/new",
        element: <TAFSettings />,
        action: editTAFSettingsAction,
      },
    ],
  },
];

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
        children: tafRoute,
      },
      {
        path: "login",
        element: <LoginPage />,
      },
      {
        path: "createUser",
        action: createNewUserAction,
      },
    ],
  },
]);
