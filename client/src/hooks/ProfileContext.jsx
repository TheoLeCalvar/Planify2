// React imports
import React, { createContext, useState } from "react";
import PropTypes from "prop-types";

// Create the ProfileContext
export const ProfileContext = createContext();

/**
 * ProfileProvider Component
 * This component provides a context for managing the user's profile.
 * It determines the default profile based on the user's roles and allows the profile to be updated dynamically.
 *
 * @param {Object} props - The component props.
 * @param {React.ReactNode} props.children - The child components that will have access to the ProfileContext.
 * @returns {JSX.Element} - The ProfileProvider component.
 */
export const ProfileProvider = ({ children }) => {
  // Retrieve user data from localStorage or sessionStorage
  const data =
    localStorage.getItem("userData") || sessionStorage.getItem("userData");
  const user = JSON.parse(data);

  /**
   * Determines the default profile based on the user's roles.
   * Priority order:
   * 1. Admin
   * 2. TAF Manager
   * 3. Lecturer
   * If no roles are found, defaults to "taf_manager".
   *
   * @returns {string} - The default profile.
   */
  const defaultProfile = (() => {
    if (user?.profiles?.admin) {
      return "admin";
    } else if (user?.profiles?.taf_manager) {
      return "taf_manager";
    } else if (user?.profiles?.lecturer) {
      return "lecturer";
    }
    return "taf_manager"; // Default profile if no roles are found
  })();

  // State for managing the current profile
  const [profile, setProfile] = useState(defaultProfile);

  return (
    /**
     * Provide the profile and setProfile function to child components.
     */
    <ProfileContext.Provider value={{ profile, setProfile }}>
      {children}
    </ProfileContext.Provider>
  );
};

// Define the expected prop types for the ProfileProvider component
ProfileProvider.propTypes = {
  /**
   * The child components that will have access to the ProfileContext.
   */
  children: PropTypes.node.isRequired,
};
