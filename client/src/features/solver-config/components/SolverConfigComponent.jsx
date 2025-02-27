// React imports
import React from "react";
import {
  useLoaderData,
  useNavigate,
  useOutletContext,
  useParams,
} from "react-router-dom";

// Material-UI imports
import { Stack, Typography } from "@mui/material";

// Local imports
import ValidatedForm from "@/components/ValidatedForm";
import DeleteConfigButton from "./DeleteConfigButton";
import SolverConfigList from "./SolverConfigList";
import { globalConfigSections, ueConfigSections } from "../utils/solverConfig";

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

const SolverConfigComponent = () => {
  const data = useLoaderData();
  const { idConfig } = useParams();
  const navigate = useNavigate();
  const { ue } = useOutletContext();
  const isEditing = Boolean(idConfig);

  const onCancel = () => navigate("..");

  // Determine the sections and header based on the outlet context.
  const sections = ue ? ueConfigSections : globalConfigSections;
  const header = ue
    ? {
        title: "Contraintes relatives à l’UE",
        tooltip:
          "Seules les contraintes spécifiques à cette UE sont affichées. Les contraintes globales à la TAF sont configurées dans les paramètres de la TAF.",
      }
    : {
        title: "Contraintes relatives à la TAF",
        tooltip:
          "Seules les contraintes globales à la TAF sont affichées. Les contraintes relatives aux UE sont configurées dans les paramètres de chaque UE.",
      };

  const validateField = (name, value, otherValues) => {
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
          isEditing ? <DeleteConfigButton idConfig={idConfig} /> : null
        }
      >
        <Stack spacing={3} direction="column">
          <SolverConfigList
            title={header.title}
            tooltip={header.tooltip}
            sections={sections}
            formData={data}
          />
        </Stack>
      </ValidatedForm>
    </>
  );
};

export default SolverConfigComponent;
