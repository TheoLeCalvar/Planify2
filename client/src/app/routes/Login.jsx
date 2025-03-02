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
import axiosInstance from "@/config/axiosConfig";
import { AuthContext } from "@/hooks/AuthContext";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(true);

  const navigate = useNavigate();
  const { isLogin, login } = useContext(AuthContext);

  useEffect(() => {
    if (isLogin()) {
      console.log(
        "Utilisateur déjà connecté, redirection vers la page d'accueil",
      );
      navigate("/");
    }
  }, [navigate]);

  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

  const handleCheckboxChange = (event) => {
    setRememberMe(event.target.checked);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    axiosInstance
      .post("/auth/login", { mail: email, password })
      .then((response) => {
        login({ roles: response.data.roles }, response.data.token, rememberMe);
        navigate("/");
        toast.success("Connexion réussie !");
      })
      .catch((error) => {
        if (error.response?.status === 401) {
          toast.error("Mot de passe incorrect !");
        } else if (error.response?.status === 404) {
          toast.error("Utilisateur non trouvé !");
        } else {
          toast.error("Erreur lors de la connexion !");
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
          Se connecter
        </Typography>
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
                value={rememberMe}
                onChange={handleCheckboxChange}
                color="primary"
              />
            }
            label="Rester connecter"
          />
          {/* Forgotten Password Link */}
          {/* <Box sx={{ textAlign: "right", mb: 2 }}>
            <Link href="#" variant="body2">
              Mot de passe oublié ?
            </Link>
          </Box> */}
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
