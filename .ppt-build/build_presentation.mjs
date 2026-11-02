import fs from "node:fs/promises";
import path from "node:path";
import { pathToFileURL, fileURLToPath } from "node:url";
import { Presentation, PresentationFile } from "@oai/artifact-tool";

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const SKILL_DIR = "/Users/parthpareek/.codex/plugins/cache/openai-primary-runtime/presentations/26.904.11930/skills/presentations";
const TMP_DIR = path.join(ROOT, ".ppt-build");
const OUT_DIR = path.join(ROOT, "docs");
const RUNTIME_PYTHON = "/Users/parthpareek/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/bin/python3.12";
await fs.mkdir(TMP_DIR, { recursive: true });
await fs.mkdir(OUT_DIR, { recursive: true });

const { resolvePresentationFont } = await import(pathToFileURL(path.join(SKILL_DIR, "container_tools/artifact_tool_utils.mjs")).href);
const family = resolvePresentationFont({ preferred: ["Arial", "Aptos", "DejaVu Sans"] });
const presentation = Presentation.create({ slideSize: { width: 1280, height: 720 } });

const navy = "#16324F";
const orange = "#E8894A";
const pale = "#F2F6FA";
const blue = "#DCEAF7";
const gray = "#536270";

function textbox(slide, text, x, y, w, h, size = 24, color = navy, bold = false, align = "left") {
  const shape = slide.shapes.add({ geometry: "textbox", position: { left: x, top: y, width: w, height: h }, fill: "none", line: { fill: "none", width: 0 } });
  shape.text = text;
  shape.text.style = { typeface: family, fontSize: size, bold, color, autoFit: "shrinkTextOnOverflow" };
  shape.text.paragraphFormat = { alignment: align };
  return shape;
}

function box(slide, text, x, y, w, h, fill = blue, border = navy, size = 22) {
  const shape = slide.shapes.add({ geometry: "roundRect", position: { left: x, top: y, width: w, height: h }, fill, line: { fill: border, width: 2 } });
  shape.text = text;
  shape.text.style = { typeface: family, fontSize: size, bold: true, color: navy, autoFit: "shrinkTextOnOverflow" };
  shape.text.paragraphFormat = { alignment: "center" };
  return shape;
}

function line(slide, x1, y1, x2, y2, color = orange) {
  slide.shapes.add({ geometry: "line", position: { left: x1, top: y1, width: Math.max(1, x2 - x1), height: Math.max(1, y2 - y1) }, line: { fill: color, width: 3, beginArrowhead: "none", endArrowhead: "triangle" } });
}

function title(slide, t, kicker = "BUSXTRACK") {
  textbox(slide, kicker, 70, 32, 400, 26, 14, orange, true);
  textbox(slide, t, 70, 64, 1140, 58, 38, navy, true);
  slide.shapes.add({ geometry: "line", position: { left: 70, top: 132, width: 1140, height: 1 }, line: { fill: orange, width: 2 } });
}

function bulletList(slide, items, x, y, w, size = 25, gap = 48) {
  items.forEach((item, i) => {
    textbox(slide, "•", x, y + i * gap, 22, 34, size, orange, true);
    textbox(slide, item, x + 32, y + i * gap, w - 32, 40, size, navy, false);
  });
}

function footer(slide, n) {
  textbox(slide, `BusXTrack | Mid-Term I | ${n}`, 70, 682, 500, 20, 12, gray, false);
}

// 1 Cover
{
  const s = presentation.slides.add();
  s.background.fill = "#FFFFFF";
  textbox(s, "BusXTrack", 70, 170, 1140, 92, 64, navy, true, "center");
  textbox(s, "Smart College Bus Tracking and ETA System", 120, 275, 1040, 50, 31, orange, false, "center");
  textbox(s, "Project-Based Evaluation | Java Swing + MySQL", 120, 365, 1040, 32, 22, gray, false, "center");
  box(s, "Schedule-based ETA simulation", 390, 475, 500, 74, pale, navy, 23);
  textbox(s, "Mid-Term I Presentation", 120, 620, 1040, 28, 18, navy, false, "center");
  s.speakerNotes.textFrame.setText("BusXTrack is a local Java Swing and MySQL prototype. It does not use GPS or external APIs.");
}

