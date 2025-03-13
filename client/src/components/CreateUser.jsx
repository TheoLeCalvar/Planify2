// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Box, Stack } from "@mui/material";
import { Dialog, DialogContent, DialogTitle } from "@mui/material";

// Local imports
import ValidatedInput from "./ValidatedInput";
import ValidatedForm from "./ValidatedForm";
import axiosInstance from "@/config/axiosConfig";
import { toast } from "react-toastify";

export async function action({ request }) {
  const data = await request.json();

  await axiosInstance
    .post(`/users`, data)
    .then(() => {
      toast.success("Utilisateur créé");
    })
    .catch((response) => {
      if (response.status === 409) {
        toast.error("Cet utilisateur existe déjà");
      } else {
        toast.error("Erreur lors de la création de l'utilisateur");
      }
    });
  return null;
}

// Subcomponent for the CreateUser dialog
const CreateUserDialog = ({ open, onClose }) => (
  <Dialog open={open} onClose={onClose}>
    <DialogTitle>Créer un nouvel utilisateur</DialogTitle>
    <DialogContent>
      <CreateUser onCancel={onClose} />
    </DialogContent>
  </Dialog>
);

CreateUserDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export function CreateUser({ onCancel }) {
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
      case "password":
        if (value.length < 6) {
          return "Le mot de passe doit contenir au moins 6 caractères.";
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
        action="/createUser"
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
        <ValidatedInput
          name="password"
          label="Mot de passe"
          type="password"
          margin="normal"
          fullWidth
          required
        />
      </ValidatedForm>
    </Box>
  );
}

CreateUser.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default CreateUserDialog;
