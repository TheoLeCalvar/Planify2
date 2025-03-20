import React, { useEffect } from "react";
import { Box, Card, CardContent, Typography } from "@mui/material";
import PropTypes from "prop-types";

export default function EmptyStateMessage({
  messagePrimary,
  messageSecondary,
  centerPage = true,
}) {
  useEffect(() => {
    // Empêche le scroll sur le body quand le composant est monté
    document.body.style.overflow = "hidden";

    return () => {
      // Réactive le scroll quand le composant est démonté
      document.body.style.overflow = "auto";
    };
  }, []);

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      minHeight={centerPage ? "100vh" : "0"}
      bgcolor="#f5f5f5"
    >
      <Card
        sx={{
          padding: 4,
          borderRadius: 4,
          boxShadow: 3,
          minWidth: 300,
          textAlign: "center",
          backgroundColor: "white",
        }}
      >
        <CardContent>
          <Typography variant="h6" color="text.primary" gutterBottom>
            {messagePrimary}
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {messageSecondary}
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
}

EmptyStateMessage.propTypes = {
  messagePrimary: PropTypes.string.isRequired,
  messageSecondary: PropTypes.string,
  centerPage: PropTypes.bool,
};
