// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Box, Stack } from "@mui/material";

// Local imports
import ConfigFieldGroupHeader from "./ConfigFieldGroupHeader"; // Component for rendering the group header
import ConfigFieldInput from "./ConfigFieldInput"; // Component for rendering text or number input fields
import ConfigFieldSelect from "./ConfigFieldSelect"; // Component for rendering select dropdown fields
import ConfigFieldTime from "./ConfigFieldTime"; // Component for rendering time input fields
import styles from "./ConfigField.styles"; // Styles for the field group

/**
 * ConfigFieldGroup Component
 * This component renders a group of configuration fields based on the provided configuration object.
 * It supports multiple field types such as text, number, select, and time.
 *
 * @param {Object} props - The component props.
 * @param {Object} props.config - The configuration object for the field group.
 * @param {Object} props.formData - The form data object for managing field values.
 * @returns {JSX.Element} - The rendered ConfigFieldGroup component.
 */
const ConfigFieldGroup = ({ config, formData }) => {
  return (
    <Box>
      {/* Render the group header with title and description */}
      <ConfigFieldGroupHeader
        title={config.title}
        description={config.description}
      />

      {/* Render the fields in a vertical stack */}
      <Stack sx={styles.fieldStack}>
        {config.fields.map((field) => {
          // Render the appropriate field component based on the field type
          switch (field.type) {
            case "select":
              return (
                <ConfigFieldSelect
                  key={field.name} // Unique key for React rendering
                  field={field} // Field configuration
                  formData={formData} // Form data for managing values
                />
              );
            case "number":
            case "text":
              return (
                <ConfigFieldInput
                  key={field.name} // Unique key for React rendering
                  field={field} // Field configuration
                  formData={formData} // Form data for managing values
                  type={field.type} // Input type (text or number)
                />
              );
            case "time":
              return (
                <ConfigFieldTime
                  key={field.name} // Unique key for React rendering
                  field={field} // Field configuration
                  formData={formData} // Form data for managing values
                />
              );
            default:
              // Return null for unsupported field types
              return null;
          }
        })}
      </Stack>
    </Box>
  );
};

ConfigFieldGroup.propTypes = {
  /**
   * The configuration object for the field group.
   * Contains the title, description, and an array of fields.
   */
  config: PropTypes.shape({
    title: PropTypes.string.isRequired, // The title of the field group
    description: PropTypes.string, // The description of the field group
    fields: PropTypes.arrayOf(
      PropTypes.shape({
        name: PropTypes.string.isRequired, // The unique name of the field
        label: PropTypes.string.isRequired, // The label for the field
        type: PropTypes.oneOf(["text", "number", "select", "time"]).isRequired, // The type of the field
        defaultValue: PropTypes.any, // The default value for the field
        required: PropTypes.bool, // Whether the field is required
        options: PropTypes.arrayOf(
          PropTypes.shape({
            value: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
              .isRequired, // The value of the option (for select fields)
            label: PropTypes.string.isRequired, // The label of the option (for select fields)
          }),
        ), // Options for select fields
        validate: PropTypes.func, // Validation function for the field
        minTime: PropTypes.object, // Minimum time (for time fields)
        maxTime: PropTypes.object, // Maximum time (for time fields)
        format: PropTypes.string, // Format for time fields
      }),
    ).isRequired, // Array of field configurations
  }).isRequired,

  /**
   * The form data object for managing field values.
   * Contains the current values of the fields in the group.
   */
  formData: PropTypes.object,
};

export default ConfigFieldGroup;
