import { Outlet, useLoaderData } from "react-router-dom";

export async function loader({ params }) {
  
    const mockTaf = {
        id: params.idTAF,
        name: "A tAF",
        description: "TAF is a student association.",
        managerName: "Théo Le Calvar",
        UE: [
            { id: 1, name: "UE A", responsible: "Squalala" },
            { id: 2, name: "UE B", responsible: "Jacques Noyé" },
            { id: 3, name: "UE C", responsible: "Gilles Simonin" },
        ]
    }


  return { ...mockTaf };
}

export default function TAF() {
  const taf = useLoaderData();
  // existing code

  return (
    <>
      <Outlet context={{taf}} />
    </>
  )
}
