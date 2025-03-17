// ConfigFieldGroupHeader.jsx
// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Stack, Typography } from "@mui/material";

// Import the shared styles
import styles from "./ConfigField.styles";

/**
 * ConfigFieldGroupHeader Component
 * This component renders the header for a configuration field group.
 * It displays the title of the group and an optional description.
 *
 * @param {Object} props - The component props.
 * @param {string} props.title - The title of the configuration field group.
 * @param {string} [props.description] - An optional description for the group.
 * @returns {JSX.Element} - The rendered ConfigFieldGroupHeader component.
 */
const ConfigFieldGroupHeader = ({ title, description }) => {
  return (
    <Stack sx={styles.headerStack}>
      {/* Render the title of the field group */}
      <Typography variant="h6">{title}</Typography>

      {/* Render the description if provided */}
      {description && (
        <Typography variant="body2" color="text.secondary">
          {description}
        </Typography>
      )}
    </Stack>
  );
};

// Define the expected prop types for the component
ConfigFieldGroupHeader.propTypes = {
  /**
   * The title of the configuration field group.
   * This is a required string.
   */
  title: PropTypes.string.isRequired,

  /**
   * An optional description for the configuration field group.
   * This is a string.
   */
  description: PropTypes.string,
};

export default ConfigFieldGroupHeader;
