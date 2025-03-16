// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { MenuItem, Select, Typography } from "@mui/material";

// Local imports
import ValidatedInput from "@/components/ValidatedInput"; // Component for rendering validated input fields
import styles from "./ConfigField.styles"; // Styles for the configuration fields

/**
 * ConfigFieldSelect Component
 * This component renders a labeled select dropdown field for a configuration form.
 * It uses the `ValidatedInput` component to handle validation and input rendering.
 *
 * @param {Object} props - The component props.
 * @param {Object} props.field - The configuration object for the select field.
 * @param {Object} props.formData - The form data object for managing field values.
 * @returns {JSX.Element} - The rendered ConfigFieldSelect component.
 */
const ConfigFieldSelect = ({ field, formData }) => {
  // Common properties for the select field
  const commonProps = {
    name: field.name, // The name of the select field
    defaultValue: formData[field.name] || field.defaultValue, // Default value from formData or field configuration
    required: field.required, // Whether the field is required
  };

  return (
    <>
      {/* Render the field label */}
      <Typography variant="body1" sx={styles.fieldLabel}>
        {field.label} :
      </Typography>

      {/* Render the validated input field with a select dropdown */}
      <ValidatedInput {...commonProps}>
        <Select>
          {/* Render each option in the dropdown */}
          {field.options.map((opt) => (
            <MenuItem key={opt.value} value={opt.value}>
              {opt.label}
            </MenuItem>
          ))}
        </Select>
      </ValidatedInput>
    </>
  );
};

// Define the expected prop types for the component
ConfigFieldSelect.propTypes = {
  /**
   * The configuration object for the select field.
   * Contains the name, label, default value, required status, type, and options for the field.
   */
  field: PropTypes.shape({
    name: PropTypes.string.isRequired, // The unique name of the field
    label: PropTypes.string.isRequired, // The label for the field
    defaultValue: PropTypes.any, // The default value for the field
    required: PropTypes.bool, // Whether the field is required
    type: PropTypes.oneOf(["select"]).isRequired, // The type of the field (must be "select")
    options: PropTypes.arrayOf(
      PropTypes.shape({
        value: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
          .isRequired, // The value of the option
        label: PropTypes.string.isRequired, // The label of the option
      }),
    ).isRequired, // Array of options for the select field
  }).isRequired,

  /**
   * The form data object for managing field values.
   * Contains the current values of the fields in the form.
   */
  formData: PropTypes.object,
};

export default ConfigFieldSelect;
