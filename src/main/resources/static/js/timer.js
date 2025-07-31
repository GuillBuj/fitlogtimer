// ===== Chrono =====
let chronoStartTime = null;
let chronoInterval = null;
let chronoElapsed = 0;
let chronoRunning = false;
let timerRunning = false;

function restoreChrono() {
    const savedStart = localStorage.getItem("chronoStartTime");
    if (savedStart) {
        chronoStartTime = parseInt(savedStart);
        chronoElapsed = Date.now() - chronoStartTime;
        chronoRunning = true;

        updateChronoDisplay(chronoElapsed); // affichage immédiat
        chronoInterval = setInterval(updateChrono, 100);
    }
}

function restoreTimer() {
    const timerRunningStored = localStorage.getItem("timerRunning");
    const timerEndStored = localStorage.getItem("timerEndTime");
    const remainingStored = localStorage.getItem("timerRemaining");

    if (timerRunningStored === "true" && remainingStored !== null) {
        timerRemaining = parseInt(remainingStored);
        timerRunning = true;

        updateTimerDisplay(timerRemaining, true);
        chronoStartTime = parseInt(localStorage.getItem("chronoStartTime"));
        chronoElapsed = Date.now() - chronoStartTime;
        chronoRunning = true;
        chronoInterval = setInterval(updateChrono, 100);

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

                updateSinceEnd();
                sinceEndInterval = setInterval(updateSinceEnd, 1000);
            }
        }, 1000);

    } else if (timerEndStored) {
        timerEndTime = parseInt(timerEndStored);
        updateSinceEnd();
        sinceEndInterval = setInterval(updateSinceEnd, 1000);
    }
}

function updateChronoDisplay(ms) {
    const totalSeconds = Math.floor(ms / 1000);
    const minutes = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
    const seconds = String(totalSeconds % 60).padStart(2, '0');

    if (document.getElementById("chrono")) {
        document.getElementById("chrono").textContent = `${minutes}:${seconds}`;
    }

    if (document.getElementById("nav-chrono")) {
        document.getElementById("nav-chrono").textContent = `${minutes}:${seconds}`;
    }
}

function startChrono() {
    if (chronoRunning) return;

    chronoStartTime = Date.now() - chronoElapsed;
    localStorage.setItem("chronoStartTime", chronoStartTime);
    chronoInterval = setInterval(updateChrono, 100);
    chronoRunning = true;

    const toggleBtn = document.getElementById("chrono-toggle");
    if (toggleBtn) toggleBtn.textContent = "⏸";
}

function updateChrono() {
    console.log("updateChrono");
    chronoElapsed = Date.now() - chronoStartTime;
    updateChronoDisplay(chronoElapsed);
}

function pauseChrono() {
    clearInterval(chronoInterval);
    chronoRunning = false;
    localStorage.removeItem("chronoStartTime");

    const toggleBtn = document.getElementById("chrono-toggle");
    if (toggleBtn) toggleBtn.textContent = "▶️";
}

function resetChrono() {
    clearInterval(chronoInterval);
    chronoInterval = null;
    chronoRunning = false;
    chronoElapsed = 0;
    localStorage.removeItem("chronoStartTime");
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
    if (timerDisplay) {
        timerDisplay.textContent = `${min}:${sec}`;
        timerDisplay.style.color = isActivePhase ? "#a30000" : "#1e6e1e";
    }

    const navTimer = document.getElementById("nav-timer");
    if (navTimer) {
        navTimer.textContent = `${min}:${sec}`;
    }
}

function updateSinceEnd() {
    if (!timerEndTime) return;

    const elapsed = Math.floor((Date.now() - timerEndTime) / 1000);
    updateTimerDisplay(elapsed, false); // false = phase repos
}

function startTimer() {
    const timerDurationInput = document.getElementById("timer-duration");
    const duration = timerDurationInput ? parseInt(timerDurationInput.value, 10) : 60;
    if (isNaN(duration) || duration < 1) return;

    startChrono();

    timerRemaining = duration;
    localStorage.setItem("timerRunning", "true");
    localStorage.setItem("timerRemaining", timerRemaining.toString());
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
            localStorage.setItem("timerRunning", "false");
            localStorage.setItem("timerEndTime", Date.now().toString());
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
    localStorage.setItem("timerRunning", "false");
    localStorage.removeItem("timerEndTime");

    const timerDurationInput = document.getElementById("timer-duration");
    timerRemaining = timerDurationInput ? parseInt(timerDurationInput.value, 10) : 60;

    updateTimerDisplay(timerRemaining, true);

    const timerBtn = document.getElementById("timer-button");
    if (timerBtn) timerBtn.textContent = "⏱";
}

function handleTimer() {
    if (timerRunning) {
        resetTimer();
    } else {
        startTimer();
    }
}

window.toggleChrono = toggleChrono;
window.handleTimer = handleTimer;
window.resetChrono = resetChrono;
window.resetTimer = resetTimer;

document.addEventListener("DOMContentLoaded", () => {
    restoreChrono();
    restoreTimer();
});

