// React imports
import React from "react";
import { useRouteError } from "react-router-dom"; // Hook for accessing route-specific errors

/**
 * ErrorPage component.
 * This component is displayed when an error occurs in a route managed by React Router.
 * It captures and displays error details to help users and developers understand the issue.
 *
 * @returns {JSX.Element} - The rendered error page.
 */
export default function ErrorPage() {
  const error = useRouteError(); // Retrieve the error object from React Router
  console.error("Erreur capturée par React Router", error); // Log the error for debugging purposes

  return (
    <div id="error-page">
      {/* Header section */}
      <h1>Oups!</h1>
      <p>Une erreur imprévue est survenue...</p>
      <p>Essayez de rafraîchir la page.</p>

      {/* Display error details */}
      <h3>Détails :</h3>
      <p>
        <i>{error.statusText || error.message}</i>{" "}
        {/* Show the error message or status text */}
      </p>
    </div>
  );
}
