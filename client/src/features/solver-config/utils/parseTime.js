// Date imports
import dayjs from "dayjs";

/**
 * parseTime Utility Function
 * This function ensures that the provided value is a DayJS instance.
 * If the value is already a DayJS instance, it is returned as-is.
 * Otherwise, it attempts to parse the value into a DayJS instance using the specified format.
 *
 * @param {any} value - The value to parse. Can be a string, a DayJS instance, or another type.
 * @param {string} [format="HH:mm"] - The format to use when parsing the value (default is "HH:mm").
 * @returns {Dayjs} - A DayJS instance representing the parsed time.
 */
export const parseTime = (value, format = "HH:mm") =>
  dayjs.isDayjs(value) ? value : dayjs(value, format);

export default parseTime;
