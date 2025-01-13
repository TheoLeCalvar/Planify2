import { createViewWeek } from "@schedule-x/calendar";

export const USE_MOCK_DATA = import.meta.env.VITE_USE_MOCK === "true";

export const constants = {
    CALENDAR: {
        TYPES: {
            AVAILABILITY: "availability",
        },
        SLOT_STATUS: {
            AVAILABLE: "AVAILABLE",
            UNAVAILABLE: "UNAVAILABLE",
            UNPREFERRED: "UNPREFERRED",
        },
        SLOT_COLOR: {
            AVAILABLE: "slot-color-available",
            UNAVAILABLE: "slot-color-unavailable",
            UNPREFERRED: "slot-color-unpreferred",
        },
        DEFAULT_STATUS: "AVAILABLE",
    },
    SCHEDULE_GENERAL_CONFIG: {
        locale: "fr-FR",
        dayBoundaries: {
            start: "08:00",
            end: "19:00",
        },
        weekOptions: {
            gridHeight: 500,
            nDays: 5,
            eventWidth: 95,
        },
        calendars: {
            1: {
                colorName: "color1",
                lightColors: {
                    main: "#f9d71c",
                    container: "#fff5aa",
                    onContainer: "#594800",
                },
                darkColors: {
                    main: "#fff5c0",
                    onContainer: "#fff5de",
                    container: "#a29742",
                },
            },
            2: {
                colorName: "color2",
                lightColors: {
                    main: "#f91c45",
                    container: "#ffd2dc",
                    onContainer: "#59000d",
                },
                darkColors: {
                    main: "#ffc0cc",
                    onContainer: "#ffdee6",
                    container: "#a24258",
                },
            },
            3: {
                colorName: "color3",
                lightColors: {
                    main: "#1cf9b0",
                    container: "#dafff0",
                    onContainer: "#004d3d",
                },
                darkColors: {
                    main: "#c0fff5",
                    onContainer: "#e6fff5",
                    container: "#42a297",
                },
            },
            4: {
                colorName: "color4",
                lightColors: {
                    main: "#1c7df9",
                    container: "#d2e7ff",
                    onContainer: "#002859",
                },
                darkColors: {
                    main: "#c0dfff",
                    onContainer: "#dee6ff",
                    container: "#426aa2",
                },
            },
            7: {
                colorName: "color7",
                lightColors: {
                    main: "#ff5733",
                    container: "#ffd2d2",
                    onContainer: "#590000",
                },
                darkColors: {
                    main: "#ffb3b3",
                    onContainer: "#ffe6e6",
                    container: "#a23d3d",
                },
            },
            6: {
                colorName: "color6",
                lightColors: {
                    main: "#33ff57",
                    container: "#d2ffd2",
                    onContainer: "#005900",
                },
                darkColors: {
                    main: "#b3ffb3",
                    onContainer: "#e6ffe6",
                    container: "#3da23d",
                },
            },
            5: {
                colorName: "color5",
                lightColors: {
                    main: "#7d1cf9",
                    container: "#e7d2ff",
                    onContainer: "#2d0059",
                },
                darkColors: {
                    main: "#dfc0ff",
                    onContainer: "#e6deff",
                    container: "#6a42a2",
                },
            },
        },
        views: [createViewWeek()],
    },
};
