const canvas = document.getElementById("donutChart");
const classique = parseInt(canvas.dataset.classique);
const hphc = parseInt(canvas.dataset.hphc);

new Chart(canvas, {
    type: "doughnut",
    data: {
        labels: ["Classique", "HP/HC"],
        datasets: [{
            data: [classique, hphc],
            backgroundColor: ["#2a7a4a", "#5bbf8a"],
            borderWidth: 0,
            hoverOffset: 4
        }]
    },
    options: {
        cutout: "70%",
        plugins: {
            legend: { display: false },
            tooltip: {
                callbacks: {
                    label: ctx => ` ${ctx.label} : ${ctx.parsed}%`
                }
            }
        }
    }
});

const canvasMode = document.getElementById("donutChartMode");
const reel = parseInt(canvasMode.dataset.reel);
const echeancier = parseInt(canvasMode.dataset.echeancier);

new Chart(canvasMode, {
    type: "doughnut",
    data: {
        labels: ["Réel", "Échéancier"],
        datasets: [{
            data: [reel, echeancier],
            backgroundColor: ["#1a3a8a", "#85b7eb"],
            borderWidth: 0,
            hoverOffset: 4
        }]
    },
    options: {
        cutout: "70%",
        plugins: {
            legend: { display: false },
            tooltip: {
                callbacks: {
                    label: ctx => ` ${ctx.label} : ${ctx.parsed}%`
                }
            }
        }
    }
});
