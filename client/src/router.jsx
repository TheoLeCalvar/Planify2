import Root from "./routes/root";
import LoginPage from "./routes/login";
import AppBarComponent from "./routes/layout/appBar";
import SideBar from "./routes/layout/sideBar";
import ErrorPage from "./routes/error-page";
import CoursesAvailability from "./routes/coursesAvailability";
import TAF from "./routes/taf";
import UE from "./routes/ue";
import Settings from "./routes/ue/settings";
import Courses from "./routes/ue/courses";
import General from "./routes/ue/general";
import { loader as TAFLoader } from "./routes/taf";
import { loader as AppBarLoader } from "./routes/layout/appBar";
import { loader as UELoader } from "./routes/ue";
import { loader as CoursesLoader } from "./routes/ue/courses";
import { action as editUEAction } from "./routes/ue/settings";
import { action as editCoursesAction } from "./routes/ue/courses";
import { action as editTAFCalendarAction } from "./routes/coursesAvailability"

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
                                path: "",
                                element: <SideBar />,
                                children: [
                                    {
                                        path: "calendar",
                                        element: <CoursesAvailability />,
                                        action: editTAFCalendarAction
                                    },
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
                                                action: editUEAction,
                                                element: <Settings />,
                                            },
                                            {
                                                path: "courses",
                                                loader: CoursesLoader,
                                                action: editCoursesAction,
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
