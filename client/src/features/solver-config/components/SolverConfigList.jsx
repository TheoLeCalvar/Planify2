// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { IconButton, Stack, Tooltip, Typography } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

// Local imports
import ConfigFieldGroup from "./ConfigFieldGroup"; // Component for rendering a group of configuration fields

/**
 * ConfigSectionList Component
 * This component renders a list of configuration sections.
 * Each section contains a header and a group of configuration fields.
 *
 * @param {Object} props - The component props.
 * @param {string} props.title - The title of the configuration list.
 * @param {string} props.tooltip - The tooltip text for the configuration list.
 * @param {Array} props.sections - The list of configuration sections to render.
 * @param {Object} props.formData - The form data object for managing field values.
 * @returns {JSX.Element} - The rendered ConfigSectionList component.
 */
const ConfigSectionList = ({ title, tooltip, sections, formData }) => (
  <>
    {/* Header with title and tooltip */}
    <Stack direction="row" alignItems="center" spacing={10}>
      {/* Tooltip with an info icon */}
      <Tooltip title={tooltip}>
        <IconButton size="small">
          <InfoIcon fontSize="inherit" />
        </IconButton>
      </Tooltip>

      {/* Title of the configuration list */}
      <Typography variant="h6">{title}</Typography>
    </Stack>

    {/* Render each configuration section */}
    <Stack spacing={5}>
      {sections.map((section) => (
        <ConfigFieldGroup
          key={section.title} // Unique key for each section
          config={section} // Configuration object for the section
          formData={formData} // Form data for managing field values
        />
      ))}
    </Stack>
  </>
);

// Define the expected prop types for the component
ConfigSectionList.propTypes = {
  /**
   * The title of the configuration list.
   * This is a required string.
   */
  title: PropTypes.string.isRequired,

  /**
   * The tooltip text for the configuration list.
   * This is a required string.
   */
  tooltip: PropTypes.string.isRequired,

  /**
   * The list of configuration sections to render.
   * Each section contains a title and an array of fields.
   */
  sections: PropTypes.arrayOf(
    PropTypes.shape({
      title: PropTypes.string.isRequired, // The title of the section
      fields: PropTypes.arrayOf(PropTypes.object).isRequired, // The fields in the section
    }),
  ).isRequired,

  /**
   * The form data object for managing field values.
   * Contains the current values of the fields in the form.
   */
  formData: PropTypes.object,
};

export default ConfigSectionList;
