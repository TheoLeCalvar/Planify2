import Root from "./routes/root";
import ErrorPage from "./routes/error-page";

import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Calendar from "./routes/calendar";


export const router = createBrowserRouter([
    {
        path: "/",
        element: <Root />,
        errorElement: <ErrorPage />,
    },
    {
      path: "/calendar",
      element: <Calendar />,
    }
]);