// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Typography } from "@mui/material";
import { LocalizationProvider, TimeField } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

// Local imports
import ValidatedInput from "@/components/ValidatedInput"; // Component for rendering validated input fields
import parseTime from "../utils/parseTime"; // Utility function for parsing time strings
import styles from "./ConfigField.styles"; // Styles for the configuration fields

/**
 * ConfigFieldTime Component
 * This component renders a labeled time input field for a configuration form.
 * It uses the `ValidatedInput` component to handle validation and input rendering.
 * The `TimeField` component from Material-UI is used for time selection.
 *
 * @param {Object} props - The component props.
 * @param {Object} props.field - The configuration object for the time field.
 * @param {Object} props.formData - The form data object for managing field values.
 * @returns {JSX.Element} - The rendered ConfigFieldTime component.
 */
const ConfigFieldTime = ({ field, formData }) => {
  // Convert a string default value to a Dayjs instance if needed.
  const defaultTimeValue =
    typeof field.defaultValue === "string"
      ? parseTime(field.defaultValue, field.format || "HH:mm")
      : field.defaultValue;

  // Common properties for the time field
  const commonProps = {
    name: field.name, // The name of the time field
    required: field.required, // Whether the field is required
    defaultValue: formData[field.name]
      ? parseTime(formData[field.name]) // Parse the value from formData if available
      : defaultTimeValue, // Use the default value if no formData value is provided
  };

  return (
    <>
      {/* Render the field label */}
      <Typography variant="body1" sx={styles.fieldLabel}>
        {field.label} :
      </Typography>

      {/* Render the time input field with localization */}
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="fr">
        <ValidatedInput {...commonProps}>
          <TimeField
            format={field.format || "HH:mm"} // Time format (default is "HH:mm")
            minTime={field.minTime} // Minimum allowed time
            maxTime={field.maxTime} // Maximum allowed time
          />
        </ValidatedInput>
      </LocalizationProvider>
    </>
  );
};

// Define the expected prop types for the component
ConfigFieldTime.propTypes = {
  /**
   * The configuration object for the time field.
   * Contains the name, label, default value, required status, type, format, and time constraints.
   */
  field: PropTypes.shape({
    name: PropTypes.string.isRequired, // The unique name of the field
    label: PropTypes.string.isRequired, // The label for the field
    defaultValue: PropTypes.any, // The default value for the field
    required: PropTypes.bool, // Whether the field is required
    type: PropTypes.oneOf(["time"]).isRequired, // The type of the field (must be "time")
    format: PropTypes.string, // The time format (e.g., "HH:mm")
    minTime: PropTypes.object, // Minimum allowed time (Dayjs object)
    maxTime: PropTypes.object, // Maximum allowed time (Dayjs object)
  }).isRequired,

  /**
   * The form data object for managing field values.
   * Contains the current values of the fields in the form.
   */
  formData: PropTypes.object,
};

export default ConfigFieldTime;
