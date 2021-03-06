package com.github.maxopoly.zeus.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ParsingUtils {
	
	public static long parseTime(String arg, TimeUnit unit) {
		long millis = parseTime(arg);
		return unit.convert(millis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Parses a time value specified in a config. This allows to specify human
	 * readable time values easily, instead of having to specify every amount in
	 * ticks or seconds. The unit of a number specifed by the letter added after it,
	 * for example 5h means 5 hours or 34s means 34 seconds. Possible modifiers are:
	 * t (ticks), s (seconds), m (minutes), h (hours) and d (days)
	 * <p>
	 * Additionally you can combine those amounts in any way you want, for example
	 * you can specify 3h5m43s as 3 hours, 5 minutes and 43 seconds. This doesn't
	 * have to be sorted and may even list the same unit multiple times for
	 * different values, but the values are not allowed to be separated by anything
	 *
	 * @param input Parsed string containing the time format
	 * @return How many milliseconds the given time value is
	 */
	public static long parseTime(String input) {
		input = input.replace(" ", "").replace(",", "").toLowerCase();
		long result = 0;
		try {
			result += Long.parseLong(input);
			return result;
		} catch (NumberFormatException e) {
		}
		while (!input.equals("")) {
			String typeSuffix = getSuffix(input, Character::isLetter);
			input = input.substring(0, input.length() - typeSuffix.length());
			String numberSuffix = getSuffix(input, Character::isDigit);
			if (typeSuffix.length() == 0 && numberSuffix.length() == 0) {
				//unparseable character
				break;
			}
			input = input.substring(0, input.length() - numberSuffix.length());
			long duration;
			if (numberSuffix.length() == 0) {
				duration = 1;
			} else {
				duration = Long.parseLong(numberSuffix);
			}
			switch (typeSuffix) {
			case "ms":
			case "milli":
			case "millis":
				result += duration;
				break;
			case "s": // seconds
			case "sec":
			case "second":
			case "seconds":
				result += TimeUnit.SECONDS.toMillis(duration);
				break;
			case "m": // minutes
			case "min":
			case "minute":
			case "minutes":
				result += TimeUnit.MINUTES.toMillis(duration);
				break;
			case "h": // hours
			case "hour":
			case "hours":
				result += TimeUnit.HOURS.toMillis(duration);
				break;
			case "d": // days
			case "day":
			case "days":
				result += TimeUnit.DAYS.toMillis(duration);
				break;
			case "w": // weeks
			case "week":
			case "weeks":
				result += TimeUnit.DAYS.toMillis(duration * 7);
				break;
			case "month": // weeks
			case "months":
				result += TimeUnit.DAYS.toMillis(duration * 30);
				break;
			case "y":
			case "year":
			case "years":
				result += TimeUnit.DAYS.toMillis(duration * 365);
				break;
			case "never":
			case "inf":
			case "infinite":
			case "perm":
			case "perma":
			case "forever":
				// 1000 years counts as perma
				result += TimeUnit.DAYS.toMillis(365 * 1000);
			default:
				// just ignore it
			}
		}
		return result;
	}

	private static String getSuffix(String arg, Predicate<Character> selector) {
		StringBuilder number = new StringBuilder();
		for (int i = arg.length() - 1; i >= 0; i--) {
			if (selector.test(arg.charAt(i))) {
				number.insert(0, arg.substring(i, i + 1));
			} else {
				break;
			}
		}
		return number.toString();
	}

}
