document.addEventListener("DOMContentLoaded", async () => {
    const user = await validateToken();

    if (!user) {
        logout();
        return;
    }

    showAdminMenu(user);
});

async function validateToken() {
    const token = localStorage.getItem("token");

    if (!token) return null;

    try {
        const response = await fetch(`${API_URL}/user/me`, {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) return null;

        return await response.json();

    } catch (error) {
        return null;
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    window.location.href = "login.html";
}

function showAdminMenu(user) {
    const adminMenu = document.getElementById("adminMenu");

    if (user.role === "ADMIN") {
        adminMenu.style.display = "block";
    }
}