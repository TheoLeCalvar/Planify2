// React imports
import React from "react";
import PropTypes from "prop-types"; // PropTypes for type checking

// Material-UI imports
import { Box, Stack } from "@mui/material"; // Layout components
import { Dialog, DialogContent, DialogTitle } from "@mui/material"; // Dialog components

// Local imports
import ValidatedInput from "./ValidatedInput"; // Input component with validation
import ValidatedForm from "./ValidatedForm"; // Form component with validation
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests
import { toast } from "react-toastify"; // Notifications for success and error messages

/**
 * Action function for creating a new user.
 * Sends a POST request to the server with the user data.
 *
 * @param {Object} request - The HTTP request object.
 * @returns {null} - Returns null after the action is completed.
 */
export async function action({ request }) {
  const data = await request.json(); // Parse the request body as JSON

  await axiosInstance
    .post(`/users`, data) // Send a POST request to create a new user
    .then(() => {
      toast.success("Utilisateur créé"); // Show success notification
    })
    .catch((response) => {
      // Handle errors
      if (response.status === 409) {
        toast.error("Cet utilisateur existe déjà"); // User already exists
      } else {
        toast.error("Erreur lors de la création de l'utilisateur"); // General error
      }
    });
  return null;
}

/**
 * CreateUserDialog component.
 * This component renders a dialog for creating a new user.
 *
 * @param {Object} props - The component props.
 * @param {boolean} props.open - Whether the dialog is open or not.
 * @param {Function} props.onClose - Function to close the dialog.
 *
 * @returns {JSX.Element} - The rendered CreateUserDialog component.
 */
const CreateUserDialog = ({ open, onClose }) => (
  <Dialog open={open} onClose={onClose}>
    <DialogTitle>Créer un nouvel utilisateur</DialogTitle>
    <DialogContent>
      <CreateUser onCancel={onClose} />
    </DialogContent>
  </Dialog>
);

CreateUserDialog.propTypes = {
  open: PropTypes.bool.isRequired, // Whether the dialog is open
  onClose: PropTypes.func.isRequired, // Function to close the dialog
};

/**
 * CreateUser component.
 * This component renders a form for creating a new user.
 * It includes fields for first name, last name, email, and password.
 *
 * @param {Object} props - The component props.
 * @param {Function} props.onCancel - Function to handle form cancellation.
 *
 * @returns {JSX.Element} - The rendered CreateUser component.
 */
export function CreateUser({ onCancel }) {
  /**
   * Validates individual form fields.
   *
   * @param {string} name - The name of the field.
   * @param {any} value - The value of the field.
   * @returns {string} - An error message if validation fails, otherwise an empty string.
   */
  const validateField = (name, value) => {
    switch (name) {
      case "email":
        if (
          !value.match(
            /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/gi,
          )
        ) {
          return "L'email n'est pas valide."; // Invalid email format
        }
        break;
      case "password":
        if (value.length < 6) {
          return "Le mot de passe doit contenir au moins 6 caractères."; // Password too short
        }
        break;
      default:
        return "";
    }
    return ""; // No error
  };

  return (
    <Box sx={{ pt: 1 }}>
      {/* Form for creating a new user */}
      <ValidatedForm
        validateField={validateField} // Field validation function
        onSubmit={onCancel} // Handle form submission
        onCancel={onCancel} // Handle form cancellation
        action="/createUser" // Action endpoint for form submission
      >
        {/* Stack for first name and last name inputs */}
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
        {/* Email input */}
        <ValidatedInput
          name="email"
          label="Email"
          defaultValue="@imt-atlantique.fr"
          margin="normal"
          fullWidth
          required
        />
        {/* Password input */}
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
  onCancel: PropTypes.func.isRequired, // Function to handle form cancellation
};

export default CreateUserDialog;
