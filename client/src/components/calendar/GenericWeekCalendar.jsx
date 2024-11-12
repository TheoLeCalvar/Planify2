import { useCalendarApp, ScheduleXCalendar } from '@schedule-x/react'
import React, { useContext } from 'react'

import { CalendarContext } from '../../context/CalendarContext'

import generateClassSlots from '../../helper/classSlot'
import { constants } from '../../contants'
import locale from '../../config/locale.json'

import '@schedule-x/theme-default/dist/index.css'
import './calendar.css'
import './genericWeekCalendar.css'
 
export default function GenericWeekCalendar() {
  //const eventService = createEventsServicePlugin()
  const { onGenericChange, genericEventService: eventService } = useContext(CalendarContext)

  const onEventClick = (event) => {
    const currentStatus = Object.values(constants.CALENDAR.SLOT_STATUS).indexOf(event.status)
    const newStatusId = (currentStatus + 1) % Object.values(constants.CALENDAR.SLOT_STATUS).length
    const newStatus = Object.values(constants.CALENDAR.SLOT_STATUS)[newStatusId]
    const updatedEvent = {
      ...event, 
      status: newStatus,
      people: [locale.slotStatus[newStatus]],
      _options: {
        additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]],
      }}
    eventService.update(updatedEvent)
    onGenericChange(updatedEvent)
  }

  const initialEvents = generateClassSlots('2000-01-03', '2000-01-08')

  // useState(
  //   () => {
  //     setGenericWeekEvents(initialEvents)
  //   },
  //   []
  // )
 
  const calendar = useCalendarApp({
    ...constants.SCHEDULE_GENERAL_CONFIG,
    selectedDate: '2000-01-03',
    events: initialEvents,
    callbacks: {
      onEventClick
    }

  }, [eventService])
  
  return (
    <div className='generic-week-calendar'>
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  )
}