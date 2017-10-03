CREATE TABLE CalendarDescription (
  name VARCHAR PRIMARY KEY NOT NULL,
  _from DATE NOT NULL,
  _to DATE NOT NULL,
  labels JSON NOT NULL
);

CREATE TABLE Resources (
  id VARCHAR NOT NULL,
  name VARCHAR NOT NULL,
  calendarName VARCHAR REFERENCES CalendarDescription (name) NOT NULL,
  numberOfPatients INTEGER NOT NULL,
  PRIMARY KEY (id, calendarName)
);

CREATE TABLE Tasks (
  id VARCHAR NOT NULL,
  day VARCHAR NOT NULL,
  calendarName VARCHAR REFERENCES CalendarDescription (name) NOT NULL,
  label VARCHAR NOT NULL,
  start INTEGER NOT NULL,
  finish INTEGER NOT NULL,
  tags VARCHAR ARRAY NOT NULL,
  PRIMARY KEY (id, calendarName)
);


CREATE TABLE Schedules (
  name VARCHAR NOT NULL,
  calendarName VARCHAR REFERENCES CalendarDescription (name) NOT NULL,
  constraints JSON NOT NULL,
  PRIMARY KEY (name, calendarName)
);

CREATE TABLE Assignments (
  taskId VARCHAR NOT NULL,
  resourceId VARCHAR NOT NULL,
  calendarName VARCHAR REFERENCES CalendarDescription (name) NOT NULL,
  scheduleName VARCHAR NOT NULL,
  PRIMARY KEY (taskId, resourceId, calendarName, scheduleName),
  FOREIGN KEY (taskId, calendarName) REFERENCES Tasks (id, calendarName),
  FOREIGN KEY (resourceId, calendarName) REFERENCES Resources (id, calendarName),
  FOREIGN KEY (scheduleName, calendarName) REFERENCES Schedules (name, calendarName)
);

CREATE TABLE Counters (
  id VARCHAR NOT NULL,
  name VARCHAR NOT NULL,
  include VARCHAR ARRAY NOT NULL,
  exclude VARCHAR ARRAY NOT NULL,
  groupName VARCHAR,
  calendarName VARCHAR REFERENCES CalendarDescription (name) NOT NULL,
  PRIMARY KEY (id, calendarName)
);
