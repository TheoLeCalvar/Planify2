// React imports
import React from "react";
import { useRouteError } from "react-router-dom";

export default function ErrorPage() {
  const error = useRouteError();
  console.error("Erreur capturée par React Router", error);

  return (
    <div id="error-page">
      <h1>Oups!</h1>
      <p>Une erreur imprévue est survenue...</p>
      <p>Essayez de rafraîchir la page.</p>
      <h3>Détails :</h3>
      <p>
        <i>{error.statusText || error.message}</i>
      </p>
    </div>
  );
}
