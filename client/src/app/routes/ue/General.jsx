// React imports
import React from "react";
import { Outlet, useOutletContext } from "react-router-dom"; // React Router hooks for nested routes and context

/**
 * UEGeneral component.
 * This component displays general information about a specific UE (Unité d'Enseignement).
 * It retrieves the UE data from the parent route's context and renders its name and description.
 * Additionally, it renders nested routes using the `Outlet` component.
 *
 * @returns {JSX.Element} - The rendered component.
 */
const UEGeneral = () => {
  const context = useOutletContext(); // Access context data from the parent route

  const ue = context.ue; // Retrieve UE data from the context

  return (
    <>
      {/* Header section */}
      <h1>Général</h1>
      {/* Display the name of the UE */}
      <h2>{ue.name}</h2>
      {/* Display the description of the UE */}
      <p>{ue.description}</p>
      {/* Render nested routes with the same context */}
      <Outlet context={context} />
    </>
  );
};

export default UEGeneral;
