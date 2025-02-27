// React imports
import React from "react";

// Local imports
import SolverConfigSelectorComponent from "@/features/solver-config/components/SolverConfigSelector";
import axiosInstance from "@/config/axiosConfig";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/configs`);
  return response.data;
}

export default function SolverConfigSelector() {
  return <SolverConfigSelectorComponent />;
}
