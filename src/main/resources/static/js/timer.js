// ===== Chrono =====
let chronoStartTime = null;
let chronoInterval = null;
let chronoElapsed = 0;
let chronoRunning = false;
let timerRunning = false;


function updateChronoDisplay(ms) {
    const totalSeconds = Math.floor(ms / 1000);
    const minutes = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
    const seconds = String(totalSeconds % 60).padStart(2, '0');
    document.getElementById("chrono").textContent = `${minutes}:${seconds}`;
}

function startChrono() {
    if (chronoRunning) return;

    chronoStartTime = Date.now() - chronoElapsed;
    chronoInterval = setInterval(updateChrono, 100);
    chronoRunning = true;
    document.getElementById("chrono-toggle").textContent = "⏸";
}

function updateChrono() {
    chronoElapsed = Date.now() - chronoStartTime;
    updateChronoDisplay(chronoElapsed);
}

function pauseChrono() {
    clearInterval(chronoInterval);
    chronoRunning = false;
    document.getElementById("chrono-toggle").textContent = "▶️";
}

function resetChrono() {
    clearInterval(chronoInterval);
    chronoRunning = false;
    chronoElapsed = 0;
    updateChronoDisplay(0);
}

function toggleChrono() {
    if (chronoRunning) {
        pauseChrono();
    } else {
        startChrono();
    }
}

// ===== Timer =====
let timerInterval = null;
let timerRemaining = 0;
let timerEndTime = null;
let sinceEndInterval = null;

function updateTimerDisplay(seconds, isActivePhase = false) {
    const min = String(Math.floor(seconds / 60)).padStart(2, '0');
    const sec = String(seconds % 60).padStart(2, '0');
    const timerDisplay = document.getElementById("timer");

    timerDisplay.textContent = `${min}:${sec}`;
    timerDisplay.style.color = isActivePhase ? "#a30000" : "#1e6e1e"; // rouge/vert foncé
}

function updateSinceEnd() {
    if (!timerEndTime) return;

    const elapsed = Math.floor((Date.now() - timerEndTime) / 1000);
    updateTimerDisplay(elapsed, false); // false = phase repos
}

function startTimer() {
    const duration = parseInt(document.getElementById("timer-duration").value, 10);
    if (isNaN(duration) || duration < 1) return;

    startChrono();

    timerRemaining = duration;
    updateTimerDisplay(timerRemaining, true);
    clearInterval(timerInterval);
    clearInterval(sinceEndInterval);
    timerEndTime = null;

    timerRunning = true;
    document.getElementById("timer-button").textContent = "🔄";

    timerInterval = setInterval(() => {
        if (timerRemaining > 0) {
            timerRemaining--;
            updateTimerDisplay(timerRemaining, true);
        } else {
            clearInterval(timerInterval);

            timerEndTime = Date.now();
            updateSinceEnd();
            sinceEndInterval = setInterval(updateSinceEnd, 1000);
            timerRunning = false;
            document.getElementById("timer-button").textContent = "⏱";
        }
    }, 1000);
}


function resetTimer() {
    clearInterval(timerInterval);
    clearInterval(sinceEndInterval);
    timerRunning = false;
    timerEndTime = null;

    const duration = parseInt(document.getElementById("timer-duration").value, 10) || 60;
    timerRemaining = duration;
    updateTimerDisplay(timerRemaining, true);
    document.getElementById("timer-button").textContent = "⏱";
}

function handleTimer() {
    if (timerRunning) {
        resetTimer();
    } else {
        startTimer();
    }
}

