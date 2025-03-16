// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Typography } from "@mui/material";

// Local imports
import ValidatedInput from "@/components/ValidatedInput"; // Component for rendering validated input fields
import styles from "./ConfigField.styles"; // Styles for the configuration fields

/**
 * ConfigFieldInput Component
 * This component renders a labeled input field (text or number) for a configuration form.
 * It uses the `ValidatedInput` component to handle validation and input rendering.
 *
 * @param {Object} props - The component props.
 * @param {Object} props.field - The configuration object for the input field.
 * @param {Object} props.formData - The form data object for managing field values.
 * @param {string} [props.type] - The type of the input field (overrides the field's type if provided).
 * @returns {JSX.Element} - The rendered ConfigFieldInput component.
 */
const ConfigFieldInput = ({ field, formData, type }) => {
  // Common properties for the input field
  const commonProps = {
    name: field.name, // The name of the input field
    defaultValue: formData[field.name] || field.defaultValue, // Default value from formData or field configuration
    required: field.required, // Whether the field is required
  };

  return (
    <>
      {/* Render the field label */}
      <Typography variant="body1" sx={styles.fieldLabel}>
        {field.label} :
      </Typography>

      {/* Render the validated input field */}
      <ValidatedInput {...commonProps} type={type || field.type} />
    </>
  );
};

// Define the expected prop types for the component
ConfigFieldInput.propTypes = {
  /**
   * The configuration object for the input field.
   * Contains the name, label, default value, required status, and type of the field.
   */
  field: PropTypes.shape({
    name: PropTypes.string.isRequired, // The unique name of the field
    label: PropTypes.string.isRequired, // The label for the field
    defaultValue: PropTypes.any, // The default value for the field
    required: PropTypes.bool, // Whether the field is required
    type: PropTypes.oneOf(["text", "number"]).isRequired, // The type of the field (text or number)
  }).isRequired,

  /**
   * The form data object for managing field values.
   * Contains the current values of the fields in the form.
   */
  formData: PropTypes.object,

  /**
   * The type of the input field (optional).
   * Overrides the field's type if provided.
   */
  type: PropTypes.oneOf(["text", "number"]),
};

export default ConfigFieldInput;
