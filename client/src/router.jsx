import Root from "./routes/root";
import LoginPage from "./routes/login";
import AppBarComponent from "./routes/layout/appBar";
import SideBar from "./routes/layout/sideBar";
import ErrorPage from "./routes/error-page";
import Calendar from "./routes/calendar";
import TAF from "./routes/taf";
import UE from "./routes/ue";
import Settings from "./routes/ue/settings";
import Courses from "./routes/ue/courses";
import General from "./routes/ue/general";
import { loader as TAFLoader } from "./routes/taf";
import { loader as AppBarLoader } from "./routes/layout/appBar";
import { loader as UELoader } from "./routes/ue";

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
                loader: AppBarLoader,
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
                                children: [
                                    {
                                        path: "ue/:idUE",
                                        element: <UE />,
                                        loader: UELoader,
                                        children: [
                                            {
                                                index: true,
                                                element: <General />,
                                            },
                                            {
                                                path: "settings",
                                                element: <Settings />,
                                            },
                                            {
                                                path: "courses",
                                                element: <Courses />,
                                            },
                                        ],
                                    }
                                ]
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
