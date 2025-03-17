// React imports
import React from "react";

// Toastify imports for notifications
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify"; // Component for displaying toast notifications

// Material-UI imports for theming
import { ThemeProvider } from "@mui/material/styles";
import muiTheme from "@/assets/muiTheme"; // Custom Material-UI theme

// Context providers
import { AuthProvider } from "@/hooks/AuthContext"; // Provides authentication context
import { ProfileProvider } from "@/hooks/ProfileContext"; // Provides profile context

// Router
import Router from "./Router"; // Main application router

/**
 * Root component.
 * This component serves as the root of the application.
 * It wraps the entire app with necessary providers and configurations, such as:
 * - Material-UI theme provider for consistent styling
 * - Authentication and profile context providers for managing user state
 * - Toast notifications for user feedback
 * - The main router for handling navigation
 *
 * @returns {JSX.Element} - The rendered root component.
 */
export default function Root() {
  return (
    // Apply the custom Material-UI theme
    <ThemeProvider theme={muiTheme}>
      {/* Provide authentication context */}
      <AuthProvider>
        {/* Provide profile context */}
        <ProfileProvider>
          {/* Render the main application router */}
          <Router />
        </ProfileProvider>
      </AuthProvider>

      {/* Configure and render the ToastContainer for notifications */}
      <ToastContainer
        position="bottom-right" // Position of the notifications
        autoClose={5000} // Auto-close notifications after 5 seconds
        hideProgressBar={false} // Show the progress bar
        newestOnTop={false} // Display older notifications on top
        closeOnClick // Close notifications on click
        rtl={false} // Disable right-to-left layout
        pauseOnFocusLoss // Pause auto-close when the window loses focus
        draggable // Allow dragging notifications
        pauseOnHover // Pause auto-close when hovering over a notification
        theme="light" // Use the light theme for notifications
      />
    </ThemeProvider>
  );
}
