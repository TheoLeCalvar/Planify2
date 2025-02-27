// React imports
import React from "react";
import {
  useLoaderData,
  useNavigate,
  useOutletContext,
  useParams,
} from "react-router-dom";

// Material-UI imports
import { IconButton, Stack, Tooltip, Typography } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

// Local imports
import ValidatedForm from "@/components/ValidatedForm";
import ConfigFieldGroup from "./ConfigFieldGroup";
import DeleteConfigButton from "./DeleteConfigButton";
import { globalConfigSections, ueConfigSections } from "../utils/solverConfig";

const SolverConfigComponent = () => {
  const data = useLoaderData();
  const params = useParams();
  const navigate = useNavigate();
  const context = useOutletContext();

  const isEditing = !!params.idConfig;

  const onCancel = () => {
    navigate("..");
  };

  // Helper to retrieve the validation function for a given field from the mapping.
  const findValidator = (fieldName, sections) => {
    for (const section of sections) {
      for (const field of section.fields) {
        if (field.name === fieldName && typeof field.validate === "function") {
          return field.validate;
        }
      }
    }
    return null;
  };

  const validateField = (name, value, otherValues) => {
    const sections = context?.ue ? ueConfigSections : globalConfigSections;
    const validator = findValidator(name, sections);
    return validator ? validator(value, otherValues) : "";
  };

  return (
    <>
      <Typography variant="h5" gutterBottom mt={2}>
        {isEditing ? "Editer la configuration" : "Nouvelle configuration"}
      </Typography>
      <ValidatedForm
        validateField={validateField}
        onCancel={onCancel}
        actionButtons={
          isEditing ? <DeleteConfigButton idConfig={params.idConfig} /> : null
        }
      >
        <Stack spacing={3} direction="column">
          {!context?.ue ? (
            <>
              <Stack direction="row" alignItems="center" spacing={1}>
                <Tooltip title="Seules les contraintes globales à la TAF sont affichées. Les contraintes relatives aux UE sont configurées dans les paramètres de chaque UE.">
                  <IconButton size="small">
                    <InfoIcon fontSize="inherit" />
                  </IconButton>
                </Tooltip>
                <Typography variant="h6">
                  Contraintes relatives à la TAF
                </Typography>
              </Stack>
              {globalConfigSections.map((section) => (
                <ConfigFieldGroup
                  key={section.title}
                  config={section}
                  formData={data}
                />
              ))}
            </>
          ) : (
            <>
              <Stack direction="row" alignItems="center" spacing={1}>
                <Tooltip title="Seules les contraintes spécifiques à cette UE sont affichées. Les contraintes globales à la TAF sont configurées dans les paramètres de la TAF.">
                  <IconButton size="small">
                    <InfoIcon fontSize="inherit" />
                  </IconButton>
                </Tooltip>
                <Typography variant="h6">
                  Contraintes relatives à l&apos;UE
                </Typography>
              </Stack>
              {ueConfigSections.map((section) => (
                <ConfigFieldGroup
                  key={section.title}
                  config={section}
                  formData={data}
                />
              ))}
            </>
          )}
        </Stack>
      </ValidatedForm>
    </>
  );
};

export default SolverConfigComponent;
