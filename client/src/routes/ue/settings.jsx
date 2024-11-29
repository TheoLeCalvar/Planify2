import { Outlet, useLoaderData, useOutletContext } from "react-router-dom";

export async function loader({ params }) {
    const mockData = [
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
        }
    ];

    return mockData;
}

export default function UESettings() {
    const context = useOutletContext();

    return (
        <>
            <h1>Paramètres</h1>
            <Outlet context={context} />
        </>
    );
}
