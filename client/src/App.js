import { useCalendarApp, ScheduleXCalendar } from '@schedule-x/react'
import { createEventsServicePlugin } from '@schedule-x/events-service'
import React, { useEffect } from 'react'

import generateClassSlots from './helper/classSlot'
import exportCalendar from './helper/exportCalendar'
import importCalendar from './helper/importCalendar'
import { constants } from './contants'
import locale from './config/locale.json'
import Button from '@mui/material/Button';

 
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
    ...constants.SCHEDULE_GENERAL_CONFIG,
    events: generateClassSlots('2024-09-01', '2024-12-31'),
    callbacks: {
      onEventClick
    }

  }, [eventService])
  
  return (
    <div>
      <ScheduleXCalendar calendarApp={calendar} />
      <Button variant="text" onClick={() => {exportCalendar(eventService.getAll(), constants.CALENDAR.TYPES.AVAILABILITY)}}>Exporter</Button>
      <Button variant="text" onClick={() => {
        importCalendar(constants.CALENDAR.TYPES.AVAILABILITY)
        .then((events) => {
          eventService.set(events)
        })
        .catch((error) => {
          console.error(error)
        })
      }}>Importer</Button>

    </div>
  )
}
 
export default CalendarApp