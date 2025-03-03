// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Typography } from "@mui/material";
import { LocalizationProvider, TimeField } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

// Local imports
import ValidatedInput from "@/components/ValidatedInput";
import parseTime from "../utils/parseTime";
import styles from "./ConfigField.styles";

const ConfigFieldTime = ({ field, formData }) => {
  // Convert a string default value to a Dayjs instance if needed.
  const defaultTimeValue =
    typeof field.defaultValue === "string"
      ? parseTime(field.defaultValue, field.format || "HH:mm")
      : field.defaultValue;

  const commonProps = {
    name: field.name,
    required: field.required,
    defaultValue: formData[field.name] || defaultTimeValue,
  };

  return (
    <>
      <Typography variant="body1" sx={styles.fieldLabel}>
        {field.label} :
      </Typography>
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="fr">
        <ValidatedInput {...commonProps}>
          <TimeField
            format={field.format || "HH:mm"}
            minTime={field.minTime}
            maxTime={field.maxTime}
          />
        </ValidatedInput>
      </LocalizationProvider>
    </>
  );
};

ConfigFieldTime.propTypes = {
  field: PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    defaultValue: PropTypes.any,
    required: PropTypes.bool,
    type: PropTypes.oneOf(["time"]).isRequired,
    format: PropTypes.string,
    minTime: PropTypes.object,
    maxTime: PropTypes.object,
  }).isRequired,
  formData: PropTypes.object,
};

export default ConfigFieldTime;
