import {
    createViewWeek,
  } from '@schedule-x/calendar'

export const USE_MOCK_DATA = import.meta.env.VITE_USE_MOCK === 'true';

export const constants = {
    CALENDAR: {
        TYPES: {
            AVAILABILITY: 'availability',
        },
        SLOT_STATUS: {
            AVAILABLE: 'AVAILABLE',
            UNAVAILABLE: 'UNAVAILABLE',
            UNPREFERRED: 'UNPREFERRED',
        },
        SLOT_COLOR: {
            AVAILABLE: 'slot-color-available',
            UNAVAILABLE: 'slot-color-unavailable',
            UNPREFERRED: 'slot-color-unpreferred'
        },
        DEFAULT_STATUS: 'AVAILABLE',
    },
    SCHEDULE_GENERAL_CONFIG: {
        locale: "fr-FR",
        dayBoundaries: {
          start: '08:00',
          end: '19:00',
        },
        weekOptions: {
          gridHeight: 500,
          nDays: 5,
          eventWidth: 95,
        },
        views: [createViewWeek()]
    }
}