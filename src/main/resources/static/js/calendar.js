document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');

    fetch('/api/calendar')
        .then(response => response.json())
        .then(data => {
            const calendarItems = data.calendarItems ?? [];
            const exerciseColors = data.exerciseColors ?? {};
            const workoutTypeColors = data.workoutTypeColors ?? {};

            const events = calendarItems.map(item => {
                return {
                    title: item.workoutType? item.workoutType : " ",
                    start: item.date,
                    color: workoutTypeColors[item.workoutType] || '#999',
                    idWorkout: item.idWorkout,
                    extendedProps: {
                        exercises: Array.isArray(item.exerciseShortNames)
                            ? item.exerciseShortNames.map(exerciseName => ({
                                name: exerciseName,
                                color: exerciseColors[exerciseName] || '#666'
                            })) : []
                    }
                };
            });

            const calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                locale: 'fr',
                firstDay: 1, //débute la semaine le lundi
                height: 'auto',
                contentHeight: 'auto',
                expandRows: true,
                events: events,
                customButtons: {
                    prevHalfYear: {
                        text: '← 6',
                        click: function () {
                            calendar.incrementDate({ months: -6 });
                        }
                    },
                    prevYear: {
                        text: '← Année',
                        click: function () {
                            calendar.incrementDate({ years: -1 });
                        }
                    },
                    nextYear: {
                        text: 'Année →',
                        click: function () {
                            calendar.incrementDate({ years: 1 });
                        }
                    }
                },
                headerToolbar: {
                    left: 'prevYear,prevHalfYear,prev',
                    center: 'title',
                    right: 'next,nextHalfYear,nextYear'
                },
                eventClick: function(info) {
                    window.location.href = '/workouts/' + info.event.extendedProps.idWorkout;
                },
                eventContent: function(arg) {
                    const exercises = arg.event.extendedProps.exercises || [];

                    const exerciseHTML = `
                        <div style="
                            display: flex;
                            flex-wrap: wrap;
                            gap: 4px;
                            align-items: center;
                        ">
                            ${exercises.map(ex => `
                                <span style="
                                    background: ${ex.color};
                                    color: white;
                                    padding: 2px 6px;
                                    border-radius: 5px;
                                    font-size: 0.75em;
                                    white-space: nowrap;
                                ">${ex.name}</span>
                            `).join('')}
                        </div>
                    `;

                    return {
                        html: `
                            <div style="font-weight:bold; margin-bottom:4px; color:#222">${arg.event.title}</div>
                            <div>${exerciseHTML}</div>
                        `
                    };
                }
            });

            calendar.render();
        })
        .catch(error => console.error("Erreur de chargement des données du calendrier", error));
});