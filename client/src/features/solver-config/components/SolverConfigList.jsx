// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { IconButton, Stack, Tooltip, Typography } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

// Local imports
import ConfigFieldGroup from "./ConfigFieldGroup";

// Helper component for displaying header and mapping sections
const ConfigSectionList = ({ title, tooltip, sections, formData }) => (
  <>
    <Stack direction="row" alignItems="center" spacing={1}>
      <Tooltip title={tooltip}>
        <IconButton size="small">
          <InfoIcon fontSize="inherit" />
        </IconButton>
      </Tooltip>
      <Typography variant="h6">{title}</Typography>
    </Stack>
    {sections.map((section) => (
      <ConfigFieldGroup
        key={section.title}
        config={section}
        formData={formData}
      />
    ))}
  </>
);

ConfigSectionList.propTypes = {
  title: PropTypes.string.isRequired,
  tooltip: PropTypes.string.isRequired,
  sections: PropTypes.arrayOf(
    PropTypes.shape({
      title: PropTypes.string.isRequired,
      fields: PropTypes.arrayOf(PropTypes.object).isRequired,
    }),
  ).isRequired,
  formData: PropTypes.object,
};
