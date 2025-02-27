// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { MenuItem, Select, Typography } from "@mui/material";

// Local imports
import ValidatedInput from "@/components/ValidatedInput";

const ConfigFieldSelect = ({ field, formData }) => {
  const commonProps = {
    name: field.name,
    defaultValue: formData[field.name] || field.defaultValue,
    required: field.required,
    label: field.label,
  };

  return (
    <>
      <Typography variant="body1">{field.label} :</Typography>
      <ValidatedInput {...commonProps}>
        <Select>
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

ConfigFieldSelect.propTypes = {
  field: PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    defaultValue: PropTypes.any,
    required: PropTypes.bool,
    type: PropTypes.oneOf(["select"]).isRequired,
    options: PropTypes.arrayOf(
      PropTypes.shape({
        value: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
          .isRequired,
        label: PropTypes.string.isRequired,
      }),
    ).isRequired,
  }).isRequired,
  formData: PropTypes.object,
};

export default ConfigFieldSelect;
