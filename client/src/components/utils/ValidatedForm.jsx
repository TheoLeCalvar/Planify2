import { useRef, useState } from "react";
import { Form, useSubmit } from "react-router-dom";
import { Button, Stack, Typography } from "@mui/material";
import LoadingButton from "@mui/lab/LoadingButton";
import SaveIcon from "@mui/icons-material/Save";
import { FormContext } from "../../context/FormContext";
import dayjs from "dayjs";

const ValidatedForm = ({ validateField, onSubmit, onCancel, children, action }) => {
    const [loading, setLoading] = useState(false);
    const [isFormValid, setIsFormValid] = useState(true);
    const [formError, setFormError] = useState("");
    const form = useRef(null);
    const submit = useSubmit();

    const handleSubmit = (e) => {
        e.preventDefault();
        const form = e.target;
        const formValues = Object.fromEntries(new FormData(form));

        const hasErrors = Array.from(form.elements).some((element) => {
            if (element.tagName === "INPUT" || element.tagName === "TEXTAREA") {
                const error = validateField(element.name, element.value, formValues);
                if (dayjs(element.value, "DD/MM/YYYY").isValid()) {
                    element.value = dayjs(element.value, "DD/MM/YYYY").format("YYYY-MM-DD");
                }
                error && setFormError(error);
                return error !== "";
            }
            return false;
        });

        if (!hasErrors) {
            setLoading(true);
            setIsFormValid(true);
            submit(form, {
                method: "post",
                action,
                navigate: !action,
            });
            onSubmit && onSubmit(form);
        } else {
            setIsFormValid(false);
        }
    };

    return (
        <Form method="post" onSubmit={handleSubmit} ref={form}>
            <FormContext.Provider value={{validate : validateField, form}}>
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
            <Stack spacing={2} direction={"row"} justifyContent={"flex-end"}>
                {onCancel && (
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={onCancel}
                    >
                        Annuler
                    </Button>
                )}
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
