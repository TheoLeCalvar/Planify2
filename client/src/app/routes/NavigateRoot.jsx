// React imports
import React from "react";
import useAxiosInterceptor from "@/hooks/useAxiosInterceptor"; // Custom hook for setting up Axios interceptors
import { Outlet } from "react-router-dom"; // React Router component for rendering nested routes

/**
 * NavigateRoot component.
 * This component serves as a root layout for nested routes.
 * It sets up Axios interceptors globally using the `useAxiosInterceptor` hook
 * and renders child routes using the `Outlet` component.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function NavigateRoot() {
  // Initialize Axios interceptors
  useAxiosInterceptor();

  return <Outlet />; // Render nested routes
}
