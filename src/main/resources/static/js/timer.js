// ===== Chrono / Timer =====
let chronoStartTime = null;
let chronoInterval = null;
let chronoElapsed = 0;
let chronoRunning = false;

let timerInterval = null;
let timerRemaining = 0;
let timerEndTime = null;
let timerRunning = false;
let sinceEndInterval = null;

// === Chrono ===
function restoreChrono() {
    const savedStart = parseInt(localStorage.getItem("chronoStartTime"));
    if (!isNaN(savedStart)) {
        chronoStartTime = savedStart;
        chronoElapsed = Date.now() - chronoStartTime;
        chronoRunning = true;
        updateChronoDisplay(chronoElapsed);
        chronoInterval = setInterval(updateChrono, 100);
        setNavVisibility(true);
    }
}

function startChrono() {
    if (chronoRunning) return;

    chronoStartTime = Date.now() - chronoElapsed;
    localStorage.setItem("chronoStartTime", chronoStartTime);
    chronoInterval = setInterval(updateChrono, 100);
    chronoRunning = true;

    setNavVisibility(true);
    updateChronoToggleButtons("⏸");
}

function pauseChrono() {
    clearInterval(chronoInterval);
    chronoRunning = false;
    localStorage.removeItem("chronoStartTime");
    updateChronoToggleButtons("▶");
}

function resetChrono() {
    clearInterval(chronoInterval);
    chronoInterval = null;
    chronoRunning = false;
    chronoElapsed = 0;
    localStorage.removeItem("chronoStartTime");
    updateChronoDisplay(0);
    resetTimer();
    setNavVisibility(false);
}

function toggleChrono() {
    chronoRunning ? pauseChrono() : startChrono();
}

function updateChrono() {
    chronoElapsed = Date.now() - chronoStartTime;
    updateChronoDisplay(chronoElapsed);
}

function updateChronoDisplay(ms) {
    const totalSeconds = Math.floor((isNaN(ms) ? 0 : ms) / 1000);
    const minutes = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
    const seconds = String(totalSeconds % 60).padStart(2, '0');
    document.querySelectorAll("#chrono, #nav-chrono").forEach(el => el.textContent = `${minutes}:${seconds}`);
}

function updateChronoToggleButtons(symbol) {
    document.querySelectorAll('[data-action="chrono-toggle"]').forEach(btn => btn.textContent = symbol);
}

// === Timer ===
function startTimer() {
    const timerDurationInput = document.getElementById("timer-duration");
    const duration = Number(timerDurationInput?.value || 60);
    if (isNaN(duration) || duration < 1) return;

    if (!chronoRunning && chronoElapsed === 0) startChrono();

    timerRemaining = duration;
    timerRunning = true;
    timerEndTime = null;

    localStorage.setItem("timerRunning", "true");
    localStorage.setItem("timerRemaining", timerRemaining.toString());
    localStorage.setItem("timerInitial", duration.toString());

    updateTimerDisplay(timerRemaining, true);
    updateNavbarColor("active");

    clearInterval(timerInterval);
    clearInterval(sinceEndInterval);

    timerInterval = setInterval(() => {
        if (timerRemaining > 0) {
            timerRemaining--;
            updateTimerDisplay(timerRemaining, true);
            localStorage.setItem("timerRemaining", timerRemaining.toString());
        } else {
            clearInterval(timerInterval);
            timerRunning = false;
            timerEndTime = Date.now();
            localStorage.setItem("timerRunning", "false");
            localStorage.setItem("timerEndTime", timerEndTime.toString());
            updateNavbarColor("ended");
            updateSinceEnd();
            sinceEndInterval = setInterval(updateSinceEnd, 1000);
        }
    }, 1000);
}

function resetTimer() {
    clearInterval(timerInterval);
    clearInterval(sinceEndInterval);

    timerRunning = false;
    timerEndTime = null;
    localStorage.setItem("timerRunning", "false");
    localStorage.removeItem("timerEndTime");
    localStorage.removeItem("timerRemaining");

    const input = document.getElementById("timer-duration");
    timerRemaining = input ? parseInt(input.value, 10) : 60;
    updateTimerDisplay(timerRemaining, true);
    updateNavbarColor("ended");
    localStorage.removeItem("timerInitial");
}

