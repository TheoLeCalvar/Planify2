// React imports
import React from "react";
import { createBrowserRouter } from "react-router-dom";

// Routes components
import LoginPage from "./routes/Login";
import AppBar from "./routes/layout/AppBar";
import SideBar from "./routes/layout/SideBar";
import ErrorPage from "./routes/ErrorPage";
import TAF from "./routes/Taf";
import RegisterPage from "./routes/Register";
import NavigateRoot from "./routes/NavigateRoot";

// Loader functions
import { loaderLecturer as TAFLoader } from "./routes/Taf";
import { loaderLecturer as AppBarLoader } from "./routes/layout/AppBar";
import { loader as AvailibilityLoader } from "./routes/lecturer/LecturerAvailability";
import { action as AvailibilityAction } from "./routes/lecturer/LecturerAvailability";
import LecturerAvailability from "./routes/lecturer/LecturerAvailability";

// Define the TAF route (with a nested SideBar)
const tafRoute = [
  {
    path: "taf/:idTAF",
    element: <TAF />,
    loader: TAFLoader,
    action: AvailibilityAction,
    children: [
      {
        path: "",
        element: <SideBar />,
        children: [
          {
            path: "",
            element: <LecturerAvailability />,
            loader: AvailibilityLoader,
          },
        ],
      },
    ],
  },
];

export const router = createBrowserRouter([
  {
    path: "/",
    element: <NavigateRoot />,
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
        path: "register",
        element: <RegisterPage />,
      },
    ],
  },
]);

export default router;
