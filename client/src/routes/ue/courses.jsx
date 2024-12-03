import { Outlet, useLoaderData, useOutletContext } from "react-router-dom";
import BlockManager from "../../components/courses/BlockManager";

export async function loader({ params }) {
    const mockData = [
        {
            id: 1,
            name: "Bloc 1",
            description: "Description du bloc 1",
            courses: [
                {
                    id: 1,
                    name: "Cours 1",
                    description: "Description du cours 1",
                },
                {
                    id: 2,
                    name: "Cours 2",
                    description: "Description du cours 2",
                },
            ],
        },
        {
            id: 2,
            name: "Bloc 2",
            description: "Description du bloc 2",
            courses: [
                {
                    id: 3,
                    name: "Cours 3",
                    description: "Description du cours 3",
                },
                {
                    id: 4,
                    name: "Cours 4",
                    description: "Description du cours 4",
                },
            ],
        },
        {
            id: 3,
            name: "Bloc 3",
            description: "Description du bloc 3",
            courses: [
                {
                    id: 5,
                    name: "Cours 5",
                    description: "Description du cours 5",
                },
                {
                    id: 6,
                    name: "Cours 6",
                    description: "Description du cours 6",
                },
            ],
        },
    ];

    return mockData;
}

export default function UECourses() {
    const data = useLoaderData();
    const context = useOutletContext();

    return (
        <>
            <h1>Cours</h1>
            <BlockManager/>
            <Outlet context={context} />
        </>
    );
}