function handleTimer() {
    timerRunning ? resetTimer() : startTimer();
}

function updateTimerDisplay(seconds, isActivePhase = false) {
    const min = String(Math.floor(seconds / 60)).padStart(2, '0');
    const sec = String(seconds % 60).padStart(2, '0');
    const display = `${min}:${sec}`;
    const color = isActivePhase ? "#a30000" : "#1e6e1e";

    document.querySelectorAll("#timer, #nav-timer").forEach(el => {
        el.textContent = display;
    });
    document.querySelectorAll("#timer").forEach(el => {
        el.style.color = color;
    });
}

function updateSinceEnd() {
    if (!timerEndTime) return;
    const elapsed = Math.floor((Date.now() - timerEndTime) / 1000);
    const initial = Number(localStorage.getItem("timerInitial")) || 60;
    updateTimerDisplay(initial + elapsed, false);
}

function restoreTimer() {
    const running = localStorage.getItem("timerRunning") === "true";
    const end = parseInt(localStorage.getItem("timerEndTime"));
    const remaining = parseInt(localStorage.getItem("timerRemaining"));

    if (running && !isNaN(remaining)) {
        timerRemaining = remaining;
        timerRunning = true;
        updateTimerDisplay(timerRemaining, true);
        updateNavbarColor("active");

        if (!chronoRunning) restoreChrono();

        timerInterval = setInterval(() => {
            if (--timerRemaining > 0) {
                updateTimerDisplay(timerRemaining, true);
                localStorage.setItem("timerRemaining", timerRemaining.toString());
            } else {
                clearInterval(timerInterval);
                timerRunning = false;
                timerEndTime = Date.now();
                localStorage.setItem("timerRunning", "false");
                localStorage.setItem("timerEndTime", timerEndTime.toString());
                updateNavbarColor("ended");
                updateSinceEnd();
                sinceEndInterval = setInterval(updateSinceEnd, 1000);
            }
        }, 1000);
    } else if (!isNaN(end)) {
        timerEndTime = end;
        updateNavbarColor("ended");
        updateSinceEnd();
        sinceEndInterval = setInterval(updateSinceEnd, 1000);
    }
}


function updateNavbarColor(state) {
    const nav = document.getElementById("chrono-timer-nav");
    if (!nav) return;

    nav.classList.remove("timer-active", "timer-ended");

    if (state === "active") {
        nav.classList.add("timer-active");
    } else if (state === "ended") {
        nav.classList.add("timer-ended");
    }
}

// === Utilitaires ===
function setNavVisibility(visible) {
    const nav = document.getElementById("chrono-timer-nav");
    if (nav) nav.classList.toggle("hidden", !visible);
}

// === DOM Ready ===
document.addEventListener("DOMContentLoaded", () => {
    restoreChrono();
    restoreTimer();

    const actionMap = {
        "chrono-toggle": toggleChrono,
        "chrono-reset": resetChrono,
        "chrono-stop": resetChrono,
        "timer-toggle": handleTimer,
        "timer-reset": resetTimer,
    };

    document.querySelectorAll("[data-action]").forEach(btn => {
        const action = btn.getAttribute("data-action");
        if (actionMap[action]) {
            btn.addEventListener("click", actionMap[action]);
        }
    });

    // Inactivité de plus de 2h
    const last = parseInt(localStorage.getItem("lastInteractionTime"));
    if (!isNaN(last) && Date.now() - last > 2 * 60 * 60 * 1000) {
        resetChrono();
        resetTimer();
        localStorage.clear();
        localStorage.setItem("lastInteractionTime", Date.now().toString());
    }

    ["click", "keydown", "touchstart"].forEach(evt =>
        window.addEventListener(evt, () =>
            localStorage.setItem("lastInteractionTime", Date.now().toString())
        )
    );
});

// Exports
window.toggleChrono = toggleChrono;
window.handleTimer = handleTimer;
window.resetChrono = resetChrono;
window.resetTimer = resetTimer;