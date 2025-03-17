// Import the `createViewWeek` function from the ScheduleX calendar library
import { createViewWeek } from "@schedule-x/calendar";

/**
 * Global constants for the application.
 * This file contains configuration values and constants used throughout the app.
 */
export const constants = {
  CALENDAR: {
    TYPES: {
      AVAILABILITY: "availability", // Type of calendar for managing availability
    },
    SLOT_STATUS: {
      AVAILABLE: "AVAILABLE", // Status indicating the slot is available
      UNAVAILABLE: "UNAVAILABLE", // Status indicating the slot is unavailable
      UNPREFERRED: "UNPREFERRED", // Status indicating the slot is unpreferred
    },
    SLOT_COLOR: {
      AVAILABLE: "slot-color-available", // CSS class for available slots
      UNAVAILABLE: "slot-color-unavailable", // CSS class for unavailable slots
      UNPREFERRED: "slot-color-unpreferred", // CSS class for unpreferred slots
    },
    DEFAULT_STATUS: "AVAILABLE", // Default status for calendar slots
  },
  SCHEDULE_GENERAL_CONFIG: {
    locale: "fr-FR", // Locale for the calendar (French)
    dayBoundaries: {
      start: "08:00", // Start time for the calendar day
      end: "19:00", // End time for the calendar day
    },
    weekOptions: {
      gridHeight: 500, // Height of the calendar grid
      nDays: 5, // Number of days displayed in the week view
      eventWidth: 95, // Width of events in the calendar
    },
    calendars: {
      // Configuration for different calendar colors
      1: {
        colorName: "color1",
        lightColors: {
          main: "#f9d71c", // Main color in light mode
          container: "#fff5aa", // Container color in light mode
          onContainer: "#594800", // Text color on container in light mode
        },
        darkColors: {
          main: "#fff5c0", // Main color in dark mode
          onContainer: "#fff5de", // Text color on container in dark mode
          container: "#a29742", // Container color in dark mode
        },
      },
      2: {
        colorName: "color2",
        lightColors: {
          main: "#f91c45", // Main color in light mode
          container: "#ffd2dc", // Container color in light mode
          onContainer: "#59000d", // Text color on container in light mode
        },
        darkColors: {
          main: "#ffc0cc", // Main color in dark mode
          onContainer: "#ffdee6", // Text color on container in dark mode
          container: "#a24258", // Container color in dark mode
        },
      },
      3: {
        colorName: "color3",
        lightColors: {
          main: "#1cf9b0", // Main color in light mode
          container: "#dafff0", // Container color in light mode
          onContainer: "#004d3d", // Text color on container in light mode
        },
        darkColors: {
          main: "#c0fff5", // Main color in dark mode
          onContainer: "#e6fff5", // Text color on container in dark mode
          container: "#42a297", // Container color in dark mode
        },
      },
      4: {
        colorName: "color4",
        lightColors: {
          main: "#1c7df9", // Main color in light mode
          container: "#d2e7ff", // Container color in light mode
          onContainer: "#002859", // Text color on container in light mode
        },
        darkColors: {
          main: "#c0dfff", // Main color in dark mode
          onContainer: "#dee6ff", // Text color on container in dark mode
          container: "#426aa2", // Container color in dark mode
        },
      },
      7: {
        colorName: "color7",
        lightColors: {
          main: "#ff5733", // Main color in light mode
          container: "#ffd2d2", // Container color in light mode
          onContainer: "#590000", // Text color on container in light mode
        },
        darkColors: {
          main: "#ffb3b3", // Main color in dark mode
          onContainer: "#ffe6e6", // Text color on container in dark mode
          container: "#a23d3d", // Container color in dark mode
        },
      },
      6: {
        colorName: "color6",
        lightColors: {
          main: "#33ff57", // Main color in light mode
          container: "#d2ffd2", // Container color in light mode
          onContainer: "#005900", // Text color on container in light mode
        },
        darkColors: {
          main: "#b3ffb3", // Main color in dark mode
          onContainer: "#e6ffe6", // Text color on container in dark mode
          container: "#3da23d", // Container color in dark mode
        },
      },
      5: {
        colorName: "color5",
        lightColors: {
          main: "#7d1cf9", // Main color in light mode
          container: "#e7d2ff", // Container color in light mode
          onContainer: "#2d0059", // Text color on container in light mode
        },
        darkColors: {
          main: "#dfc0ff", // Main color in dark mode
          onContainer: "#e6deff", // Text color on container in dark mode
          container: "#6a42a2", // Container color in dark mode
        },
      },
    },
    views: [createViewWeek()], // Default view configuration for the calendar
  },
};

/**
 * Drawer width for the application's side navigation.
 */
export const drawerWidth = 240;

/**
 * Height of the application's AppBar (header).
 */
export const appBarHeight = 64;
