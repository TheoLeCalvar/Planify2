// React imports
import React from "react";

// Data imports
import axiosInstance from "@/config/axiosConfig";
import { USE_MOCK_DATA } from "@/config/constants";

// Custom components
import AppBarComponent from "@/features/app-bar/components/AppBarComponent";

// Data loader function
export async function loader() {
  if (USE_MOCK_DATA) {
    return [
      {
        id: 1,
        name: "TAF DCL",
        description: "Développement Collaboratif de Logiciel",
      },
      {
        id: 2,
        name: "TAF EDP",
        description: "Environnement de Développement de Projet",
      },
      {
        id: 3,
        name: "TAF GPE",
        description: "Gestion de Projet et Entrepreneuriat",
      },
    ];
  }
  const response = await axiosInstance.get("/alltaf");
  return response.data;
}

const AppBar = () => {
  return <AppBarComponent />;
};

export default AppBar;
