import React, { createContext, useState } from "react";
import PropTypes from "prop-types";

export const ProfileContext = createContext();

export const ProfileProvider = ({ children }) => {
  const data =
    localStorage.getItem("userData") || sessionStorage.getItem("userData");
  const user = JSON.parse(data);

  const defaultProfile = (() => {
    if (user?.profiles?.admin) {
      return "admin";
    } else if (user?.profiles?.taf_manager) {
      return "taf_manager";
    } else if (user?.profiles?.lecturer) {
      return "lecturer";
    }
    return "taf_manager";
  })();

  const [profile, setProfile] = useState(defaultProfile);

  return (
    <ProfileContext.Provider value={{ profile, setProfile }}>
      {children}
    </ProfileContext.Provider>
  );
};

ProfileProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
