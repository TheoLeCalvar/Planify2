import dayjs from 'dayjs';

import slotConfig from "../config/slots.json";
import locale from "../config/locale.json";
import { constants } from '../contants'

export default function generateClassSlots(startDate, endDate){
    const events = [];
    let currentId = 1;
    let currentDate = dayjs(startDate);


    while (currentDate.isBefore(endDate) || currentDate.isSame(endDate, 'day')) {
        if (Object.keys(slotConfig).includes(currentDate.day().toString())) { // Monday to Friday
            const slots = slotConfig[currentDate.day()];
            // eslint-disable-next-line no-loop-func
            slots.forEach((slot, index) => {
                events.push({
                    id: currentId.toString(),
                    title: `Créneau ${index + 1}`,
                    start: currentDate.format(`YYYY-MM-DD ${slot.start}`),
                    end: currentDate.format(`YYYY-MM-DD ${slot.end}`),
                    status: constants.CALENDAR.DEFAULT_STATUS,
                    people: [locale.slotStatus[constants.CALENDAR.DEFAULT_STATUS]],
                    _options: {
                        additionalClasses: [constants.CALENDAR.SLOT_COLOR[constants.CALENDAR.DEFAULT_STATUS]],
                    },
                });
                currentId++;
            });
        }
        currentDate = currentDate.add(1, 'day');
    }

    return events;

}