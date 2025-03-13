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
import axiosInstance from "@/config/axiosConfig";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const RegisterPage = () => {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("@imt-atlantique.fr");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({});

  const navigate = useNavigate();

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const validatePassword = (password) => {
    // Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character
    const re =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return re.test(password);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    let validationErrors = {};

    if (!validateEmail(email)) {
      validationErrors.email = "Adresse email invalide";
    }

    if (!validatePassword(password)) {
      validationErrors.password =
        "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial";
    }

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    axiosInstance
      .post("/auth/register", {
        name: firstName,
        lastName,
        mail: email,
        password,
      })
      .then(() => {
        toast.success("Compte créé avec succès !");
        navigate("/login");
      })
      .catch((error) => {
        if (error.response?.status === 409) {
          toast.error("Utilisateur déjà existant !");
        } else {
          toast.error("Erreur inconnue lors de la création du compte !");
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
        <Typography component="h1" variant="h5" sx={{ mb: 2 }}>
          Créer un compte
        </Typography>
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
        <form onSubmit={handleSubmit} style={{ width: "100%" }}>
          {/* Grid pour afficher Prénom et Nom côte à côte */}
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

          {/* Email Input */}
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

          {/* Password Input */}
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

          {/* Submit Button */}
          <Button type="submit" fullWidth variant="contained" color="primary">
            Valider
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default RegisterPage;
