import {
    Outlet,
    useLoaderData,
    useOutletContext,
    useFetcher,
} from "react-router-dom";
import BlockManager from "../../components/courses/BlockManager";
import { useState } from "react";
import LoadingButton from "@mui/lab/LoadingButton";
import SaveIcon from "@mui/icons-material/Save";
import { Fab } from "@mui/material";
import { LessonsContext } from "../../context/LessonsContext";

export async function loader({ params }) {
    const mockData = [
        {
            id: 1,
            title: "Bloc 1",
            description: "Description du bloc 1",
            courses: [
                {
                    id: 1,
                    title: "Cours 1",
                    description: "Description du cours 1",
                },
                {
                    id: 2,
                    title: "Cours 2",
                    description: "Description du cours 2",
                },
            ],
            dependencies: [],
        },
        {
            id: 2,
            title: "Bloc 2",
            description: "Description du bloc 2",
            courses: [
                {
                    id: 3,
                    title: "Cours 3",
                    description: "Description du cours 3",
                },
                {
                    id: 4,
                    title: "Cours 4",
                    description: "Description du cours 4",
                },
            ],
            dependencies: [1],
        },
        {
            id: 3,
            title: "Bloc 3",
            description: "Description du bloc 3",
            courses: [
                {
                    id: 5,
                    title: "Cours 5",
                    description: "Description du cours 5",
                },
                {
                    id: 6,
                    title: "Cours 6",
                    description: "Description du cours 6",
                },
            ],
            dependencies: [1, 2],
        },
    ];

    const users = [{
        id: 1,
        name: "John Doe",
        alreadySelected: false
    },
    {
        id: 2,
        name: "Jacques Noyé",
        alreadySelected: true
    },
    {
        id: 3,
        name: "Foo Bar",
        alreadySelected: true
    },
    {
        id: 4,
        name: "Baz Qux",
        alreadySelected: false
    }];

    return {lessons: mockData, users};
}

export async function action({ request }) {
    let data = await request.json();
    console.log(data);
    await new Promise((res) => setTimeout(res, 1000));
    return { ok: true };
}

export default function UECourses() {
    const {lessons: data, users} = useLoaderData();
    const context = useOutletContext();
    const fetcher = useFetcher();

    const busy = fetcher.state !== "idle";

    const [coursesData, setCoursesData] = useState(data ?? []);
    const [dependencyError, setDependencyError] = useState(null);
    const [lecturersList, setLecturersList] = useState(users ?? []);

    return (
        <>
            <h1>Cours</h1>
            <LessonsContext.Provider value={{lecturersList, setLecturersList}}>
                <BlockManager
                    coursesData={coursesData}
                    setCoursesData={setCoursesData}
                    dependencyError={dependencyError}
                    setDependencyError={setDependencyError}
                />
            </LessonsContext.Provider>
            <Fab
                variant="extended"
                size="large"
                color="primary"
                onClick={() =>
                    fetcher.submit(coursesData, {
                        encType: "application/json",
                        method: "post",
                    })
                }
                disabled={!!dependencyError || busy}
                sx={{
                    position: "fixed",
                    right: 32,
                    bottom: 32,
                }}
            >
                {busy ? (
                    <LoadingButton loading loadingPosition="start" startIcon={<SaveIcon />}>
                        Sauvegarde...
                    </LoadingButton>
                ) : (
                    <>
                        <SaveIcon sx={{ mr: 1 }}/>
                        Sauvegarder
                    </>
                )}
            </Fab>
            <Outlet context={context} />
        </>
    );
}
