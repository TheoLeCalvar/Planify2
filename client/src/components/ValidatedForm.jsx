// React imports
import React, { useRef, useState } from "react";
import { Form, useSubmit } from "react-router-dom"; // React Router utilities for form handling
import PropTypes from "prop-types"; // PropTypes for type checking

// Material-UI imports
import { Button, Stack, Typography } from "@mui/material"; // UI components for layout and styling
import LoadingButton from "@mui/lab/LoadingButton"; // Button with loading state
import SaveIcon from "@mui/icons-material/Save"; // Save icon for the button

// Local imports
import { FormContext } from "@/hooks/FormContext"; // Context for managing form state and validation

/**
 * ValidatedForm component.
 * This component renders a form with validation, error handling, and customizable action buttons.
 * It uses React Router's form submission utilities and provides a context for managing form state.
 *
 * @param {Object} props - The component props.
 * @param {Function} props.validateField - Function to validate individual form fields.
 * @param {Function} [props.onSubmit] - Callback function executed when the form is successfully submitted.
 * @param {Function} [props.onCancel] - Callback function executed when the cancel button is clicked.
 * @param {React.ReactNode} props.children - The form fields to render inside the form.
 * @param {string} [props.action] - The action URL for the form submission.
 * @param {React.ReactNode} [props.actionButtons] - Additional action buttons to render in the form.
 *
 * @returns {JSX.Element} - The rendered ValidatedForm component.
 */
const ValidatedForm = ({
  validateField,
  onSubmit,
  onCancel,
  children,
  action,
  actionButtons,
}) => {
  // State variables
  const [loading, setLoading] = useState(false); // Loading state for the submit button
  const [formValue, setValue] = useState({}); // Current form values
  const [isFormValid, setIsFormValid] = useState(true); // Form validity state
  const [formError, setFormError] = useState(""); // Error message for the form
  const form = useRef(null); // Reference to the form element
  const submit = useSubmit(); // React Router's submit function for form handling

  /**
   * Handles form submission.
   * Validates the form fields and submits the form if valid.
   *
   * @param {Object} e - The form submission event.
   */
  const handleSubmit = (e) => {
    e?.preventDefault(); // Prevent default form submission behavior

    // Validate all form fields
    const hasErrors = Object.entries(formValue).some(([name, value]) => {
      const error = validateField(name, value, formValue);
      error && setFormError(error); // Set the error message if validation fails
      return error !== "";
    });

    if (!hasErrors) {
      // If no errors, submit the form
      setLoading(true);
      setIsFormValid(true);
      submit(formValue, {
        method: "post",
        action,
        navigate: false,
        encType: "application/json",
      });
      onSubmit && onSubmit(formValue); // Execute the onSubmit callback if provided
    } else {
      setIsFormValid(false); // Mark the form as invalid
    }
  };

  return (
    <Form method="post" onSubmit={handleSubmit} ref={form} action={action}>
      {/* Provide form context for child components */}
      <FormContext.Provider
        value={{ validate: validateField, value: formValue, setValue }}
      >
        {children}
      </FormContext.Provider>

      {/* Display error messages if the form is invalid */}
      {!isFormValid && (
        <>
          <Typography variant="body1" color="error" mt={2}>
            Veuillez corriger les erreurs dans le formulaire.
          </Typography>
          <Typography variant="body2" color="error">
            {formError}
          </Typography>
        </>
      )}

      {/* Action buttons */}
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Stack
          spacing={2}
          direction={"row"}
          justifyContent={"flex-start"}
          my={3}
        >
          {actionButtons}
        </Stack>
        <Stack spacing={2} direction={"row"} justifyContent={"flex-end"} my={3}>
          {/* Cancel button */}
          {onCancel && (
            <Button variant="outlined" color="secondary" onClick={onCancel}>
              Annuler
            </Button>
          )}
          {/* Submit button with loading state */}
          <LoadingButton
            variant="contained"
            color="primary"
            onClick={handleSubmit}
            loading={loading}
            loadingPosition="start"
            startIcon={<SaveIcon />}
          >
            Sauvegarder
          </LoadingButton>
        </Stack>
      </div>
    </Form>
  );
};

// Define the prop types for the component
ValidatedForm.propTypes = {
  validateField: PropTypes.func.isRequired, // Function to validate individual form fields
  onSubmit: PropTypes.func, // Callback function for form submission
  onCancel: PropTypes.func, // Callback function for form cancellation
  children: PropTypes.node.isRequired, // Form fields to render inside the form
  action: PropTypes.string, // Action URL for form submission
  actionButtons: PropTypes.node, // Additional action buttons to render
};

// Export the component as the default export
export default ValidatedForm;
