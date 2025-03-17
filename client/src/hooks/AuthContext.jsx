// React imports
import React, { createContext } from "react";
import PropTypes from "prop-types";

// Create the AuthContext
export const AuthContext = createContext();

/**
 * AuthProvider Component
 * This component provides authentication-related functionality and state to its children.
 * It uses React Context to share authentication methods and data across the application.
 *
 * @param {Object} props - The component props.
 * @param {React.ReactNode} props.children - The child components that will have access to the AuthContext.
 * @returns {JSX.Element} - The AuthProvider component.
 */
export const AuthProvider = ({ children }) => {
  /**
   * Logs in the user by storing their authentication token and user data.
   * The data is stored in either `localStorage` or `sessionStorage` based on the `remember` flag.
   *
   * @param {Object} userData - The user data to store.
   * @param {string} authToken - The authentication token.
   * @param {boolean} remember - Whether to remember the user (store data in `localStorage`).
   */
  const login = (userData, authToken, remember) => {
    if (remember) {
      localStorage.setItem("authToken", authToken);
      localStorage.setItem("userData", JSON.stringify(userData));
    } else {
      sessionStorage.setItem("authToken", authToken);
      sessionStorage.setItem("userData", JSON.stringify(userData));
    }
  };

  /**
   * Logs out the user by removing their authentication token and user data
   * from both `localStorage` and `sessionStorage`.
   */
  const logout = () => {
    localStorage.removeItem("authToken");
    sessionStorage.removeItem("authToken");
    localStorage.removeItem("userData");
    sessionStorage.removeItem("userData");
  };

  /**
   * Checks if the user is logged in by verifying the presence of an authentication token
   * in either `localStorage` or `sessionStorage`.
   *
   * @returns {boolean} - `true` if the user is logged in, `false` otherwise.
   */
  const isLogin = () => {
    return (
      !!localStorage.getItem("authToken") ||
      !!sessionStorage.getItem("authToken")
    );
  };

  /**
   * Retrieves the user data from `localStorage` or `sessionStorage`.
   *
   * @returns {Object|null} - The parsed user data object, or `null` if no data is found.
   */
  const getUser = () => {
    const data =
      localStorage.getItem("userData") || sessionStorage.getItem("userData");
    return JSON.parse(data);
  };

  return (
    /**
     * Provide the authentication methods and state to child components.
     */
    <AuthContext.Provider value={{ getUser, login, logout, isLogin }}>
      {children}
    </AuthContext.Provider>
  );
};

// Define the expected prop types for the AuthProvider component
AuthProvider.propTypes = {
  /**
   * The child components that will have access to the AuthContext.
   */
  children: PropTypes.node.isRequired,
};
