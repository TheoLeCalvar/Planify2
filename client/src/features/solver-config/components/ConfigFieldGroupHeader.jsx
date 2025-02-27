// ConfigFieldGroupHeader.jsx
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Stack, Typography } from "@mui/material";

// Import the shared styles
import styles from "./ConfigField.styles";

const ConfigFieldGroupHeader = ({ title, description }) => {
  return (
    <Stack sx={styles.headerStack}>
      <Typography variant="h6">{title}</Typography>
      {description && (
        <Typography variant="body2" color="text.secondary">
          {description}
        </Typography>
      )}
    </Stack>
  );
};

ConfigFieldGroupHeader.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
};

export default ConfigFieldGroupHeader;
