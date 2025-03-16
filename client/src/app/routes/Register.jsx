// React imports
import React, { useState } from "react";

// Material-UI imports
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Grid2 as Grid,
} from "@mui/material";

// Axios instance for API requests
import axiosInstance from "@/config/axiosConfig";

// Notifications
import { toast } from "react-toastify";

// React Router hook for navigation
import { useNavigate } from "react-router-dom";

/**
 * RegisterPage component.
 * This component renders a registration form for users to create an account.
 * It handles user input, form validation, and submission to the server.
 *
 * @returns {JSX.Element} - The rendered registration page.
 */
const RegisterPage = () => {
  // State variables for form inputs
  const [firstName, setFirstName] = useState(""); // First name input state
  const [lastName, setLastName] = useState(""); // Last name input state
  const [email, setEmail] = useState("@imt-atlantique.fr"); // Email input state
  const [password, setPassword] = useState(""); // Password input state
  const [errors, setErrors] = useState({}); // State for form validation errors

  const navigate = useNavigate(); // Hook for navigation

  /**
   * Validates the email format.
   *
   * @param {string} email - The email to validate.
   * @returns {boolean} - True if the email is valid, false otherwise.
   */
  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Regular expression for email validation
    return re.test(String(email).toLowerCase());
  };

  /**
   * Validates the password format.
   * The password must be at least 8 characters long and contain at least one uppercase letter,
   * one lowercase letter, one number, and one special character.
   *
   * @param {string} password - The password to validate.
   * @returns {boolean} - True if the password is valid, false otherwise.
   */
  const validatePassword = (password) => {
    const re =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return re.test(password);
  };

  /**
   * Handles form submission for registration.
   * Validates the form inputs and sends a POST request to the server.
   *
   * @param {Object} event - The form submission event.
   */
  const handleSubmit = (event) => {
    event.preventDefault(); // Prevent default form submission behavior

    let validationErrors = {};

    // Validate email
    if (!validateEmail(email)) {
      validationErrors.email = "Adresse email invalide";
    }

    // Validate password
    if (!validatePassword(password)) {
      validationErrors.password =
        "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial";
    }

    // If there are validation errors, update the state and stop submission
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    // Send registration request to the server
    axiosInstance
      .post("/auth/register", {
        name: firstName,
        lastName,
        mail: email,
        password,
      })
      .then(() => {
        toast.success("Compte créé avec succès !"); // Show success notification
        navigate("/login"); // Redirect to the login page
      })
      .catch((error) => {
        // Handle server errors
        if (error.response?.status === 409) {
          toast.error("Utilisateur déjà existant !"); // User already exists
        } else {
          toast.error("Erreur inconnue lors de la création du compte !"); // General error
        }
      });
  };

  return (
    <Container maxWidth="xs">
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          mt: 8,
          p: 3,
          boxShadow: 3,
          borderRadius: 2,
          bgcolor: "background.paper",
        }}
      >
        {/* Page title */}
        <Typography component="h1" variant="h5" sx={{ mb: 2 }}>
          Créer un compte
        </Typography>

        {/* Link to login page */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2">
            Déjà inscrit ?{" "}
            <Button
              variant="text"
              color="primary"
              onClick={() => navigate("/login")}
            >
              Connectez-vous
            </Button>
          </Typography>
        </Box>

        {/* Registration form */}
        <form onSubmit={handleSubmit} style={{ width: "100%" }}>
          {/* Grid for first name and last name inputs */}
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                label="Prénom"
                variant="outlined"
                required
                fullWidth
                autoComplete="given-name"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Nom"
                variant="outlined"
                required
                fullWidth
                autoComplete="family-name"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
              />
            </Grid>
          </Grid>

          {/* Email input */}
          <TextField
            label="Adresse email"
            variant="outlined"
            margin="normal"
            required
            fullWidth
            autoComplete="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={!!errors.email}
            helperText={errors.email}
          />

          {/* Password input */}
          <TextField
            label="Mot de passe"
            variant="outlined"
            margin="normal"
            required
            fullWidth
            type="password"
            autoComplete="new-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={!!errors.password}
            helperText={errors.password}
          />

          {/* Submit button */}
          <Button type="submit" fullWidth variant="contained" color="primary">
            Valider
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default RegisterPage;
