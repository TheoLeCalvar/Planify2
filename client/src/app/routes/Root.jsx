// React imports
import React from "react";
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify";
import { ThemeProvider } from "@mui/material/styles";
import muiTheme from "@/assets/muiTheme";
import { AuthProvider } from "@/hooks/AuthContext";
import { ProfileProvider } from "@/hooks/ProfileContext";
import Router from "./Router";

export default function Root() {
  return (
    <ThemeProvider theme={muiTheme}>
      <AuthProvider>
        <ProfileProvider>
          <Router />
        </ProfileProvider>
      </AuthProvider>
      <ToastContainer
        position="bottom-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
    </ThemeProvider>
  );
}
