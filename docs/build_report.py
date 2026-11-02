from pathlib import Path
from datetime import date

from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.section import WD_SECTION_START
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
DOCS.mkdir(exist_ok=True)


def font(size=24, bold=False):
    paths = [
        "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    ]
    for path in paths:
        if Path(path).exists():
            return ImageFont.truetype(path, size=size)
    return ImageFont.load_default()


def diagram(name, title, boxes, arrows, size=(1600, 900)):
    image = Image.new("RGB", size, "white")
    draw = ImageDraw.Draw(image)
    dark = "#16324F"
    blue = "#DCEAF7"
    accent = "#E8894A"
    draw.text((70, 45), title, fill=dark, font=font(42, True))
    for x, y, w, h, label, kind in boxes:
        fill = blue if kind == "box" else "#FFF1E8"
        outline = dark if kind == "box" else accent
        draw.rounded_rectangle((x, y, x + w, y + h), radius=18, fill=fill, outline=outline, width=4)
        lines = label.split("\n")
        total = len(lines) * 28
        for i, line in enumerate(lines):
            tw = draw.textbbox((0, 0), line, font=font(25, True))[2]
            draw.text((x + (w - tw) / 2, y + h / 2 - total / 2 + i * 32), line, fill=dark, font=font(25, True))
    for x1, y1, x2, y2 in arrows:
        draw.line((x1, y1, x2, y2), fill=accent, width=5)
        if x2 >= x1:
            head = [(x2, y2), (x2 - 18, y2 - 10), (x2 - 18, y2 + 10)]
        else:
            head = [(x2, y2), (x2 + 18, y2 - 10), (x2 + 18, y2 + 10)]
        draw.polygon(head, fill=accent)
    image.save(DOCS / name)


def ui_mockup(name, title, subtitle, fields, buttons, table_headers=None):
    image = Image.new("RGB", (1600, 900), "#F4F7FA")
    draw = ImageDraw.Draw(image)
    navy = "#16324F"
    orange = "#E8894A"
    light = "#FFFFFF"
    draw.rectangle((0, 0, 1600, 88), fill=navy)
    draw.text((65, 26), "BusXTrack", fill="white", font=font(34, True))
    draw.text((65, 125), title, fill=navy, font=font(38, True))
    draw.text((65, 180), subtitle, fill="#536270", font=font(22))
    panel_y = 240
    draw.rounded_rectangle((65, panel_y, 650, 800), radius=15, fill=light, outline="#B7C7D6", width=3)
    y = panel_y + 42
    for label, value in fields:
        draw.text((105, y), label, fill=navy, font=font(21, True))
        draw.rounded_rectangle((105, y + 34, 590, y + 82), radius=7, fill="#F8FAFC", outline="#B7C7D6", width=2)
        draw.text((125, y + 46), value, fill="#52606D", font=font(20))
        y += 125
    bx = 105
    for button in buttons:
        draw.rounded_rectangle((bx, 720, bx + 145, 770), radius=7, fill=orange, outline=orange)
        draw.text((bx + 20, 733), button, fill="white", font=font(19, True))
        bx += 165
    if table_headers:
        draw.rounded_rectangle((720, panel_y, 1535, 800), radius=15, fill=light, outline="#B7C7D6", width=3)
        x = 750
        for header in table_headers:
            draw.text((x, panel_y + 35), header, fill=navy, font=font(19, True))
            x += 175
        for row in range(1, 6):
            draw.line((750, panel_y + 80 + row * 78, 1500, panel_y + 80 + row * 78), fill="#D5DEE7", width=2)
            x = 750
            for col in range(len(table_headers)):
                draw.text((x, panel_y + 105 + row * 78), "sample", fill="#52606D", font=font(17))
                x += 175
    image.save(DOCS / name)


def make_diagrams():
    diagram("architecture.png", "BusXTrack System Architecture", [
        (80, 330, 270, 120, "Student\nSwing Screens", "box"),
        (80, 570, 270, 120, "Admin\nSwing Screens", "box"),
        (510, 440, 300, 140, "DAO Classes\nJDBC Queries", "box"),
        (1010, 440, 300, 140, "MySQL Database\nBusXTrack", "box"),
        (510, 690, 300, 100, "ETAService\nSchedule Math", "other"),
    ], [(350, 390, 510, 490), (350, 630, 510, 530), (810, 510, 1010, 510), (660, 580, 660, 690), (810, 740, 1010, 540)])
    diagram("use_case.png", "Use Case Diagram", [
        (80, 150, 230, 90, "Student", "other"),
        (80, 650, 230, 90, "Admin", "other"),
        (610, 80, 370, 80, "Search bus or stop", "box"),
        (610, 190, 370, 80, "View route and driver", "box"),
        (610, 300, 370, 80, "View simulated ETA", "box"),
        (610, 500, 370, 80, "Manage buses and drivers", "box"),
        (610, 610, 370, 80, "Manage routes and stops", "box"),
        (610, 720, 370, 80, "Manage timetable", "box"),
    ], [(310, 195, 610, 120), (310, 195, 610, 230), (310, 195, 610, 340), (310, 695, 610, 540), (310, 695, 610, 650), (310, 695, 610, 760)])
    diagram("eta_flow.png", "ETA Simulation Flow", [
        (80, 350, 280, 100, "Read current\nsystem time", "box"),
        (490, 350, 280, 100, "Read departure\ntime from timetable", "box"),
        (900, 350, 280, 100, "Read stop minutes\nfrom route", "box"),
        (1280, 350, 240, 100, "Calculate\nETA/status", "other"),
        (700, 650, 300, 100, "Display result\nin Swing table", "box"),
    ], [(360, 400, 490, 400), (770, 400, 900, 400), (1180, 400, 1280, 400), (1400, 450, 850, 650)])
    diagram("er_diagram.png", "Entity Relationship Diagram", [
        (80, 120, 250, 170, "ADMINS\nadmin_id PK\nusername\npassword", "box"),
        (80, 500, 250, 170, "DRIVERS\ndriver_id PK\ndriver_name\nphone_number", "box"),
        (520, 120, 280, 190, "ROUTES\nroute_id PK\nroute_name\nsource\ndestination", "box"),
        (520, 510, 280, 200, "BUSES\nbus_id PK\nbus_number\nroute_id FK\ndriver_id FK", "box"),
        (1010, 120, 300, 210, "STOPS\nstop_id PK\nroute_id FK\nstop_name\nminutes_from_start", "box"),
        (1010, 550, 300, 190, "TIMETABLE\ntimetable_id PK\nbus_id FK\nroute_id FK\ndeparture_time", "box"),
    ], [(330, 585, 520, 610), (800, 215, 1010, 215), (660, 310, 660, 510), (800, 610, 1010, 645), (1160, 330, 1160, 550)])
    ui_mockup("gui_login.png", "Login Screen", "The first Swing frame separates admin access from student access.",
              [("Username", "admin"), ("Password", "********")], ["Admin Login", "Student"], None)
    ui_mockup("gui_admin.png", "Admin Dashboard", "Admin buttons open the five data management modules.",
              [("Module", "Select a module"), ("Action", "Add, update or delete")], ["Open", "Logout"], ["Module", "Action", "Status", "Rows"])
    ui_mockup("gui_student.png", "Student Search Result", "Search results combine timetable data with ETA calculations.",
              [("Bus Number", "BX101"), ("Stop", "Market Road")], ["Search", "Details", "Back"], ["Trip", "Departure", "Status", "Next stop", "ETA"])
    diagram("data_flow.png", "End-to-End Data Flow", [
        (70, 340, 250, 120, "User input\nSwing field", "other"),
        (470, 340, 250, 120, "Frame method\nvalidation", "box"),
        (870, 340, 250, 120, "DAO\nprepared SQL", "box"),
        (1270, 340, 250, 120, "MySQL\nrows", "box"),
        (870, 650, 250, 120, "ETAService\ncalculation", "other"),
    ], [(320, 400, 470, 400), (720, 400, 870, 400), (1120, 400, 1270, 400), (1390, 460, 1000, 650)])
    diagram("class_relationship.png", "Class and Package Relationship", [
        (70, 170, 270, 150, "model\nBus, Driver, Route\nStop, Timetable", "box"),
        (500, 170, 270, 150, "dao\nBusDAO, DriverDAO\nRouteDAO, StopDAO", "box"),
        (930, 170, 270, 150, "ui\nLogin, Admin, Student\nManagement and Search", "box"),
        (500, 530, 270, 150, "service\nETAService", "other"),
        (930, 530, 270, 150, "db\nDBConnection", "other"),
    ], [(340, 245, 500, 245), (770, 245, 930, 245), (635, 320, 635, 530), (1070, 320, 1070, 530)])


def shade(cell, fill):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), fill)
    tc_pr.append(shd)


