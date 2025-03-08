// React imports
import React from "react";
import { Outlet, useLoaderData } from "react-router-dom";

// Local imports
import axiosInstance from "@/config/axiosConfig";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}`);
  return { taf: response.data };
}

export async function loaderLecturer({ params }) {
  const [taf, lessons] = await Promise.all([
    axiosInstance.get(`/taf/${params.idTAF}`),
    axiosInstance.get(`/lecturer/lessons/${params.idTAF}`),
  ]);
  return { taf: taf.data, lessons: lessons.data };
}

export default function TAF() {
  const data = useLoaderData();

  return (
    <>
      <Outlet context={data} />
    </>
  );
}
