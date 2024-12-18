// src/components/LoginPage.js

import React, { useState } from 'react';
import { Container, Box, Typography, TextField, Button, FormControlLabel, Checkbox, Link } from '@mui/material';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);

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
    // Handle login logic here (e.g., API call)
    console.log('Email:', email);
    console.log('Password:', password);
    console.log('Remember me:', rememberMe);
  };

  return (
    <Container maxWidth="xs">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          mt: 8,
          p: 3,
          boxShadow: 3,
          borderRadius: 2,
          bgcolor: 'background.paper',
        }}
      >
        <Typography component="h1" variant="h5" sx={{ mb: 2 }}>
          Login
        </Typography>
        <form onSubmit={handleSubmit} style={{ width: '100%' }}>
          {/* Email Input */}
          <TextField
            label="Email Address"
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
            label="Password"
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
            control={<Checkbox value={rememberMe} onChange={handleCheckboxChange} color="primary" />}
            label="Remember me"
          />
          {/* Forgotten Password Link */}
          <Box sx={{ textAlign: 'right', mb: 2 }}>
            <Link href="#" variant="body2">
              Forgotten password?
            </Link>
          </Box>
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