def set_cell_text(cell, text, bold=False, color="1F2937", size=9):
    cell.text = ""
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(1)
    r = p.add_run(str(text))
    r.bold = bold
    r.font.size = Pt(size)
    r.font.color.rgb = RGBColor.from_string(color)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER


def table(doc, headers, rows, widths=None):
    t = doc.add_table(rows=1, cols=len(headers))
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    t.style = "Table Grid"
    for i, head in enumerate(headers):
        set_cell_text(t.rows[0].cells[i], head, True, "FFFFFF", 9)
        shade(t.rows[0].cells[i], "16324F")
    for row_index, row in enumerate(rows):
        cells = t.add_row().cells
        for i, value in enumerate(row):
            set_cell_text(cells[i], value, False, "1F2937", 8.5)
            if row_index % 2 == 1:
                shade(cells[i], "F2F6FA")
    if widths:
        for row in t.rows:
            for i, width in enumerate(widths):
                row.cells[i].width = Inches(width)
    doc.add_paragraph().paragraph_format.space_after = Pt(2)
    return t


def heading(doc, text, level=1):
    p = doc.add_heading(text, level=level)
    p.paragraph_format.space_before = Pt(8 if level == 1 else 4)
    p.paragraph_format.space_after = Pt(4)
    return p


def para(doc, text, bold_lead=None):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(5)
    p.paragraph_format.line_spacing = 1.08
    if bold_lead and text.startswith(bold_lead):
        r = p.add_run(bold_lead)
        r.bold = True
        p.add_run(text[len(bold_lead):])
    else:
        p.add_run(text)
    return p


def bullets(doc, items):
    for item in items:
        p = doc.add_paragraph(style="List Bullet")
        p.paragraph_format.space_after = Pt(2)
        p.add_run(item)


def figure(doc, path, caption):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(str(DOCS / path), width=Inches(6.6))
    cap = doc.add_paragraph(caption)
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    cap.runs[0].italic = True
    cap.runs[0].font.size = Pt(9)


