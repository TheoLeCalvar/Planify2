// React imports
import React, { useRef, useState } from "react";
import { Form, useSubmit } from "react-router-dom";
import PropTypes from "prop-types";

// Material-UI imports
import { Button, Stack, Typography } from "@mui/material";
import LoadingButton from "@mui/lab/LoadingButton";
import SaveIcon from "@mui/icons-material/Save";

// Local imports
import { FormContext } from "@/hooks/FormContext";

const ValidatedForm = ({
  validateField,
  onSubmit,
  onCancel,
  children,
  action,
  actionButtons,
}) => {
  const [loading, setLoading] = useState(false);
  const [formValue, setValue] = useState({});
  const [isFormValid, setIsFormValid] = useState(true);
  const [formError, setFormError] = useState("");
  const form = useRef(null);
  const submit = useSubmit();

  const handleSubmit = (e) => {
    e?.preventDefault();

    const hasErrors = Object.entries(formValue).some(([name, value]) => {
      const error = validateField(name, value, formValue);
      error && setFormError(error);
      return error !== "";
    });

    if (!hasErrors) {
      setLoading(true);
      setIsFormValid(true);
      submit(formValue, {
        method: "post",
        action,
        navigate: false,
        encType: "application/json",
      });
      onSubmit && onSubmit(formValue);
    } else {
      setIsFormValid(false);
    }
  };

  return (
    <Form method="post" onSubmit={handleSubmit} ref={form} action={action}>
      <FormContext.Provider
        value={{ validate: validateField, value: formValue, setValue }}
      >
        {children}
      </FormContext.Provider>
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
          {onCancel && (
            <Button variant="outlined" color="secondary" onClick={onCancel}>
              Annuler
            </Button>
          )}
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

ValidatedForm.propTypes = {
  validateField: PropTypes.func.isRequired,
  onSubmit: PropTypes.func,
  onCancel: PropTypes.func,
  children: PropTypes.node.isRequired,
  action: PropTypes.string,
  actionButtons: PropTypes.node,
};

export default ValidatedForm;
