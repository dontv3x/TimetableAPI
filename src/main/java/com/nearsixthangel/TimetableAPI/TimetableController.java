package com.nearsixthangel.TimetableAPI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

@RestController
public class TimetableController {

	private final Timetable timetable = new Timetable();
	private HashMap<String, HashMap<String, String>> cachedTimetable = null;

	@GetMapping("/generateTimetable")
	public HashMap<String, HashMap<String, String>> generateTimetable() {
		if (cachedTimetable == null) {  // Generate only if not cached
			cachedTimetable = timetable.getTimetable();
		}
		return cachedTimetable;
	}

	@GetMapping("/resetTimetable")
	public String resetTimetable() {
		cachedTimetable = null;  // Reset to trigger new generation
		timetable.resetCounts();  // Reset subject counts
		return "Timetable reset successfully!";
	}
}

class Timetable {
	private final String[] subjects = {
			"Mathematics", "Further Mathematics", "English Grammar",
			"English Composition", "English Comprehension", "English Summary",
			"Geography / Literature", "ICT", "Vocational", "Trade",
			"Biology", "Chemistry", "Physics", "Civics", "Economics", "Language", "Elocution"
	};

	private final String[] times = {
			"08:05 - 08:45", "08:45 - 09:25", "09:25 - 10:05",
			"10:05 - 10:45", "11:10 - 11:50", "11:50 - 12:30",
			"12:30 - 1:10", "1:10 - 1:50", "1:50 - 2:30", "2:50 - 3:30"
	};

	private final int[][] count = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{5, 4, 1, 1, 1, 1, 3, 2, 3, 3, 3, 4, 4, 2, 4, 2, 1}
	};

	private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
	private final Random random = new Random();

	public HashMap<String, HashMap<String, String>> getTimetable() {
		HashMap<String, HashMap<String, String>> timetable = new HashMap<>();

		for (String day : days) {
			HashMap<String, String> dailySchedule = new HashMap<>();

			for (String time : times) {
				int subjectIndex;
				int attempts = 0;

				// Try to find a subject that hasn't reached its weekly limit
				do {
					subjectIndex = random.nextInt(subjects.length);
					attempts++;
				} while (count[0][subjectIndex] >= count[1][subjectIndex] && attempts < subjects.length * 2);

				// If max attempts reached, add a "Free Period" as fallback
				if (attempts < subjects.length * 2) {
					dailySchedule.put(time, subjects[subjectIndex]);
					count[0][subjectIndex]++;
				} else {
					dailySchedule.put(time, "Free Period");
				}
			}

			timetable.put(day, dailySchedule);
		}
		return timetable;
	}

	// Reset subject counts to zero for a fresh timetable generation
	public void resetCounts() {
		Arrays.fill(count[0], 0);
	}
}
