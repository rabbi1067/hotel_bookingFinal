// home_scene.js
// Home scene: 3D mouse-tilt effect on the hero illustration.

document.addEventListener("DOMContentLoaded", function () {

    // ---------- 3D tilt effect on the hero illustration ----------
    const art = document.getElementById("hero-art");
    const inner = document.getElementById("hero-art-inner");
    if (art && inner) {
        const maxTilt = 12; // degrees

        art.addEventListener("mousemove", function (e) {
            const rect = art.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            const px = (x / rect.width) - 0.5;
            const py = (y / rect.height) - 0.5;

            const rotateY = px * maxTilt * 2;
            const rotateX = -py * maxTilt * 2;

            inner.style.transform =
                `rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.03)`;
        });

        art.addEventListener("mouseleave", function () {
            inner.style.transform = "rotateX(0deg) rotateY(0deg) scale(1)";
        });

        art.addEventListener("touchmove", function (e) {
            const touch = e.touches[0];
            const rect = art.getBoundingClientRect();
            const px = ((touch.clientX - rect.left) / rect.width) - 0.5;
            const py = ((touch.clientY - rect.top) / rect.height) - 0.5;
            inner.style.transform =
                `rotateX(${-py * maxTilt}deg) rotateY(${px * maxTilt}deg) scale(1.02)`;
        }, { passive: true });
    }

});