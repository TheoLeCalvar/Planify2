import React, { useContext } from "react";
import { ProfileContext } from "@/hooks/ProfileContext";
import { RouterProvider } from "react-router-dom";
import TafManagerRouter from "../TafManagerRouter";
import LecturerRouter from "../LecturerRouter";

export default function Router() {
  const { profile } = useContext(ProfileContext);

  return (
    <>
      {profile === "taf_manager" ? (
        <RouterProvider router={TafManagerRouter} />
      ) : (
        <RouterProvider router={LecturerRouter} />
      )}
    </>
  );
}
