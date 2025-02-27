// Date imports
import dayjs from "dayjs";

// Ensures that the time is a DayJS instance.
export const parseTime = (value, format = "HH:mm") =>
  dayjs.isDayjs(value) ? value : dayjs(value, format);

export default parseTime;
