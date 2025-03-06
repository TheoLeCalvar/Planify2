// React imports
import React from "react";
import { Outlet } from "react-router-dom";
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify";
import { ThemeProvider } from "@mui/material/styles";
import muiTheme from "@/assets/muiTheme";
import useAxiosInterceptor from "@/hooks/useAxiosInterceptor";
import { AuthProvider } from "@/hooks/AuthContext";

export default function Root() {
  useAxiosInterceptor();

  return (
    <ThemeProvider theme={muiTheme}>
      <AuthProvider>
        <Outlet />
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
