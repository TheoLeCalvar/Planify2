// React imports
import React from "react";

// Custom components
import SideBarComponent from "@/features/side-bar/components/SideBarComponent";

/**
 * SideBar component.
 * This component serves as a wrapper for the `SideBarComponent`.
 * It is responsible for rendering the sidebar of the application.
 *
 * The actual implementation and logic of the sidebar are handled by the `SideBarComponent`.
 *
 * @returns {JSX.Element} - The rendered sidebar component.
 */
const SideBar = () => {
  return <SideBarComponent />; // Render the custom sidebar component
};

export default SideBar;
