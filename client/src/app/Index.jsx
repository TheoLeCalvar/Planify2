// React imports
import React from "react";
import ReactDOM from "react-dom/client"; // ReactDOM for rendering the root component

// Local imports
import "@/assets/index.css"; // Global CSS styles
import reportWebVitals from "./reportWebVitals"; // Utility for measuring app performance
import Root from "./routes/Root"; // Root component of the application

/**
 * Entry point of the React application.
 * This file initializes the React app and renders the root component into the DOM.
 */

// Create the root element for rendering the React app
const root = ReactDOM.createRoot(document.getElementById("root")); // Get the root DOM element by its ID

// Render the application
root.render(
  <React.StrictMode>
    <Root /> {/* Render the Root component, which wraps the entire app */}
  </React.StrictMode>,
);

/**
 * Optional: Measure performance in the app.
 * To start measuring performance, pass a function to log results (e.g., `reportWebVitals(console.log)`),
 * or send the results to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
 */
reportWebVitals();