// 2 Problem and objective
{
  const s = presentation.slides.add(); title(s, "The problem and the project objective");
  textbox(s, "Students need quick answers to simple transport questions:", 80, 175, 560, 42, 26, navy, true);
  bulletList(s, ["Which bus serves my stop?", "When is the next scheduled arrival?", "Which route and driver are assigned?"], 90, 245, 530, 25, 60);
  box(s, "BusXTrack", 770, 245, 250, 90, orange, orange, 28);
  line(s, 640, 290, 770, 290);
  textbox(s, "A small desktop system that keeps transport data in one place and shows a transparent timetable-based ETA.", 690, 390, 410, 110, 25, navy, false, "center");
  footer(s, 2);
}

// 3 Users and scope
{
  const s = presentation.slides.add(); title(s, "Two users, one shared database");
  box(s, "STUDENT", 100, 220, 230, 80, orange, orange, 25);
  box(s, "ADMIN", 100, 470, 230, 80, orange, orange, 25);
  box(s, "Swing GUI", 520, 340, 220, 80, blue, navy, 24);
  box(s, "MySQL\nbusxtrack", 940, 340, 220, 80, blue, navy, 24);
  line(s, 330, 260, 520, 375); line(s, 330, 510, 520, 385); line(s, 740, 380, 940, 380);
  textbox(s, "Search bus or stop\nView route, driver, ETA", 370, 190, 320, 92, 22, gray, false, "center");
  textbox(s, "Manage buses, drivers,\nroutes, stops and timetable", 360, 525, 350, 82, 22, gray, false, "center");
  footer(s, 3);
}

// 4 Architecture
{
  const s = presentation.slides.add(); title(s, "Simple architecture that matches the code");
  box(s, "Swing frames\nLogin + dashboards", 90, 260, 250, 100, blue, navy, 23);
  box(s, "DAO classes\nJDBC queries", 515, 260, 250, 100, blue, navy, 23);
  box(s, "MySQL tables\n6 related entities", 940, 260, 250, 100, blue, navy, 23);
  line(s, 340, 310, 515, 310); line(s, 765, 310, 940, 310);
  box(s, "ETAService\nLocalTime + schedule math", 515, 500, 250, 90, pale, orange, 21);
  line(s, 640, 360, 640, 500);
  textbox(s, "Model classes such as Bus, Driver, Route, Stop and Timetable carry the data between layers.", 180, 610, 920, 38, 22, gray, false, "center");
  footer(s, 4);
}

// 5 ETA calculation
{
  const s = presentation.slides.add(); title(s, "How the simulated ETA is calculated");
  box(s, "Current system time\nJava LocalTime.now()", 80, 230, 250, 110, blue, navy, 21);
  box(s, "Departure time\nfrom timetable", 385, 230, 250, 110, blue, navy, 21);
  box(s, "Stop minutes\nfrom route", 690, 230, 250, 110, blue, navy, 21);
  box(s, "ETA and status\nshown in table", 995, 230, 205, 110, orange, orange, 21);
  line(s, 330, 285, 385, 285); line(s, 635, 285, 690, 285); line(s, 940, 285, 995, 285);
  textbox(s, "Example", 90, 440, 140, 34, 22, orange, true);
  textbox(s, "Departure 08:00 + Market Road 15 min = expected arrival 08:15", 90, 480, 1060, 38, 27, navy, true);
  textbox(s, "If the computer time is 08:10, the interface shows approximately 5 minutes remaining. The student does not enter the current time.", 90, 540, 1050, 68, 23, gray, false);
  footer(s, 5);
}

// 6 GUI prototype
{
  const s = presentation.slides.add(); title(s, "Java Swing GUI prototype workflow");
  const steps = ["Login", "Student Home", "Search Bus\nor Stop", "Result Table", "Route / Driver\nDetails"];
  steps.forEach((st, i) => { box(s, st, 60 + i * 240, 260, 190, 82, i === 3 ? orange : blue, i === 3 ? orange : navy, 20); if (i < steps.length - 1) line(s, 250 + i * 240, 301, 300 + i * 240, 301); });
  textbox(s, "Controls demonstrated: text fields, password field, buttons, combo boxes, tables, message dialogs and validation.", 120, 470, 1040, 56, 25, navy, false, "center");
  textbox(s, "Admin follows Login -> Dashboard -> CRUD form -> database table. Student follows Continue as Student -> search -> result.", 120, 555, 1040, 54, 23, gray, false, "center");
  footer(s, 6);
}

