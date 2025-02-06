import React from "react";
import ValidatedInput from "./utils/ValidatedInput";
import ValidatedForm from "./utils/ValidatedForm";
import { Box, Stack } from "@mui/material";
import axiosInstance from "../services/axiosConfig";
import { USE_MOCK_DATA } from "../contants";

export async function action({ request, params }) {
  const formData = await request.formData();
  const data = Object.fromEntries(formData);

  if (USE_MOCK_DATA) {
    await new Promise((res) => setTimeout(res, 1000));
    return { ok: true };
  }

  const result = await axiosInstance.post(`/users`, data);
  return { ok: result.status === 200 };
}

export default function CreateUser({ onCancel }) {
  const validateField = (name, value) => {
    switch (name) {
      case "email":
        if (
          !value.match(
            /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/gi,
          )
        ) {
          return "L'email n'est pas valide.";
        }
        break;
      default:
        return "";
    }
    return ""; // Pas d'erreur
  };

  return (
    <Box sx={{ pt: 1 }}>
      <ValidatedForm
        validateField={validateField}
        onSubmit={onCancel}
        onCancel={onCancel}
        action={"./createUser"}
      >
        <Stack direction="row" spacing={2}>
          <ValidatedInput
            name="firstname"
            label="Prénom"
            defaultValue=""
            margin="normal"
            fullWidth
            required
          />
          <ValidatedInput
            name="lastname"
            label="Nom"
            defaultValue=""
            margin="normal"
            fullWidth
            required
          />
        </Stack>
        <ValidatedInput
          name="email"
          label="Email"
          defaultValue="@imt-atlantique.fr"
          margin="normal"
          fullWidth
          required
        />
      </ValidatedForm>
    </Box>
  );
}
