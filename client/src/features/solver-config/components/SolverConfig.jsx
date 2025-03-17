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
import ValidatedForm from "@/components/ValidatedForm"; // Component for rendering a validated form
import DeleteConfigButton from "./DeleteConfigButton"; // Component for deleting a configuration
import SolverConfigList from "./SolverConfigList"; // Component for rendering a list of configuration sections
import { globalConfigSections, ueConfigSections } from "../utils/solverConfig"; // Configuration sections for global and UE-specific constraints

/**
 * Utility function to find a validator for a specific field.
 * Searches through the provided sections to locate a field with a matching name and a validation function.
 *
 * @param {string} fieldName - The name of the field to validate.
 * @param {Array} sections - The list of configuration sections to search.
 * @returns {Function|null} - The validation function for the field, or null if not found.
 */
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

/**
 * SolverConfigComponent
 * This component renders a form for creating or editing solver configurations.
 * It supports both global configurations (TAF-level) and UE-specific configurations.
 *
 * @returns {JSX.Element} - The rendered SolverConfigComponent.
 */
const SolverConfigComponent = () => {
  const data = useLoaderData(); // Load the initial form data
  const { idConfig } = useParams(); // Get the configuration ID from the route parameters
  const navigate = useNavigate(); // Hook for navigating between routes
  const { ue } = useOutletContext(); // Get the UE context from the parent route
  const isEditing = Boolean(idConfig); // Determine if the form is in editing mode

  /**
   * Handles the cancel action by navigating back to the parent route.
   */
  const onCancel = () => navigate("..");

  // Determine the sections and header based on the context (UE-specific or global).
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

  /**
   * Validates a specific field in the form.
   * Uses the `findValidator` utility to locate the appropriate validation function.
   *
   * @param {string} name - The name of the field to validate.
   * @param {any} value - The value of the field to validate.
   * @param {Object} otherValues - The other field values in the form.
   * @returns {string} - The validation error message, or an empty string if valid.
   */
  const validateField = (name, value, otherValues) => {
    const validator = findValidator(name, sections);
    return validator ? validator(value, otherValues) : "";
  };

  return (
    <>
      {/* Page title */}
      <Typography variant="h5" gutterBottom mt={2}>
        {isEditing ? "Editer la configuration" : "Nouvelle configuration"}
      </Typography>

      {/* Validated form */}
      <ValidatedForm
        validateField={validateField} // Field validation function
        onCancel={onCancel} // Cancel action handler
        actionButtons={
          isEditing ? <DeleteConfigButton idConfig={idConfig} /> : null // Show delete button if editing
        }
      >
        <Stack spacing={3} direction="column">
          {/* Render the configuration sections */}
          <SolverConfigList
            title={header.title} // Header title
            tooltip={header.tooltip} // Header tooltip
            sections={sections} // Configuration sections
            formData={data} // Initial form data
          />
        </Stack>
      </ValidatedForm>
    </>
  );
};

export default SolverConfigComponent;
