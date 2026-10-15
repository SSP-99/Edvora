// src/main/resources/static/js/pomodoro.js
let timer = null;
let minutes = 25;
let seconds = 0;
let isFocusMode = true;
let isRunning = false;

const display = document.getElementById('timerDisplay');
const modeText = document.getElementById('timerMode');
const startPauseBtn = document.getElementById('startPauseBtn');
const resetBtn = document.getElementById('resetBtn');
const cycleDisplay = document.getElementById('cycleCount');

let audioCtx = null;
function initAudio() {
    if (!audioCtx) {
        audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
}

function playBeep() {
    initAudio();
    if (audioCtx.state === 'suspended') {
        audioCtx.resume();
    }
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();
    osc.connect(gain);
    gain.connect(audioCtx.destination);
    osc.type = 'sine';
    osc.frequency.setValueAtTime(880, audioCtx.currentTime);
    gain.gain.setValueAtTime(0.5, audioCtx.currentTime);
    osc.start();
    osc.stop(audioCtx.currentTime + 0.3);
}

function updateDisplay() {
    const minStr = minutes < 10 ? '0' + minutes : minutes;
    const secStr = seconds < 10 ? '0' + seconds : seconds;
    display.textContent = `${minStr}:${secStr}`;
}

function getStoredCycles() {
    return parseInt(localStorage.getItem('edvora_pomodoro_cycles') || '0', 10);
}

function updateCycleDisplay() {
    cycleDisplay.textContent = getStoredCycles();
}

function switchMode() {
    playBeep();
    if (isFocusMode) {
        let currentCycles = getStoredCycles();
        localStorage.setItem('edvora_pomodoro_cycles', currentCycles + 1);
        updateCycleDisplay();
        isFocusMode = false;
        minutes = 5;
        modeText.textContent = "Break Time";
        modeText.className = "fs-4 fw-semibold text-success";
    } else {
        isFocusMode = true;
        minutes = 25;
        modeText.textContent = "Focus Time";
        modeText.className = "fs-4 fw-semibold text-primary";
    }
    seconds = 0;
    updateDisplay();
}

function tick() {
    if (seconds === 0) {
        if (minutes === 0) {
            switchMode();
            return;
        }
        minutes--;
        seconds = 59;
    } else {
        seconds--;
    }
    updateDisplay();
}

startPauseBtn.addEventListener('click', () => {
    initAudio();
    if (isRunning) {
        clearInterval(timer);
        isRunning = false;
        startPauseBtn.textContent = "Start";
        startPauseBtn.className = "btn btn-lg btn-primary px-4 fw-bold";
    } else {
        timer = setInterval(tick, 1000);
        isRunning = true;
        startPauseBtn.textContent = "Pause";
        startPauseBtn.className = "btn btn-lg btn-danger px-4 fw-bold";
    }
});

resetBtn.addEventListener('click', () => {
    clearInterval(timer);
    isRunning = false;
    isFocusMode = true;
    minutes = 25;
    seconds = 0;
    modeText.textContent = "Focus Time";
    modeText.className = "fs-4 fw-semibold text-primary";
    startPauseBtn.textContent = "Start";
    startPauseBtn.className = "btn btn-lg btn-primary px-4 fw-bold";
    updateDisplay();
});

updateDisplay();
updateCycleDisplay();
