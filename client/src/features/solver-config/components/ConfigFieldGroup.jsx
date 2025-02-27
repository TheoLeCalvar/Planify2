// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Stack } from "@mui/material";

// Local imports
import ConfigFieldGroupHeader from "./ConfigFieldGroupHeader";
import ConfigFieldInput from "./ConfigFieldInput";
import ConfigFieldSelect from "./ConfigFieldSelect";
import ConfigFieldTime from "./ConfigFieldTime";
import styles from "./ConfigField.styles";

const ConfigFieldGroup = ({ config, formData }) => {
  return (
    <>
      <ConfigFieldGroupHeader
        title={config.title}
        description={config.description}
      />
      <Stack direction="row" spacing={3} alignItems="center">
        {config.fields.map((field) => {
          switch (field.type) {
            case "select":
              return (
                <ConfigFieldSelect
                  key={field.name}
                  field={field}
                  formData={formData}
                />
              );
            case "number":
            case "text":
              return (
                <ConfigFieldInput
                  key={field.name}
                  field={field}
                  formData={formData}
                  type={field.type}
                />
              );
            case "time":
              return (
                <ConfigFieldTime
                  key={field.name}
                  field={field}
                  formData={formData}
                />
              );
            default:
              return null;
          }
        })}
      </Stack>
    </>
  );
};

ConfigFieldGroup.propTypes = {
  config: PropTypes.shape({
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    fields: PropTypes.arrayOf(
      PropTypes.shape({
        name: PropTypes.string.isRequired,
        label: PropTypes.string.isRequired,
        type: PropTypes.oneOf(["text", "number", "select", "time"]).isRequired,
        defaultValue: PropTypes.any,
        required: PropTypes.bool,
        options: PropTypes.arrayOf(
          PropTypes.shape({
            value: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
              .isRequired,
            label: PropTypes.string.isRequired,
          }),
        ),
        validate: PropTypes.func,
        minTime: PropTypes.object,
        maxTime: PropTypes.object,
        format: PropTypes.string,
      }),
    ).isRequired,
  }).isRequired,
  formData: PropTypes.object,
};

export default ConfigFieldGroup;
