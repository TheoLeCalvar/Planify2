import { useCalendarApp, ScheduleXCalendar } from '@schedule-x/react'
import {
  createViewDay,
  createViewMonthAgenda,
  createViewMonthGrid,
  createViewWeek,
} from '@schedule-x/calendar'
import { createEventsServicePlugin } from '@schedule-x/events-service'
import React, { useEffect } from 'react'

import generateClassSlots from './helper/classSlot'
import { constants } from './contants'
import locale from './config/locale.json'
 
import '@schedule-x/theme-default/dist/index.css'
import './calendar.css'
 
function CalendarApp() {
  const eventService = createEventsServicePlugin()

  const onEventClick = (event) => {
    const currentStatus = Object.values(constants.CALENDAR.SLOT_STATUS).indexOf(event.status)
    const newStatusId = (currentStatus + 1) % Object.values(constants.CALENDAR.SLOT_STATUS).length
    const newStatus = Object.values(constants.CALENDAR.SLOT_STATUS)[newStatusId]
    eventService.update({
      ...event, 
      status: newStatus,
      people: [locale.slotStatus[newStatus]],
      _options: {
        additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]],
      }})
  }
 
  const calendar = useCalendarApp({
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
    views: [createViewWeek()],
    events: generateClassSlots('2024-09-01', '2024-12-31'),
    callbacks: {
      onEventClick
    }

  }, [eventService])
 
  useEffect(() => {
    // get all events
    calendar.eventsService.getAll()
  }, [])
 
  return (
    <div>
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  )
}
 
export default CalendarApp