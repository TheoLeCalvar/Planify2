// React imports
import React, { useContext, useEffect, useState } from "react";

// Material-UI imports
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  FormControlLabel,
  Checkbox,
} from "@mui/material";

// Axios instance for API requests
import axiosInstance from "@/config/axiosConfig";

// Context for authentication
import { AuthContext } from "@/hooks/AuthContext";

// Notifications
import { toast } from "react-toastify";

// React Router hook for navigation
import { useNavigate } from "react-router-dom";

/**
 * LoginPage component.
 * This component renders a login form for users to authenticate.
 * It handles user input, form submission, and error handling during login.
 *
 * @returns {JSX.Element} - The rendered login page.
 */
const LoginPage = () => {
  // State variables for form inputs
  const [email, setEmail] = useState(""); // Email input state
  const [password, setPassword] = useState(""); // Password input state
  const [rememberMe, setRememberMe] = useState(true); // Remember me checkbox state

  const navigate = useNavigate(); // Hook for navigation
  const { isLogin, login } = useContext(AuthContext); // Access authentication context

  /**
   * Redirects the user to the home page if already logged in.
   */
  useEffect(() => {
    if (isLogin()) {
      console.log(
        "Utilisateur déjà connecté, redirection vers la page d'accueil",
      );
      navigate("/"); // Redirect to the home page
    }
  }, [navigate]);

  /**
   * Handles changes in the email input field.
   *
   * @param {Object} event - The event object from the input field.
   */
  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };

  /**
   * Handles changes in the password input field.
   *
   * @param {Object} event - The event object from the input field.
   */
  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

  /**
   * Toggles the "Remember Me" checkbox state.
   */
  const handleCheckboxChange = () => {
    setRememberMe(!rememberMe);
  };

  /**
   * Handles form submission for login.
   * Sends a POST request to the login endpoint and processes the response.
   *
   * @param {Object} event - The form submission event.
   */
  const handleSubmit = (event) => {
    event.preventDefault(); // Prevent default form submission behavior
    axiosInstance
      .post("/auth/login", { mail: email, password }) // Send login request
      .then((response) => {
        // Handle successful login
        login(response.data.userInfo, response.data.token, rememberMe); // Save user info and token
        navigate("/"); // Redirect to the home page
        toast.success("Connexion réussie !"); // Show success notification
      })
      .catch((error) => {
        // Handle login errors
        if (error.response?.status === 401) {
          toast.error("Mot de passe incorrect !"); // Incorrect password
        } else if (error.response?.status === 404) {
          toast.error("Utilisateur non trouvé !"); // User not found
        } else {
          toast.error("Erreur lors de la connexion !"); // General error
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
          Se connecter
        </Typography>

        {/* Link to registration page */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2">
            Pas encore de compte ?{" "}
            <Button
              variant="text"
              color="primary"
              onClick={() => navigate("/register")}
            >
              Inscrivez-vous
            </Button>
          </Typography>
        </Box>

        {/* Login form */}
        <form onSubmit={handleSubmit} style={{ width: "100%" }}>
          {/* Email Input */}
          <TextField
            label="Adresse email"
            variant="outlined"
            margin="normal"
            required
            fullWidth
            autoComplete="email"
            autoFocus
            value={email}
            onChange={handleEmailChange}
          />

          {/* Password Input */}
          <TextField
            label="Mot de passe"
            variant="outlined"
            margin="normal"
            required
            fullWidth
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={handlePasswordChange}
          />

          {/* Remember Me Checkbox */}
          <FormControlLabel
            control={
              <Checkbox
                checked={rememberMe}
                onChange={handleCheckboxChange}
                color="primary"
              />
            }
            label="Rester connecter"
          />

          {/* Submit Button */}
          <Button type="submit" fullWidth variant="contained" color="primary">
            Login
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default LoginPage;
