import { Outlet, useLoaderData, useOutletContext } from "react-router-dom";
import { useNavigate, useLocation } from "react-router-dom";
import { Tabs, Tab } from "@mui/material";

export async function loader({ params }) {
    const mockTaf = [
        {
            id: params.idUE,
            name: "Programmation polyglotte",
            description: "TAF is a student association.",
            managerName: "Théo Le Calvar",
        },
        {
            id: "2",
            name: "Programmation fonctionnelle",
            description: "This is another student association.",
            managerName: "John Doe",
        },
        {
            id: "3",
            name: "Conférences",
            description: "Yet another student association.",
            managerName: "Alice Brown",
        },
    ];

    return mockTaf[params.idUE - 1];
}

export default function UE() {
    const ue = useLoaderData();
    const context = useOutletContext();

    const navigate = useNavigate();
    const location = useLocation();

    // Définition des routes et de leurs labels
    const tabs = [
        { label: "Général", path: "" },
        { label: "Paramètres", path: "settings" },
        { label: "Cours", path: "courses" },
    ];

    // Identifier l'onglet actif basé sur l'URL
    const tabIndex = tabs.findIndex((tab) => tab.path === location.pathname.split("/").pop())
    const currentTab = tabIndex === -1 ? 0 : tabIndex;

    // Changer l'URL lorsque l'utilisateur sélectionne un onglet
    const handleTabChange = (event, newValue) => {
        navigate(tabs[newValue].path);
    };

    return (
        <>
            <Tabs value={currentTab} onChange={handleTabChange} centered>
                {tabs.map((tab, index) => (
                    <Tab key={index} label={tab.label} />
                ))}
            </Tabs>
            <h1>{ue.name}</h1>
            <Outlet context={{ ...context, ue }} />
        </>
    );
}
