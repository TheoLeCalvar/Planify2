import Root from "./routes/root";
import LoginPage from "./routes/login";
import AppBarComponent from "./routes/layout/appBar";
import SideBar from "./routes/layout/sideBar";
import ErrorPage from "./routes/error-page";
import Calendar from "./routes/calendar";
import TAF from "./routes/taf";
import { loader as TAFLoader } from "./routes/taf";

import { createBrowserRouter } from "react-router-dom";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <Root />,
        errorElement: <ErrorPage />,
        children: [
            {
                path: "",
                element: <AppBarComponent />,
                children: [
                    {
                        path: "taf/:idTAF",
                        element: <TAF />,
                        loader: TAFLoader,
                        children: [
                            {
                                path: "calendar",
                                element: <Calendar />,
                            },{
                                path: "",
                                element: <SideBar />,
                            }
                        ],
                    },
                ],
            },
            {
                path: "login",
                element: <LoginPage />,
            },
        ],
    },
]);
