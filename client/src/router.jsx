import Root from "./routes/root";
import LoginPage from "./routes/login";
import AppBarComponent from "./routes/layout/appBar";
import SideBar from "./routes/layout/sideBar";
import ErrorPage from "./routes/error-page";
import Calendar from "./routes/calendar";

import { createBrowserRouter, RouterProvider } from "react-router-dom";


export const router = createBrowserRouter([
    {
        path: "/",
        element: <AppBarComponent />,
        errorElement: <ErrorPage />,
        children: [
          {
            path: "",
            element: <SideBar />,
            children: [
              {
                path: "calendar",
                element: <Calendar />
              }
            ]
          }
        ]
    },
    {
      path: "/login",
      element: <LoginPage />,
    }
]);