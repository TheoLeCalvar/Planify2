import { useState } from "react";
import { Form, useSubmit } from "react-router-dom";
import { Button, Stack, Typography } from "@mui/material";
import LoadingButton from "@mui/lab/LoadingButton";
import SaveIcon from "@mui/icons-material/Save";
import { FormContext } from "../context/FormContext";

const ValidatedForm = ({ validateField, onSubmit, children }) => {
    const [loading, setLoading] = useState(false);
    const [isFormValid, setIsFormValid] = useState(true);
    const submit = useSubmit();

    const handleSubmit = (e) => {
        e.preventDefault();
        const form = e.target;
        const hasErrors = Array.from(form.elements).some((element) => {
            if (element.tagName === "INPUT" || element.tagName === "TEXTAREA") {
                const error = validateField(element.name, element.value);
                return error !== "";
            }
            return false;
        });

        if (!hasErrors) {
            setLoading(true);
            setIsFormValid(true);
            submit(form);
            onSubmit && onSubmit(form);
        } else {
            setIsFormValid(false);
        }
    };

    return (
        <Form method="post" onSubmit={handleSubmit}>
            <FormContext.Provider value={validateField}>
                {children}
            </FormContext.Provider>
            {!isFormValid && (
                <Typography variant="body2" color="error" mt={2}>
                    Veuillez corriger les erreurs dans le formulaire.
                </Typography>
            )}
            <Stack spacing={2} direction={"row"} justifyContent={"flex-end"}>
                <Button
                    variant="outlined"
                    color="secondary"
                    onClick={() => navigate("..")}
                >
                    Annuler
                </Button>
                <LoadingButton
                    variant="contained"
                    color="primary"
                    type="submit"
                    loading={loading}
                    loadingPosition="start"
                    startIcon={<SaveIcon />}
                >
                    Sauvegarder
                </LoadingButton>
            </Stack>
        </Form>
    );
};

export default ValidatedForm;
