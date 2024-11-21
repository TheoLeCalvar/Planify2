import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react";
import React, { useContext } from "react";

import { CalendarContext } from "../../context/CalendarContext";

import { constants } from "../../contants";
import locale from "../../config/locale.json";

import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
dayjs.extend(isoWeek);

import "@schedule-x/theme-default/dist/index.css";
import "./calendar.css";
import "./genericWeekCalendar.css";

export default function GenericWeekCalendar() {
    const {
        onGenericChange,
        genericEventService: eventService,
        initialEvents,
    } = useContext(CalendarContext);

    const onEventClick = (event) => {
        const currentStatus = Object.values(
            constants.CALENDAR.SLOT_STATUS
        ).indexOf(event.status);
        const newStatusId =
            (currentStatus + 1) %
            Object.values(constants.CALENDAR.SLOT_STATUS).length;
        const newStatus = Object.values(constants.CALENDAR.SLOT_STATUS)[
            newStatusId
        ];
        const updatedEvent = {
            ...event,
            status: newStatus,
            people: [locale.slotStatus[newStatus]],
            _options: {
                additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]],
            },
        };
        eventService.update(updatedEvent);
        onGenericChange(updatedEvent);
    };

    //const initialEvents = generateClassSlots('2000-01-03', '2000-01-08')
    const genericInitialEvents = initialEvents.filter(
        (v, i, a) => a.findIndex((t) => t.inWeekId === v.inWeekId) === i
    );

    const convertDate = (date) => {
        const inputDate = dayjs(date); // Parser la date
        const weekdayOffset = inputDate.isoWeekday() - 1; // Décalage du jour dans la semaine (lundi=0, dimanche=6)

        return dayjs("2000-01-03 00:00") // Début de la première semaine ISO 2000
            .add(weekdayOffset, "day") // Ajouter le même jour de la semaine
            .hour(inputDate.hour()) // Conserver l'heure
            .minute(inputDate.minute()) // Conserver les minutes
			.format('YYYY-MM-DD HH:mm'); // Formater la date
    };

    genericInitialEvents.forEach((event) => {
        event.start = convertDate(event.start);
		event.end = convertDate(event.end);
    });
    console.log(genericInitialEvents);

    const calendar = useCalendarApp(
        {
            ...constants.SCHEDULE_GENERAL_CONFIG,
            selectedDate: "2000-01-03",
            events: genericInitialEvents,
            callbacks: {
                onEventClick,
            },
        },
        [eventService]
    );

    return (
        <div className="generic-week-calendar">
            <ScheduleXCalendar calendarApp={calendar} />
        </div>
    );
}
