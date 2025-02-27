// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Stack, Typography } from "@mui/material";

const ConfigFieldHeader = ({ title, description }) => {
  return (
    <Stack direction="row" spacing={3} alignItems="center" mt={2}>
      <Typography variant="h6">{title}</Typography>
      {description && (
        <Typography variant="body2" color="text.secondary">
          {description}
        </Typography>
      )}
    </Stack>
  );
};

ConfigFieldHeader.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
};

export default ConfigFieldHeader;
