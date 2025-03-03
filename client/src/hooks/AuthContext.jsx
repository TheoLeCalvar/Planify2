import React, { createContext } from "react";
import PropTypes from "prop-types";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const login = (userData, authToken, remember) => {
    if (remember) {
      localStorage.setItem("authToken", authToken);
      localStorage.setItem("userData", JSON.stringify(userData));
    } else {
      sessionStorage.setItem("authToken", authToken);
      sessionStorage.setItem("userData", JSON.stringify(userData));
    }
  };

  const logout = () => {
    localStorage.removeItem("authToken");
    sessionStorage.removeItem("authToken");
    localStorage.removeItem("userData");
    sessionStorage.removeItem("userData");
  };

  const isLogin = () => {
    return (
      !!localStorage.getItem("authToken") ||
      !!sessionStorage.getItem("authToken")
    );
  };

  const getUser = () => {
    const data =
      localStorage.getItem("userData") || sessionStorage.getItem("userData");
    return JSON.parse(data);
  };

  return (
    <AuthContext.Provider value={{ getUser, login, logout, isLogin }}>
      {children}
    </AuthContext.Provider>
  );
};

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
