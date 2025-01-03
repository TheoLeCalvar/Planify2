import Root from "./routes/root";
import LoginPage from "./routes/login";
import AppBarComponent from "./routes/layout/appBar";
import SideBar from "./routes/layout/sideBar";
import ErrorPage from "./routes/error-page";
import LessonsAvailability from "./routes/taf/lessonsAvailability";
import TAF from "./routes/taf";
import UE from "./routes/ue";
import Settings from "./routes/ue/settings";
import Lessons from "./routes/ue/lessons";
import General from "./routes/ue/general";
import { loader as TAFLoader } from "./routes/taf";
import { loader as AppBarLoader } from "./routes/layout/appBar";
import { loader as UELoader } from "./routes/ue";
import { loader as LessonsLoader } from "./routes/ue/lessons";
import { action as editUEAction } from "./routes/ue/settings";
import { action as editLessonsAction } from "./routes/ue/lessons";
import { action as editTAFCalendarAction } from "./routes/taf/lessonsAvailability"
import { action as editTAFSettingsAction } from "./routes/taf/settings";

import { createBrowserRouter } from "react-router-dom";
import TAFSettings from "./routes/taf/settings";

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
                                        element: <LessonsAvailability />,
                                        action: editTAFCalendarAction
                                    },
                                    {
                                        path: "settings",
                                        element: <TAFSettings />,
                                        action: editTAFSettingsAction
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
                                                path: "lessons",
                                                loader: LessonsLoader,
                                                action: editLessonsAction,
                                                element: <Lessons />,
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