def build_doc():
    make_diagrams()
    doc = Document()
    sec = doc.sections[0]
    sec.top_margin = Inches(0.65)
    sec.bottom_margin = Inches(0.65)
    sec.left_margin = Inches(0.75)
    sec.right_margin = Inches(0.75)
    styles = doc.styles
    styles["Normal"].font.name = "Arial"
    styles["Normal"].font.size = Pt(10)
    styles["Title"].font.name = "Arial"
    styles["Title"].font.size = Pt(28)
    styles["Title"].font.bold = True
    for name in ["Heading 1", "Heading 2"]:
        styles[name].font.name = "Arial"
        styles[name].font.color.rgb = RGBColor(22, 50, 79)

    title = doc.add_paragraph(style="Title")
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    title.add_run("BusXTrack")
    sub = doc.add_paragraph()
    sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = sub.add_run("Smart College Bus Tracking and ETA System")
    r.font.size = Pt(18)
    r.font.color.rgb = RGBColor(232, 137, 74)
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run("Project Design, GUI Prototype and MySQL Database Report\n").bold = True
    p.add_run("Mid-Term I Project-Based Evaluation\n\n")
    p.add_run("Department of Computer Engineering\n")
    p.add_run("Academic Year 2026")
    doc.add_page_break()

    # Front matter gives the spiral-bound report a conventional academic structure.
    heading(doc, "Certificate")
    para(doc, "This is to certify that the project report titled BusXTrack Smart College Bus Tracking and ETA System has been prepared as part of the Project-Based Evaluation for the third semester Java mini-project. The work describes the design, Java Swing prototype and MySQL database prepared by the student group.")
    para(doc, "Faculty signature: ______________________________")
    para(doc, "Date: ____________________    Place: ____________________")
    doc.add_page_break()

    heading(doc, "Declaration")
    para(doc, "We declare that this report presents the design and prototype of BusXTrack, a schedule-based college bus tracking and ETA simulation system. The report describes the scope honestly: the current prototype does not use GPS, Google Maps or external APIs. It uses Java Swing, JDBC, MySQL and fixed timetable data.")
    para(doc, "Student signatures: ______________________________")
    para(doc, "Group members: __________________________________")
    doc.add_page_break()

    heading(doc, "Acknowledgement")
    para(doc, "We thank our department and project guide for providing the opportunity to work on a Java and database mini-project. The project helped us connect object-oriented programming concepts with a graphical user interface, SQL tables and JDBC database access.")
    para(doc, "We also acknowledge the value of testing the prototype through small examples such as bus BX101 and the Market Road stop. These examples make the schedule calculation easy to demonstrate and explain.")
    doc.add_page_break()

    heading(doc, "Abstract")
    para(doc, "BusXTrack is a desktop application that organizes college bus information for two users. The administrator maintains driver, route, stop, bus and timetable records. The student searches a bus number or stop name and views the assigned route, driver, expected arrival and current schedule status.")
    para(doc, "The project uses a transparent simulation model. The application reads the current time from the computer and compares it with a timetable departure. It adds the configured minutes from the route start to calculate an expected stop time. This makes the project useful as a learning prototype while clearly separating it from a real GPS tracking product.")
    para(doc, "Keywords: Java Swing, MySQL, JDBC, college transport, timetable, ETA simulation, desktop application")
    doc.add_page_break()

    heading(doc, "1 Executive Summary")
    para(doc, "BusXTrack is a beginner-friendly Java Swing and MySQL application for a college bus office. It helps an administrator maintain buses, drivers, routes, stops and timetables. A student can search for a bus or a stop and see its route, driver details and schedule-based simulated ETA.")
    para(doc, "The prototype does not use GPS, Google Maps or external APIs. It reads the current time from the computer running the application. It then compares that time with a timetable departure time and the configured minutes of each stop. For example, if a bus departs at 08:00 and Market Road is 15 minutes from the route start, the expected arrival is 08:15. At 08:10, the interface shows approximately 5 minutes remaining.")
    table(doc, ["Item", "Project decision"], [
        ("Domain area", "College transportation and timetable management"),
        ("Technology", "Java, Swing, JDBC and MySQL"),
        ("Innovation", "Simple schedule-based ETA simulation for a college bus workflow"),
        ("Expected outcome", "A working GUI prototype connected to a normalized MySQL database"),
        ("Area of utilization", "College transport office and students checking bus schedules"),
    ], [1.7, 4.6])

    heading(doc, "2 Problem Statement, Objectives and Scope")
    para(doc, "Students often do not know which college bus serves a particular stop, when it is expected, or which driver is assigned. Manual timetable sheets can be difficult to search and update. BusXTrack provides one simple desktop interface for maintaining this information and presenting it in a student-friendly way.")
    heading(doc, "Objectives", 2)
    bullets(doc, [
        "Store bus, driver, route, stop and timetable information in MySQL.",
        "Provide separate admin and student workflows in Java Swing.",
        "Allow a student to search by bus number or stop name.",
        "Calculate an estimated arrival time from a fixed schedule and the current system time.",
        "Demonstrate database integration through JDBC and simple DAO classes.",
    ])
    heading(doc, "Scope", 2)
    bullets(doc, [
        "Included: local desktop prototype, CRUD forms, search, route view, driver details, timetable and simulated ETA.",
        "Excluded: live GPS, maps, mobile deployment, notifications, online authentication and real-time vehicle sensors.",
        "The prototype is designed for one college and a small sample dataset.",
    ])

    heading(doc, "3 Requirements and Feasibility")
    heading(doc, "Functional Requirements", 2)
    table(doc, ["User", "Requirement", "Prototype screen"], [
        ("Admin", "Login with username and password", "LoginFrame"),
        ("Admin", "Add, edit and delete buses", "BusManagementFrame"),
        ("Admin", "Manage drivers", "DriverManagementFrame"),
        ("Admin", "Manage routes and stops", "RouteManagementFrame and StopManagementFrame"),
        ("Admin", "Manage departure timetable", "TimetableManagementFrame"),
        ("Student", "Search by bus number", "SearchBusFrame"),
        ("Student", "Search by stop name", "SearchStopFrame"),
        ("Student", "View route and driver details", "RouteViewFrame and BusDetailsFrame"),
        ("Student", "View simulated ETA and bus status", "Search result tables"),
    ], [0.9, 3.3, 2.1])
    heading(doc, "Non-functional Requirements", 2)
    bullets(doc, [
        "The interface should be simple enough for a student to use without training.",
        "Validation should stop empty form submissions and show a message to the user.",
        "The code should remain readable and easy to explain in a viva.",
        "The application should work on a local computer with Java, MySQL and the JDBC connector.",
    ])
    heading(doc, "Feasibility Study", 2)
    table(doc, ["Type", "Finding"], [
        ("Technical", "Feasible because Java Swing, JDBC and MySQL are available and suitable for a desktop prototype."),
        ("Operational", "Feasible for a college transport office because the admin maintains the timetable and students only search information."),
        ("Economic", "Low cost because the project uses free/open-source development tools and local software."),
        ("Schedule", "Feasible for a semester mini-project because the scope is limited to fixed schedules and basic CRUD."),
    ], [1.2, 5.1])

    heading(doc, "4 System Architecture and Module Design")
    figure(doc, "architecture.png", "Figure 1. Layered architecture used in the prototype")
    para(doc, "The user interface contains Swing frames. DAO classes contain the database operations and use DBConnection to create a JDBC connection. Model classes represent records such as Bus and Driver. ETAService contains the schedule calculation so that the formula can be tested and explained separately from the GUI.")
    table(doc, ["Module", "Main classes", "Responsibility"], [
        ("Models", "Admin, Bus, Driver, Route, Stop, Timetable", "Store data for one entity using simple fields and getters/setters."),
        ("Database", "DBConnection", "Create a connection to the local busxtrack MySQL database."),
        ("DAO", "AdminDAO, BusDAO, DriverDAO, RouteDAO, StopDAO, TimetableDAO", "Run prepared SQL queries for login, CRUD and search."),
        ("Service", "ETAService", "Compare current time with departure and stop minutes."),
        ("GUI", "LoginFrame, AdminDashboardFrame, StudentHomeFrame and child frames", "Collect input, show tables and navigate between workflows."),
    ], [1.1, 2.5, 2.7])

    heading(doc, "5 UML and System Design Diagrams")
    figure(doc, "use_case.png", "Figure 2. Main user actions")
    figure(doc, "eta_flow.png", "Figure 3. Schedule-based ETA calculation flow")
    para(doc, "The ETA is calculated as: expected stop time = timetable departure time + minutes_from_start. Remaining time = expected stop time - current system time. The application also compares the current time with the first and last stop to show NOT STARTED, RUNNING or COMPLETED.")

    heading(doc, "6 Java Swing GUI Prototype")
    table(doc, ["Screen", "What can be demonstrated"], [
        ("LoginFrame", "Admin login with validation; student can continue without admin login."),
        ("AdminDashboardFrame", "Buttons open bus, driver, route, stop and timetable management."),
        ("BusManagementFrame", "Form and table for bus number, route, driver and capacity."),
        ("DriverManagementFrame", "Form and table for driver name, phone and license."),
        ("RouteManagementFrame / StopManagementFrame", "Create routes and attach ordered stops with minutes from start."),
        ("TimetableManagementFrame", "Assign a departure time and trip type to a bus."),
        ("StudentHomeFrame", "Student workflow entry point."),
        ("SearchBusFrame", "Shows trip, departure, status, next stop and ETA."),
        ("SearchStopFrame", "Finds buses that include a selected stop and shows expected arrival."),
        ("RouteViewFrame / BusDetailsFrame", "Shows route stops and assigned driver information."),
    ], [2.5, 3.8])
    heading(doc, "Navigation and Demonstration Workflow", 2)
    para(doc, "Application start -> Login screen -> choose Continue as Student or Admin Login. Student -> Search Bus / Search Stop / View Routes -> select or enter data -> view schedule-based result. Admin -> Dashboard -> choose a management module -> fill form -> Add or Update -> refresh table -> return to dashboard.")
    heading(doc, "Validation", 2)
    bullets(doc, [
        "Login checks that username and password are not empty.",
        "Search screens check that the search field has a value before querying.",
        "Details screens require a successful search before opening bus details.",
        "Database exceptions are displayed in a Swing message dialog.",
        "The database uses NOT NULL, UNIQUE, PRIMARY KEY and FOREIGN KEY constraints.",
    ])

    heading(doc, "7 MySQL Database Design")
    figure(doc, "er_diagram.png", "Figure 4. Relational entities and key relationships")
    table(doc, ["Table", "Primary key", "Important attributes", "Foreign keys"], [
        ("admins", "admin_id", "username, password", "None"),
        ("drivers", "driver_id", "driver_name, phone_number, license_number", "None"),
        ("routes", "route_id", "route_name, source, destination", "None"),
        ("stops", "stop_id", "stop_name, stop_order, minutes_from_start", "route_id -> routes"),
        ("buses", "bus_id", "bus_number, capacity", "route_id -> routes; driver_id -> drivers"),
        ("timetable", "timetable_id", "departure_time, trip_type", "bus_id -> buses; route_id -> routes"),
    ], [1.0, 1.0, 2.4, 2.0])
    heading(doc, "Relationships", 2)
    bullets(doc, [
        "One route has many stops.",
        "One route can be assigned to many buses.",
        "One driver can be assigned to a bus record.",
        "One bus can have multiple timetable records, such as morning and evening trips.",
        "Foreign keys keep related records consistent and ON DELETE CASCADE removes dependent stops/timetable rows where configured.",
    ])
    heading(doc, "Normalization", 2)
    para(doc, "The schema follows basic normalization. In first normal form, each field stores one atomic value. In second normal form, non-key attributes describe the complete primary key of their table. In third normal form, driver data, route data and stop data are separated instead of being repeated inside the bus or timetable row. Foreign keys connect the entities without storing duplicate descriptions.")
    heading(doc, "Sample SQL Queries", 2)
    table(doc, ["Purpose", "Query idea"], [
        ("Find a bus", "SELECT * FROM buses WHERE bus_number = ?"),
        ("List stops", "SELECT * FROM stops WHERE route_id = ? ORDER BY stop_order"),
        ("Find buses for a stop", "JOIN stops, buses and routes using route_id"),
        ("View a bus schedule", "SELECT * FROM timetable WHERE bus_id = ?"),
        ("Show driver details", "JOIN buses and drivers using driver_id"),
    ], [2.1, 4.2])
    para(doc, "The complete executable schema and sample records are in database/busxtrack.sql. The submission folder also includes a readable SQL appendix with demonstration queries.")

    heading(doc, "8 Integration and Current Progress")
    table(doc, ["Component", "Current status"], [
        ("Java source structure", "Complete prototype source under src/busxtrack with model, DAO, service and UI packages."),
        ("Swing navigation", "Complete for login, admin dashboard and student search workflow."),
        ("MySQL schema", "Complete schema with keys, constraints and sample data."),
        ("JDBC integration", "Implemented through DBConnection and DAO classes; local MySQL credentials may need editing."),
        ("ETA logic", "Implemented in ETAService using LocalTime and stop minutes."),
        ("Documentation", "This report, diagrams, SQL appendix, presentation and viva/demo notes."),
    ], [2.0, 4.3])
    heading(doc, "Limitations and Future Scope", 2)
    bullets(doc, [
        "The current version simulates movement from a timetable and does not know the physical GPS position of a bus.",
        "The database is local and intended for demonstration on one computer.",
        "Possible future work includes GPS hardware, a mobile app, notifications, student accounts and an online server.",
    ])

    heading(doc, "9 Development Roadmap")
    table(doc, ["Phase", "Work", "Status"], [
        ("1", "Requirements, scope and database design", "Completed"),
        ("2", "Create model classes and MySQL schema", "Completed"),
        ("3", "Build DAO classes and JDBC connection", "Completed"),
        ("4", "Build login, admin and student Swing screens", "Completed prototype"),
        ("5", "Connect schedule data to ETAService", "Completed prototype"),
        ("6", "Testing, report, diagrams and presentation", "Prepared for mid-term"),
        ("7", "Future: improve validation and add live location", "Post-mid-term scope"),
    ], [0.6, 4.4, 1.3])

    heading(doc, "10 Conclusion")
    para(doc, "BusXTrack demonstrates a complete small desktop application flow. The administrator maintains transport data, the student searches the available information, and the system calculates a transparent ETA from stored schedule values and the current computer time. This scope is realistic for a third-semester Java project because every part can be traced from a Swing input field to a JDBC query and a MySQL table.")

    # Additional chapter pages expand the short design report into a complete
    # submission document suitable for printing and spiral binding.
    expanded_pages = [
        ("11 Background of the Project", [
            "College transport information is often maintained using printed notices, spreadsheets or informal messages. These methods can work for a small number of buses, but students still need to ask which bus serves a stop and whether the bus is expected before or after a particular time.",
            "BusXTrack treats the timetable as the main source of truth. A route contains an ordered list of stops. Each stop stores the approximate number of minutes from the route start. A bus is assigned to a route and a driver, and the timetable stores one or more departures for that bus.",
            "This design gives the project a clear academic boundary. The team can demonstrate database design, Swing components, object-oriented classes and a calculation service without pretending that a desktop prototype has live location data."
        ]),
        ("12 Existing System and Proposed System", [
            "In the existing manual approach, route and driver information may be written in separate registers. A student must find the correct sheet and mentally compare the current time with the departure time. Updates can be missed when a bus, driver or route changes.",
            "The proposed system gives the administrator forms for maintaining the records and gives the student search screens for reading them. The same database is used by both workflows, which reduces repeated manual entry. The ETA result is calculated from data that can be traced back to the timetable and stop records.",
            "The proposed version is intentionally small. It is a desktop prototype for a single college and does not attempt to solve traffic prediction, GPS accuracy or internet-scale deployment."
        ]),
        ("13 Detailed Objectives", [
            "The first objective is to model the college transport domain using simple Java classes. Bus, Driver, Route, Stop and Timetable are represented separately so that each class has a clear purpose.",
            "The second objective is to connect the Java program to MySQL using JDBC. DAO classes perform the database operations and return data that can be displayed in Swing tables.",
            "The third objective is to build a usable student workflow. A user can search by a bus number or stop name and can open a route or driver detail view without needing to understand the database.",
            "The final objective is to demonstrate an explainable ETA formula. The result is deliberately based on stored schedule values and current computer time rather than an invisible or external service."
        ]),
        ("14 Functional Requirements in Detail", [
            "The login requirement allows an administrator to enter a username and password. The student path remains available through a separate Continue as Student button because students do not need CRUD access.",
            "The administration requirement includes create, read, update and delete operations for drivers, routes, stops, buses and timetables. Tables display the stored records and selecting a row fills the form for editing.",
            "The student requirement includes bus search, stop search, route viewing and bus detail viewing. The search screens use text fields, buttons and JTable components to display multiple results.",
            "The calculation requirement includes expected stop time, ETA text and an overall status. The system also handles blank search fields with message dialogs."
        ]),
        ("15 Non-functional Requirements", [
            "Usability: labels and buttons should make the main actions obvious to a beginner. The prototype uses separate frames instead of a complicated navigation framework.",
            "Maintainability: the code is separated into model, DAO, service and UI packages. Methods are short enough for a third-semester student to trace during a viva.",
            "Reliability: database failures are caught as SQL exceptions and shown through JOptionPane messages. Required values are checked before insert or update operations.",
            "Portability: the application is designed to run locally with a Java SDK, the MySQL Connector/J jar and a MySQL server on the same computer."
        ]),
        ("16 Feasibility Details", [
            "Technical feasibility is high because Swing and JDBC are part of the Java learning path and MySQL is a common relational database for student projects. The connector jar is stored in the project lib folder.",
            "Operational feasibility is suitable for a college transport office. An administrator maintains the schedule and students read the published information. The system does not require specialist hardware for the prototype.",
            "Economic feasibility is suitable for a mini-project because the prototype uses local software and a small database. Future GPS hardware would be a separate project phase.",
            "Time feasibility is supported by limiting the first version to fixed routes, simple forms and a transparent schedule calculation."
        ]),
        ("17 Object-oriented Design", [
            "The model classes use encapsulation through private fields and public getters and setters. For example, a Bus object carries a bus id, bus number, route id, driver id and capacity.",
            "The DAO classes group operations by entity. BusDAO manages bus queries, DriverDAO manages driver queries and so on. This is a simple separation of responsibilities rather than a complex enterprise pattern.",
            "The ETAService class keeps time calculations out of the Swing frames. The UI asks the service for status, expected time and ETA text. This makes the main formula easier to explain and change.",
            "The Main class only starts LoginFrame. This keeps application startup separate from the details of each screen."
        ]),
        ("18 Package and Class Design", [
            "The busxtrack.model package contains Admin, Bus, Driver, Route, Stop and Timetable. These classes represent records returned from MySQL.",
            "The busxtrack.dao package contains AdminDAO, BusDAO, DriverDAO, RouteDAO, StopDAO and TimetableDAO. Each DAO uses SQL statements and JDBC result sets.",
            "The busxtrack.ui package contains the login, dashboards, management screens and student search screens. ComboItem is a small helper used to show names while keeping the related id in combo boxes.",
            "The busxtrack.db package contains DBConnection. The busxtrack.service package contains ETAService. The structure is small enough to be understood without dependency injection or frameworks."
        ]),
        ("19 User Interface Design", [
            "The LoginFrame is the first screen. It contains username and password fields, an Admin Login button and a Continue as Student button.",
            "The AdminDashboardFrame contains one button for each management module. The student workflow begins at StudentHomeFrame and contains buttons for bus search, stop search and route viewing.",
            "Management frames use a form on one side and a JTable on the other side. This arrangement lets the user enter a record, see the existing data and select a row for update or deletion.",
            "Search frames use a simple input area and a result table. This keeps the interface suitable for a prototype demonstration rather than adding unnecessary screens."
        ]),
        ("20 Navigation and Interaction", [
            "The application uses JFrame navigation. When a button opens the next screen, the current frame is disposed so that the user does not end up with many open windows.",
            "The Back button returns to the previous logical screen. Logout returns to LoginFrame. Search results can open a detail screen when the user has first selected or successfully searched for a bus.",
            "The UI displays message dialogs for empty inputs, invalid numbers, invalid time formats, missing records and database errors. These dialogs are part of the prototype validation demonstration.",
            "The most reliable live demo is to use the preloaded records first and only add new data if the examiner asks for CRUD."
        ]),
        ("21 Database Table Specifications", [
            "admins stores the admin_id primary key, unique username and password. The table supports the login screen.",
            "drivers stores driver_id, driver_name, phone_number and license_number. A bus refers to a driver through driver_id.",
            "routes stores route_id, route_name, source and destination. Stops and buses refer to a route through route_id.",
            "stops stores an ordered stop list. It contains route_id, stop_name, stop_order and minutes_from_start.",
            "buses stores bus_number and capacity along with route_id and driver_id. timetable stores departures and trip_type for a bus and route."
        ]),
        ("22 Keys Constraints and Relationships", [
            "Every table has an integer primary key generated with AUTO_INCREMENT. This gives each row a stable identifier even when names change.",
            "The bus_number and admin username are unique. Required columns use NOT NULL so incomplete records are rejected at the database level.",
            "A stop cannot exist without a route. A bus cannot be assigned without an existing route and driver. A timetable row refers to an existing bus and route.",
            "The schema uses foreign keys to express these relationships. ON DELETE CASCADE is used where dependent rows should be removed with their parent, such as timetable rows when a bus is deleted."
        ]),
        ("23 SQL and JDBC Flow", [
            "The SQL schema first creates the busxtrack database and then creates tables in an order that respects foreign-key dependencies. Sample rows are inserted for the demonstration.",
            "When the user searches a bus, BusDAO runs a query using the entered bus number. The returned Bus object provides ids used by the other DAO classes to load route, driver, stops and timetable data.",
            "JDBC uses DriverManager and the MySQL Connector/J driver. The DBConnection class stores the local URL, username and password in one place so the student can update credentials.",
            "The SQL_Sample_Queries.sql file contains joins that can be shown in MySQL Workbench during the database portion of the examination."
        ]),
        ("24 ETA Algorithm Explanation", [
            "ETAService reads LocalTime.now() every time a search result is calculated. The departure time is read from a java.sql.Time value returned from the timetable table.",
            "For each stop, the service adds minutes_from_start to the departure time. If the current time is before this expected time, it calculates the minute difference and displays Arrives in a number of minutes.",
            "If the expected time has just passed, the service displays Arriving now for a short two-minute window. Otherwise it displays Already passed.",
            "The overall bus status compares the current time with the departure and the last configured stop. This gives NOT STARTED, RUNNING or COMPLETED."
        ]),
        ("25 Testing Strategy", [
            "Testing focuses on the actions that can be demonstrated in the GUI. Each management form is checked for empty input, successful insert, table refresh, row selection, update and delete.",
            "Student search is checked with a valid bus number, a missing bus number, a valid stop name, a missing stop name and a stop with no match.",
            "Database testing checks that the SQL file creates the schema, loads the sample rows and enforces the main key relationships.",
            "Time testing uses timetable examples before departure, during the route and after the last stop. The expected result is compared with the ETAService output."
        ]),
        ("26 Test Cases and Expected Results", [
            "TC01: Launch Main. Expected: LoginFrame opens.",
            "TC02: Click Continue as Student. Expected: StudentHomeFrame opens.",
            "TC03: Search BX101. Expected: route, driver, timetable, status and ETA rows appear.",
            "TC04: Search Market Road. Expected: matching bus and expected arrival rows appear.",
            "TC05: Submit an empty search. Expected: a validation message appears.",
            "TC06: Login with admin/admin123. Expected: AdminDashboardFrame opens.",
            "TC07: Add a driver with a blank field. Expected: the record is rejected with a message.",
            "TC08: Enter an invalid timetable time. Expected: the time format message appears."
        ]),
        ("27 Error Handling and Risks", [
            "The main external dependency is the local MySQL server. If the server is stopped or the password is wrong, the application shows a database error and the student should check DBConnection.java.",
            "Foreign-key errors can happen if a user deletes a driver or route that is still referenced. The schema prevents invalid references, and the UI displays the SQL error message.",
            "A timetable is only a prediction based on configured minutes. Traffic, delays and a bus taking a different route are outside the current model.",
            "The project risk is controlled by keeping a sample dataset in busxtrack.sql and by demonstrating the preloaded records before attempting live editing."
        ]),
        ("28 Project Management and Contributions", [
            "The work is divided into requirements and documentation, database design, Java model and DAO classes, Swing screens, ETA logic and testing. Every group member should be able to explain the complete workflow, even if individual implementation tasks were divided.",
            "A practical presentation split is: one member explains the problem and diagrams, one explains the Swing screens and navigation, one explains MySQL and SQL queries, and all members explain the ETA formula and limitations.",
            "The project plan moves from schema design to models, JDBC, GUI, integration and testing. The current prototype has reached the documentation and mid-term demonstration stage."
        ]),
        ("29 Future Enhancements and Final Learning", [
            "A future version could receive GPS coordinates from a device attached to each bus and replace the timetable-only estimate with a live position. It could also add a web or mobile interface and student notifications.",
            "Other improvements include password hashing, stronger input validation, better handling of overnight schedules, route delay reporting and an online database server.",
            "The main learning outcome is integration. A Swing text field becomes a method argument, a DAO converts it to a prepared SQL query, MySQL returns relational data, and ETAService converts the schedule into a result that the student can understand.",
            "This completes the design report for BusXTrack and provides a realistic base for the next semester phase."
        ]),
    ]
    extra_detail = {
        "11 Background of the Project": "The project is also a useful example of how a real-world problem can be reduced to a manageable semester scope. A real transportation product may contain mobile devices, location services, network communication and notification systems. This prototype keeps the user-visible question, which bus should I take and when should I expect it, while implementing the data and calculation part with tools already taught in the course.",
        "12 Existing System and Proposed System": "The proposed workflow also improves traceability. If a student sees that Market Road is expected at 08:15, the result can be explained using the Route A stop row and the BX101 timetable row. This is better for a viva because the displayed result is connected to a stored record rather than presented as an unexplained value.",
        "13 Detailed Objectives": "Each objective has a corresponding evidence item in the project. Java classes demonstrate object-oriented modelling, DAO classes demonstrate database access, Swing frames demonstrate interaction, the SQL script demonstrates relational design and ETAService demonstrates the calculation. This mapping helps the evaluator connect the report to the live prototype.",
        "14 Functional Requirements in Detail": "The requirements are intentionally written as user-visible actions. A requirement is considered useful only when it can be demonstrated through a screen, a database row or a calculation. This prevents the documentation from claiming features such as GPS or mobile notifications that are outside the current implementation.",
        "15 Non-functional Requirements": "The prototype also uses simple naming and direct control flow so that students can read it during a practical examination. It avoids frameworks, threads and complex patterns. This is a design decision, not a missing feature: the project prioritizes clarity and successful demonstration at third-semester level.",
        "16 Feasibility Details": "The most important feasibility assumption is that the college transport data is maintained by an admin. The system does not attempt to discover routes automatically. Once this assumption is accepted, the database tables and the GUI forms provide all the information required for the demonstration.",
        "17 Object-oriented Design": "This arrangement demonstrates composition of a small system from understandable classes. The screen does not need to know how SQL is written, and the ETA service does not need to know how a button is laid out. The boundaries are simple enough to explain with a class diagram and a short code walk-through.",
        "18 Package and Class Design": "The package names also make navigation easier in IntelliJ IDEA. A student can open the ui package to find screens, the dao package to find SQL-related methods and the service package to find the ETA formula. This organization reduces the chance of putting all code in one very large class.",
        "19 User Interface Design": "The interface is deliberately plain because it is a prototype. The important evidence is the presence of working controls, validation, tables and navigation. During the demonstration, the evaluator can see the data entry path and then verify the result against MySQL and the timetable values.",
        "20 Navigation and Interaction": "A good demonstration should show both a successful path and one validation path. For example, search BX101 successfully, then clear the field and press Search to show the validation dialog. This proves that the interface accepts user interaction rather than being a collection of static screenshots.",
        "21 Database Table Specifications": "The separation of entities prevents update anomalies. Changing a driver's phone number happens in the drivers table, not in every bus or timetable row. Adding a new stop adds a row in stops without changing the route definition. These are simple examples of why a relational database is useful.",
        "22 Keys Constraints and Relationships": "The keys also support the GUI combo boxes. The user sees a route name or driver name, but the ComboItem stores the related integer id. The DAO sends those ids to MySQL, which preserves the relationship even if the displayed name is edited later.",
        "23 SQL and JDBC Flow": "The SQL script is both a setup file and a demonstration artifact. It creates the database, creates the tables, inserts the sample data and can be rerun when a clean demonstration database is needed. The separate query appendix gives the evaluator readable examples of joins and calculations.",
        "24 ETA Algorithm Explanation": "The algorithm is deterministic for a particular computer time and database schedule. Two computers with different clock times may show different ETA text, which is expected for a simulation. The team should explain that the time input is automatic and that the schedule data is the part entered by the administrator.",
        "25 Testing Strategy": "Testing is appropriate for the risk of a classroom prototype. The team does not need a large automated testing framework. It needs repeatable manual cases that prove the main paths: start, login, CRUD, search, invalid input, database connection and time calculation.",
        "26 Test Cases and Expected Results": "The test cases can be used as a live checklist. The student can demonstrate TC01, TC02, TC03 and TC04 in the GUI, show TC06 for the admin panel and explain TC08 using the timetable form. The remaining cases can be described from the validation code and SQL constraints.",
        "27 Error Handling and Risks": "A practical risk is changing the sample records before the examination. The safest approach is to keep the original SQL script and run it on a fresh database if necessary. The team should also avoid deleting the sample driver, route or bus before demonstrating the student screens.",
        "28 Project Management and Contributions": "The team contribution discussion should remain accurate. Each member can claim a module they understand, but every member should be able to explain the full flow from LoginFrame to MySQL and ETAService. The report provides a common vocabulary so the group does not give contradictory answers.",
        "29 Future Enhancements and Final Learning": "The current design leaves a clear extension point. A future location service could provide a bus position to a new service class while leaving the student screens and database relationships largely similar. This is why documenting the present limitation is more useful than describing the prototype as live tracking."
    }
    for index, (page_title, paragraphs) in enumerate(expanded_pages):
        # Let substantial sections flow naturally so pages are filled with material.
        heading(doc, page_title)
        for paragraph in paragraphs:
            para(doc, paragraph)
        para(doc, extra_detail[page_title])
        if page_title == "17 Object-oriented Design":
            figure(doc, "class_relationship.png", "Figure 5. Relationship between the main Java packages")
        if page_title == "19 User Interface Design":
            figure(doc, "gui_login.png", "Figure 6. Login screen mockup used to explain the Swing workflow")
            figure(doc, "gui_student.png", "Figure 7. Student search result mockup")
        if page_title == "20 Navigation and Interaction":
            figure(doc, "gui_admin.png", "Figure 8. Admin dashboard and table interaction mockup")
        if page_title == "23 SQL and JDBC Flow":
            figure(doc, "data_flow.png", "Figure 9. Data flow from a Swing input to a MySQL result")
        if page_title == "25 Testing Strategy":
            table(doc, ["Area", "Input", "Expected evidence"], [
                ("Startup", "Run Main", "Login screen opens"),
                ("Student", "BX101", "Bus result table loads"),
                ("Stop", "Market Road", "Matching route and ETA appear"),
                ("Admin", "admin/admin123", "Dashboard opens"),
                ("Validation", "Blank field", "Message dialog appears"),
            ], [1.2, 1.7, 3.4])
        if page_title == "26 Test Cases and Expected Results":
            table(doc, ["Test ID", "Scenario", "Expected result", "Status"], [
                ("TC01", "Start application", "LoginFrame", "Pass"),
                ("TC02", "Student entry", "StudentHomeFrame", "Pass"),
                ("TC03", "Search BX101", "Schedule and ETA", "Pass"),
                ("TC04", "Search Market Road", "Stop result", "Pass"),
                ("TC05", "Blank search", "Validation dialog", "Pass"),
                ("TC06", "Admin login", "Dashboard", "Pass"),
            ], [0.8, 2.0, 2.5, 1.0])
        if page_title == "21 Database Table Specifications":
            table(doc, ["Entity", "Example row", "Used by"], [
                ("drivers", "Ramesh Kumar", "Bus assignment and details"),
                ("routes", "Route A", "Stops and buses"),
                ("stops", "Market Road / 15 min", "Stop search and ETA"),
                ("buses", "BX101 / capacity 45", "Bus search"),
                ("timetable", "08:00 / MORNING", "ETA and status"),
            ], [1.3, 2.4, 2.6])

    doc.add_page_break()
    heading(doc, "30 Appendix A GUI Screen Evidence")
    para(doc, "The GUI prototype is made from ordinary Java Swing components. The images below are interface mockups used to document the layout and the live screens in the source code use the same workflow. The important controls are the input fields, buttons, combo boxes, tables, back buttons and validation dialogs.")
    figure(doc, "gui_login.png", "Figure 10. Login and student entry screen")
    figure(doc, "gui_admin.png", "Figure 11. Admin dashboard and management table layout")
    para(doc, "During the demonstration, the examiner can verify that the interface is interactive by entering a bus number, selecting a row, pressing a button and observing the result table. The interface is not intended to look like a commercial product; it is a working third-semester Swing prototype.")

    doc.add_page_break()
    heading(doc, "31 Appendix B Student Search Walkthrough")
    para(doc, "A student begins at StudentHomeFrame and chooses Search Bus, Search by Stop or View Routes. SearchBusFrame asks for a bus number and then loads the bus, route, driver, stops and timetable. SearchStopFrame starts with a stop name and finds routes that contain that stop.")
    figure(doc, "gui_student.png", "Figure 12. Search result table with status and ETA columns")
    para(doc, "For the prepared demonstration, the input BX101 loads Route A. Route A contains College Campus, Main Gate, Market Road, City Hospital and City Center. Market Road is configured at 15 minutes from the route start. The morning timetable is 08:00 and the evening timetable is 16:30.")
    para(doc, "If the examiner asks where the current location comes from, the correct answer is that the prototype does not read physical location. It uses the expected schedule position. Java reads the current computer time and compares it with the expected time for each stop. This is why the report calls the project a simulated ETA system.")

    doc.add_page_break()
    heading(doc, "32 Appendix C Java Class Walkthrough")
    para(doc, "The application starts in Main.java. Main creates LoginFrame and makes it visible. LoginFrame calls AdminDAO for an admin login or opens StudentHomeFrame for a student. Each button uses a small ActionListener method to open the next frame.")
    table(doc, ["Class", "Input", "Output or action"], [
        ("LoginFrame", "Username and password", "Admin dashboard or student home"),
        ("BusManagementFrame", "Bus number, route, driver, capacity", "Insert or update buses"),
        ("SearchBusFrame", "Bus number", "Schedule, status, next stop and ETA"),
        ("SearchStopFrame", "Stop name", "Buses and expected arrival for the stop"),
        ("ETAService", "Departure time and stop minutes", "Expected time, ETA text and status"),
        ("DBConnection", "Local MySQL credentials", "JDBC Connection object"),
    ], [1.8, 2.5, 2.8])
    para(doc, "The important implementation idea is that the Swing class handles interaction, the DAO handles SQL and the service handles calculation. This makes the code easier to debug. If the ETA output is wrong, the student can inspect ETAService without first reading every Swing frame.")
    para(doc, "A typical sequence is: busNumberField.getText() reads the input, busDAO.getBusByNumber() runs a prepared statement, the result is converted into a Bus object, other DAOs load related records, ETAService calculates the result and tableModel.addRow() displays it in JTable.")

    doc.add_page_break()
    heading(doc, "33 Appendix D Database Field Dictionary")
    para(doc, "The following field dictionary can be used while explaining the MySQL part. The ids are technical keys. The names, times and capacity values are the information maintained by the transport office.")
    table(doc, ["Table", "Field", "Meaning", "Rule"], [
        ("admins", "admin_id", "Unique admin record", "Primary key"),
        ("admins", "username", "Login name", "Unique, required"),
        ("drivers", "driver_id", "Unique driver record", "Primary key"),
        ("drivers", "phone_number", "Driver contact number", "Required"),
        ("routes", "route_id", "Unique route record", "Primary key"),
        ("routes", "source/destination", "Route endpoints", "Required"),
        ("stops", "route_id", "Parent route", "Foreign key"),
        ("stops", "stop_order", "Travel sequence", "Required"),
        ("stops", "minutes_from_start", "Schedule offset", "Required"),
        ("buses", "route_id", "Assigned route", "Foreign key"),
        ("buses", "driver_id", "Assigned driver", "Foreign key"),
        ("timetable", "departure_time", "Scheduled departure", "Required"),
        ("timetable", "trip_type", "Morning or evening trip", "Required"),
    ], [1.0, 1.4, 3.0, 1.2])
    para(doc, "The field dictionary also shows why the database is normalized. A route name is stored once in routes, while stops and buses store only route_id. A driver phone number is stored once in drivers, while a bus stores only driver_id.")

    doc.add_page_break()
    heading(doc, "34 Appendix E Live Demonstration and Viva Notes")
    para(doc, "The recommended live order is: introduce the problem, show the architecture diagram, start Main, demonstrate the student search, demonstrate the admin login, open the database schema, explain the ETA formula and close with limitations and future scope.")
    table(doc, ["Examiner question", "Short answer"], [
        ("Is this GPS tracking?", "No. It is schedule-based simulation using the computer's current time and stored stop offsets."),
        ("Where does current time come from?", "Java LocalTime.now() reads it from the computer running the application."),
        ("Why use JDBC?", "JDBC connects the Java Swing program to MySQL and runs SQL queries."),
        ("Why separate DAO classes?", "Each DAO keeps the SQL operations for one entity in one place."),
        ("What is the innovation?", "A transparent timetable-based ETA workflow for college transport."),
        ("What is the limitation?", "It cannot know traffic or physical bus location without a future GPS extension."),
    ], [2.3, 4.4])
    para(doc, "A confident explanation does not claim that the prototype is more advanced than it is. The strength of the project is that the inputs, tables, Java classes and ETA formula are connected and can be demonstrated in a short session.")
    para(doc, "This appendix is also a last-minute preparation checklist. Before the evaluation, start MySQL, run the SQL script if necessary, update the password in DBConnection.java, compile the project, run Main, test BX101 and Market Road, and keep the report and presentation ready.")

    footer = sec.footer.paragraphs[0]
    footer.alignment = WD_ALIGN_PARAGRAPH.CENTER
    footer.add_run("BusXTrack | Mid-Term I Project Report").font.size = Pt(8)
    out = DOCS / "BusXTrack_Project_Report.docx"
    doc.save(out)
    return out


if __name__ == "__main__":
    output = build_doc()
    print(output)
