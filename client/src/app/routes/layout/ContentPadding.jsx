// React imports
import React from "react";
import { Outlet } from "react-router-dom";

// Material-UI imports
import { Box } from "@mui/material";

// just a box with padding for the content
const ContentPadding = () => {
  return (
    <Box sx={{ flexGrow: 1, padding: 3 }}>
      <Outlet />
    </Box>
  );
};

export default ContentPadding;