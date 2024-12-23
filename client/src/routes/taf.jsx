import { Outlet, useLoaderData } from "react-router-dom";
import axiosInstance from "../services/axiosConfig";
import { USE_MOCK_DATA } from "../contants";

export async function loader({ params }) {
    if (USE_MOCK_DATA) {
        const mockTaf = [
            {
                id: params.idTAF,
                name: "A tAF",
                description: "TAF is a student association.",
                managerName: "Théo Le Calvar",
                UE: [
                    { id: 1, name: "UE A", responsible: "Squalala" },
                    { id: 2, name: "UE B", responsible: "Jacques Noyé" },
                    { id: 3, name: "UE C", responsible: "Gilles Simonin" },
                ],
                startDate: "2024-09-07",
                endDate: "2025-03-30",
            },
            {
                id: "2",
                name: "Another TAF",
                description: "This is another student association.",
                managerName: "John Doe",
                UE: [
                    { id: 4, name: "UE D", responsible: "Jane Smith" },
                    { id: 5, name: "UE E", responsible: "John Smith" },
                    { id: 6, name: "UE F", responsible: "Alice Johnson" },
                ],
            },
            {
                id: "3",
                name: "Yet Another TAF",
                description: "Yet another student association.",
                managerName: "Alice Brown",
                UE: [
                    { id: 7, name: "UE G", responsible: "Bob Brown" },
                    { id: 8, name: "UE H", responsible: "Charlie Davis" },
                    { id: 9, name: "UE I", responsible: "Diana Evans" },
                ],
            },
        ];

        return mockTaf[params.idTAF - 1];
    }

    const response = await axiosInstance.get(`/taf/${params.idTAF}`);
    return response.data;
}

export default function TAF() {
    const taf = useLoaderData();

    return (
        <>
            <Outlet context={{ taf }} />
        </>
    );
}
