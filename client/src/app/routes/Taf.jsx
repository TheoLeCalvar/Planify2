// React imports
import React from "react";
import { Outlet, useLoaderData } from "react-router-dom";

// Local imports
import axiosInstance from "@/config/axiosConfig";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}`);
  return response.data;
}

export default function TAF() {
  const taf = useLoaderData();

  return (
    <>
      <Outlet context={{ taf }} />
    </>
  );
}
