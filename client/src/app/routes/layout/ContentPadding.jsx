// React imports
import React from "react";
import { Outlet } from "react-router-dom"; // Used to render child routes

// Material-UI imports
import { Box } from "@mui/material"; // Box component for layout and styling

/**
 * ContentPadding component.
 * This component provides consistent padding for the content area of the application.
 * It uses Material-UI's Box component to apply padding and flex-grow styling.
 *
 * The `Outlet` component is used to render nested child routes within this layout.
 *
 * @returns {JSX.Element} - The rendered component with padding applied.
 */
const ContentPadding = () => {
  return (
    <Box sx={{ flexGrow: 1, padding: 3 }}>
      {/* Renders child routes inside the padded box */}
      <Outlet />
    </Box>
  );
};

export default ContentPadding;
