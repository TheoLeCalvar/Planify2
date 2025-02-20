import React from "react";
import { Outlet, useOutletContext } from "react-router-dom";

const UEGeneral = () => {
  const context = useOutletContext();

  const ue = context.ue;

  return (
    <>
      <h1>Général</h1>
      <h2>{ue.name}</h2>
      <p>{ue.description}</p>
      <Outlet context={context} />
    </>
  );
};

export default UEGeneral;
