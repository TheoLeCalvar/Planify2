// React imports
import React from "react";

// Data imports
import axiosInstance from "@/config/axiosConfig";

// Custom components
import AppBarComponent from "@/features/app-bar/components/AppBarComponent";

// Data loader function
export async function loader() {
  const response = await axiosInstance.get("/taf");
  return response.data;
}

const AppBar = () => {
  return <AppBarComponent />;
};

export default AppBar;
