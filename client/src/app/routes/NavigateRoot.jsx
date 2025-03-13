import React from "react";
import useAxiosInterceptor from "@/hooks/useAxiosInterceptor";
import { Outlet } from "react-router-dom";

export default function NavigateRoot() {
  useAxiosInterceptor();

  return <Outlet />;
}
