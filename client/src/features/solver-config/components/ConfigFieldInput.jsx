// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Typography } from "@mui/material";

// Local imports
import ValidatedInput from "@/components/ValidatedInput";

const ConfigFieldInput = ({ field, formData, type }) => {
  const commonProps = {
    name: field.name,
    defaultValue: formData[field.name] || field.defaultValue,
    required: field.required,
    label: field.label,
  };

  return (
    <>
      <Typography variant="body1">{field.label} :</Typography>
      <ValidatedInput {...commonProps} type={type || field.type} />
    </>
  );
};

ConfigFieldInput.propTypes = {
  field: PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    defaultValue: PropTypes.any,
    required: PropTypes.bool,
    type: PropTypes.oneOf(["text", "number"]).isRequired,
  }).isRequired,
  formData: PropTypes.object,
  type: PropTypes.oneOf(["text", "number"]),
};

export default ConfigFieldInput;