// 7 Database
{
  const s = presentation.slides.add(); title(s, "MySQL design: normalized and connected");
  box(s, "admins", 80, 215, 180, 70, pale, navy, 21);
  box(s, "drivers", 80, 500, 180, 70, pale, navy, 21);
  box(s, "routes", 410, 215, 180, 70, blue, navy, 21);
  box(s, "stops", 740, 215, 180, 70, blue, navy, 21);
  box(s, "buses", 410, 500, 180, 70, blue, navy, 21);
  box(s, "timetable", 740, 500, 180, 70, blue, navy, 21);
  line(s, 590, 250, 740, 250); line(s, 500, 285, 500, 500); line(s, 260, 535, 410, 535); line(s, 590, 535, 740, 535); line(s, 500, 285, 500, 500);
  textbox(s, "Primary keys identify rows. Foreign keys connect route, stop, bus, driver and timetable records.", 970, 235, 240, 260, 22, gray, false, "center");
  footer(s, 7);
}

// 8 SQL and normalization
{
  const s = presentation.slides.add(); title(s, "Database rules and sample queries");
  textbox(s, "Constraints", 90, 190, 430, 34, 26, orange, true);
  bulletList(s, ["PRIMARY KEY for each entity", "FOREIGN KEY for relationships", "NOT NULL for required values", "UNIQUE bus number and username"], 100, 245, 470, 23, 48);
  textbox(s, "Queries used by the prototype", 690, 190, 470, 34, 26, orange, true);
  bulletList(s, ["Find bus by bus_number", "List stops by route_id", "Find buses for a stop", "Load timetable and driver details"], 700, 245, 470, 23, 48);
  textbox(s, "The tables are separated so driver and route details are not repeated in every bus row. That is the basic 3NF idea used in the design.", 130, 560, 1020, 52, 23, navy, false, "center");
  footer(s, 8);
}

// 9 Progress and limitations
{
  const s = presentation.slides.add(); title(s, "Current progress and honest project limits");
  box(s, "Completed\nSwing screens", 90, 220, 250, 100, blue, navy, 23);
  box(s, "Completed\nMySQL schema", 515, 220, 250, 100, blue, navy, 23);
  box(s, "Completed\nETA prototype", 940, 220, 250, 100, blue, navy, 23);
  textbox(s, "Limitations", 100, 440, 250, 32, 25, orange, true);
  bulletList(s, ["No physical GPS position", "No maps or external APIs", "Local database for demonstration"], 105, 495, 1050, 24, 46);
  footer(s, 9);
}

// 10 Demo sequence
{
  const s = presentation.slides.add(); title(s, "Live demonstration sequence");
  const items = ["Problem and objective", "Architecture and UML", "Run Swing GUI", "Search BX101 / Market Road", "Show ETA calculation", "Open MySQL schema", "Explain integration and future scope"];
  items.forEach((item, i) => {
    box(s, `${i + 1}. ${item}`, 120, 175 + i * 58, 1040, 40, i === 4 ? "#FFF1E8" : pale, i === 4 ? orange : navy, 21);
  });
  textbox(s, "The core message", 150, 615, 240, 28, 20, orange, true);
  textbox(s, "A transparent, schedule-based college bus information system built with beginner-friendly Java technologies.", 400, 610, 700, 38, 22, navy, false);
  footer(s, 10);
}

const candidatePath = path.join(TMP_DIR, "candidate.pptx");
await (await PresentationFile.exportPptx(presentation)).save(candidatePath);
const { finalizePresentation } = await import(pathToFileURL(path.join(SKILL_DIR, "container_tools/artifact_tool_utils.mjs")).href);
const finalPath = path.join(OUT_DIR, "BusXTrack_Midterm_Presentation.pptx");
const result = await finalizePresentation({
  workspaceDir: ROOT,
  candidatePath,
  finalPath,
  pythonExecutable: RUNTIME_PYTHON,
  integrityValidatorPath: path.join(SKILL_DIR, "container_tools/inspect_presentation_package_integrity.py"),
  layoutValidatorPath: path.join(SKILL_DIR, "container_tools/inspect_presentation_layout_geometry.py"),
  layoutArgs: ["--expected-slide-size-emu", "12192000,6858000", "--validate-bullet-geometry", "--validate-heading-fit"],
  requiredNativeTableOwnerSlides: [],
  fontPolicy: { basis: "design", families: [family] },
  verifyArtifactToolImport: true,
  receiptPath: path.join(TMP_DIR, "validation.json"),
});
console.log(JSON.stringify(result));
