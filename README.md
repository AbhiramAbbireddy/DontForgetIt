## Don't Forget This – Smart Reminder App

Don't Forget This is an Android reminder app built in Java that helps you remember important tasks and events.  
The focus is on a clean, calm interface and a simple workflow that you can actually use every day, instead of a complex productivity system that you abandon after a week.

### Problem the App Solves

Most people rely on memory for small but important tasks:
- Calling someone at a specific time
- Taking medicine on schedule
- Submitting an assignment or report
- Remembering appointments or events

Forgetting these is easy, especially when you are busy or tired.  
This app gives you a lightweight place to quickly add a reminder, see what is coming up today, and get notified before it is due.

### Core Features

- **Beautiful home screen**
  - Today section with date and a quick summary of how many reminders you have
  - List of reminders shown as rounded cards, inspired by a modern Figma design

- **Add reminder flow**
  - Enter reminder title
  - Pick date with a date picker
  - Pick time with a time picker
  - Automatically sets a notification before the scheduled time

- **Persistent storage**
  - All reminders are saved in a local Room database
  - Closing and reopening the app does not lose data

- **Smart notifications**
  - Android `AlarmManager` schedules a notification
  - Notification appears with the reminder title
  - Logic ensures the notification fires even if the exact "10 minutes before" time has already passed

- **Empty state**
  - When there are no reminders, the home screen shows a friendly message instead of a blank list

### Tech Stack

- **Language**: Java
- **Minimum Android SDK**: 24
- **Target / Compile SDK**: 36
- **UI**
  - XML layouts
  - Material Components (`com.google.android.material`)
  - RecyclerView for the reminders list
- **Data**
  - Room Database (`ReminderDatabase`, `ReminderDao`, `Reminder` entity/model)
- **System APIs**
  - `AlarmManager` and `PendingIntent` for alarms
  - `BroadcastReceiver` for firing notifications (`ReminderReceiver`)
  - Android 13+ notification permission handling

### Project Structure

High level Java package structure:

- `com.example.dontforget.activities`
  - `MainActivity` – home screen with list of reminders and add button
  - `AddReminderActivity` – screen to input title, date and time
  - `ReminderReceiver` – receives alarm events and shows notifications

- `com.example.dontforget.model`
  - `Reminder` – data model representing a single reminder

- `com.example.dontforget.database`
  - `ReminderDao` – Room DAO for CRUD operations on reminders
  - `ReminderDatabase` – Room database singleton

- `com.example.dontforget.adapter`
  - `ReminderAdapter` – RecyclerView adapter to render reminder cards

Key layout and drawable resources:

- `activity_main.xml` – home screen UI (date header, today label, list, bottom navigation, floating add button)
- `activity_add_reminder.xml` – add reminder form
- `item_reminder.xml` – card layout for a single reminder
- `bg_reminder_card.xml` – rounded card background
- `bg_icon_circle.xml` – circular background for the small icon on each card
- `bg_badge.xml` – pill shaped background for the "10 min before" badge
- `bg_bottom_nav.xml` – rounded background for the bottom navigation bar

### How the App Works (Flow)

1. **Launch**
   - `MainActivity` loads.
   - Room database is initialized.
   - All reminders are loaded from `ReminderDatabase` using `ReminderDao`.
   - `ReminderAdapter` displays them in a `RecyclerView`.
   - If there are no reminders, an "empty state" message is shown.

2. **Add Reminder**
   - User taps the floating `+` button on the home screen.
   - `AddReminderActivity` opens.
   - User enters:
     - Title text
     - Date via `DatePickerDialog`
     - Time via `TimePickerDialog`
   - On save:
     - Validation ensures that all fields are filled.
     - The reminder is returned to `MainActivity` via `setResult`.

3. **Save and Display**
   - `MainActivity.onActivityResult` receives the title, date and time.
   - A `Reminder` object is created and inserted into Room using `ReminderDao`.
   - The list is reloaded from the database.
   - The new reminder immediately appears in the list as a card.

4. **Scheduling Notifications**
   - After inserting a new reminder, `MainActivity` calls `setReminder(time, title)`.
   - `setReminder`:
     - Parses the selected time into hour and minute.
     - Builds a `Calendar` instance for the main time.
     - Computes a "trigger time" 10 minutes earlier.
     - If the 10-minutes-before time has already passed:
       - If the main time is still in the future, the alarm is moved to the main time.
       - If even the main time is in the past, the reminder is scheduled for the same time on the next day.
     - Uses `AlarmManager` with a `PendingIntent` targeting `ReminderReceiver` to schedule the alarm.

5. **Notification**
   - When the alarm fires, Android calls `ReminderReceiver.onReceive`.
   - The receiver:
     - Creates a notification channel if necessary (Android O and above).
     - Builds a notification with:
       - Title "Reminder"
       - Text set to the reminder title
       - High priority so it is visible to the user
     - Shows the notification using `NotificationManager`.

### Handling Android 13+ Notifications

Android 13 introduced the `POST_NOTIFICATIONS` runtime permission.  
This project:

- Declares the permission in `AndroidManifest.xml`:

  - `android.permission.POST_NOTIFICATIONS`

- Requests it at runtime in `MainActivity` using `ActivityCompat.requestPermissions`.
- Only schedules and shows notifications after the user has granted permission.

### UI Design Notes

The UI is inspired by a modern Figma design:

- Soft background color for the main screen.
- Clear visual hierarchy:
  - Date and summary at the top
  - "Today" section label
  - Reminder cards as white rounded rectangles with slight elevation
- Each reminder card includes:
  - A small circular icon on the left
  - Title text
  - Time and date in a subtle row beneath the title
  - A pill badge on the right with "10 min before"
- A centered floating action button in a primary accent color for adding reminders.
- A rounded white bottom navigation bar with "Home" and "Calendar" labels (navigation itself is minimal in the current version but visually matches the design).

### How to Run the Project

1. **Requirements**
   - Android Studio (latest stable version)
   - Java 11 configured for the project
   - Android device or emulator with API 24 or higher

2. **Steps**
   - Open Android Studio.
   - Choose "Open an existing project".
   - Select the `DontForget` folder.
   - Let Gradle sync.
   - Connect a real device (recommended) or start an emulator.
   - Click "Run" to install and start the app.

3. **Testing Reminders**
   - Add a reminder with a time a little in the future.
   - Check that:
     - It appears in the list on the home screen.
     - A notification appears 10 minutes before (or at the set time if the 10-minute point was already in the past).

### Future Improvements and Ideas

This project is intentionally kept focused and simple, but there are several natural extensions:

- Calendar screen that lets you browse reminders by date.
- Natural language quick add (for example, "Call mom tomorrow 7pm").
- Recurring reminders (daily, weekly, custom).
- Categories or tags for reminders (work, personal, health, etc.).
- Voice input for reminders to make it easier for elderly users.
- Cloud backup or sync across devices.

### Why This Project is Useful

This app is not just a demo. It is a practical foundation for:

- A personal productivity tool you can actually use every day.
- A polished mobile app project for a portfolio or hackathon.
- A base to learn about:
  - Android activities and navigation
  - RecyclerView and custom item layouts
  - Room database
  - Alarms and notifications
  - Handling Android version differences (such as notification permissions)

The codebase is intentionally straightforward and readable so that it can be extended and improved as your Android and Java skills grow.

