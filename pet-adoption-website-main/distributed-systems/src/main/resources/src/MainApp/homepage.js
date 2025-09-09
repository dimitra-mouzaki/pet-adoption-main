import { routes } from '../routes.js';
import { apiCall } from '../apiCalls.js';

document.addEventListener("DOMContentLoaded", async () => {
    const userName = localStorage.getItem('userName');
    const userRole = localStorage.getItem('userRole');
    const token = localStorage.getItem('token');

    const headerRightContent = document.getElementById("headerRightContent");
    const petCardsContainer = document.getElementById("petCardsContainer");
    const petFilter = document.getElementById("petFilter");
    const addPetBtn = document.getElementById("addPetBtn");
    const addPetForm = document.getElementById("addPetForm");
    const checkSheltersBtn = document.getElementById("checkSheltersBtn");
    const sheltersContainer = document.getElementById("sheltersContainer");
    const userAdoptionsContainer = document.getElementById("userAdoptionsContainer");
    const userAppointmentsContainer = document.getElementById("userAppointmentsContainer");
    const myAppsBtn = document.getElementById("myAppsBtn");
    const myRequestsBtn = document.getElementById("myRequestsBtn");

    // Navbar
    if (userName) {
        headerRightContent.innerHTML = `
            <span class="text-white me-3">Welcome back, ${userName}!</span>
            <button class="btn btn-outline-light btn-sm" id="logoutBtn">
                <i class="fa-solid fa-person-walking-arrow-right"></i>
            </button>
        `;
        document.getElementById("logoutBtn").addEventListener("click", () => {
            localStorage.clear();
            window.location.href = routes.homepage;
        });
    } else {
        headerRightContent.innerHTML = `
            <a href=${routes.login_register} class="btn btn-outline-light btn-sm me-2">
                <i class="fas fa-sign-in-alt"></i> Sign In
            </a>
            <a href=${routes.login_register} class="btn btn-light btn-sm">
                <i class="fas fa-user-plus"></i> Sign Up
            </a>
        `;
        petCardsContainer.innerHTML = `
            <div class="alert alert-warning w-100 text-center">
                Please sign in to view available pets.
            </div>
        `;
        petFilter.style.display = 'none';
        return;
    }

    // Burger menu filters
    const filterType = document.getElementById("filterType");
    const filterGender = document.getElementById("filterGender");
    const filterAgeRange = document.getElementById("filterAgeRange");
    const applyFiltersBtn = document.getElementById("applyFiltersBtn");

    applyFiltersBtn.addEventListener("click", async () => {
        const type = filterType.value;
        const gender = filterGender.value;
        const ageRange = filterAgeRange.value;

        try {
            const pets = await apiCall.getFilteredPets({ type, gender, ageRange });

            // Render pets
            renderPets(pets);

            const offcanvasEl = document.getElementById("filtersMenu");
            const offcanvas = bootstrap.Offcanvas.getInstance(offcanvasEl);
            if (offcanvas) offcanvas.hide();

        } catch (err) {
            console.error("Error applying filters:", err);
            petCardsContainer.innerHTML = `
                <div class="alert alert-danger text-center">
                    Could not load filtered pets: ${err.message}
                </div>
            `;
        }
    });

    // Hide filter by default
    petFilter.style.display = 'none';

    let pets = [];

    // Load pets based on role and filter
    async function loadPets(filter = 'available') {
        try {
            switch (userRole) {
                case 'basic':
                case 'shelter':
                    pets = await apiCall.getAvailablePets();
                    break;
                case 'vet':
                    pets = filter === 'unhealthy'
                        ? await apiCall.getUnhealthyPets()
                        : await apiCall.getAvailablePets();
                    break;
                case 'admin':
                    pets = filter === 'pending'
                        ? await apiCall.getPendingPets()
                        : await apiCall.getAllPets();
                    break;
                default:
                    pets = await apiCall.getAvailablePets();
            }

            renderPets(pets);
        } catch (err) {
            console.error("Error fetching pets:", err);
            petCardsContainer.innerHTML = `
                <div class="alert alert-warning w-100 text-center">
                    Could not load pets: ${err.message}
                </div>
            `;
        }
    }

    // Render pets
    function renderPets(pets) {
        petCardsContainer.innerHTML = '';

        if (!Array.isArray(pets)) {
            petCardsContainer.innerHTML = `
                <div class="alert alert-info w-100 text-center">
                    ${pets.message || pets || 'No pets to display.'}
                </div>
            `;
            return;
        }

        if (pets.length === 0) {
            petCardsContainer.innerHTML = `
                <div class="alert alert-info w-100 text-center">
                    No pets to display.
                </div>
            `;
            return;
        }

        // Render cards
        pets.forEach(pet => {
            const card = document.createElement('div');
            card.className = 'col-md-4 mb-4';
            card.innerHTML = `
                <div class="card h-100">
                    <img src="../img/dog2.jpg" class="card-img-top" alt="${pet.name}">
                    <div class="card-body">
                        <h5 class="card-title">${pet.name}</h5>
                        <p class="card-text">Breed: ${pet.breed}</p>
                        <p class="card-text">Age: ${pet.age}</p>
                        <p class="card-text">Gender: ${pet.gender}</p>
                        <p class="card-text">Animal: ${pet.animal}</p>
                        <div class="d-flex justify-content-between mt-3">
                            <button class="btn btn-success btn-sm seemore-btn">See more information</button>
                        </div>
                    </div>
                </div>
            `;
            petCardsContainer.appendChild(card);

            card.querySelector('.seemore-btn').addEventListener('click', () => {
                // alert(`See more information about ${pet.name}`);
                window.location.href = `${routes.animal_page}?id=${pet.id}`;
            });
        });
    }

    if (userRole === 'shelter') {
        addPetBtn.style.display = 'inline-block';

        addPetBtn.addEventListener("click", () => {
            const modal = new bootstrap.Modal(document.getElementById("addPetModal"));
            modal.show();
        });

        addPetForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);

            const newPet = {
                name: formData.get("name"),
                age: parseInt(formData.get("age")),
                animal: formData.get("animal"),
                breed: formData.get("breed"),
                gender: formData.get("gender")
            };

            try {
                await apiCall.addPet(newPet);
                bootstrap.Modal.getInstance(document.getElementById("addPetModal")).hide();
                e.target.reset();
                loadPets();
            } catch (err) {
                alert("Error adding pet: " + err.message);
            }
        });
    } else {
        addPetBtn.style.display = 'none';
    }
    
    // Setup filter visibility & listener
    if (userRole === 'vet') {
        petFilter.style.display = 'inline-block';
        petFilter.innerHTML = `
            <option value="available">Available</option>
            <option value="unhealthy">Unhealthy</option>
        `;
        petFilter.addEventListener('change', (e) => loadPets(e.target.value));
    } else if (userRole === 'admin') {
        petFilter.style.display = 'inline-block';
        petFilter.innerHTML = `
            <option value="all">All</option>
            <option value="pending">Pending</option>
        `;
        petFilter.addEventListener('change', (e) => loadPets(e.target.value));
    }

    if (userRole === 'basic' && myAppsBtn) {
        myAppsBtn.style.display = "inline-block";

        myAppsBtn.addEventListener("click", async () => {
            try {
                const appointments = await apiCall.getUserAppointments();
                userAppointmentsContainer.innerHTML = "";

                showSection("apps");

                // Hide pets and filter
                petCardsContainer.innerHTML = "";
                petFilter.style.display = "none";

                if (!appointments || appointments.length === 0) {
                    userAppointmentsContainer.innerHTML = `
                        <div class="alert alert-info text-center">
                            You have no appointments scheduled.
                        </div>
                    `;
                    return;
                }

                const ul = document.createElement("ul");
                ul.className = "list-group";
                ul.style.width = "50%";
                ul.style.margin = "0 auto";

                appointments.forEach(app => {
                    const li = document.createElement("li");
                    li.className = "list-group-item d-flex justify-content-between align-items-center";

                    li.innerHTML = `
                        <div>
                            <strong>${app.pet.name}</strong> at <strong>${app.shelter.name}</strong><br>
                            <small>${new Date(app.date).toLocaleString()}</small>
                        </div>
                        <div>
                            <button class="btn btn-danger btn-sm cancel-appointment">Cancel</button>
                        </div>
                    `;

                    li.querySelector(".cancel-appointment").addEventListener("click", async () => {
                        try {
                            await apiCall.cancelAppointment(app.id);
                            li.remove();
                            if (!ul.hasChildNodes()) {
                                userAppointmentsContainer.innerHTML = `
                                    <div class="alert alert-info text-center">
                                        You have no appointments scheduled.
                                    </div>
                                `;
                            }
                        } catch (err) {
                            alert("Error cancelling appointment: " + err.message);
                        }
                    });

                    ul.appendChild(li);
                });

                userAppointmentsContainer.appendChild(ul);

            } catch (err) {
                console.error("Error fetching appointments:", err);
                userAppointmentsContainer.innerHTML = `
                    <div class="alert alert-danger text-center">
                        Could not load appointments: ${err.message}
                    </div>
                `;
            }
        });
    }

    if (userRole === 'basic' && myRequestsBtn) {
        myRequestsBtn.style.display = "inline-block";

        myRequestsBtn.addEventListener("click", async () => {
            try {
                const adoptions = await apiCall.getUserAdoptions();
                userAdoptionsContainer.innerHTML = "";

                showSection("requests");

                // Hide pets and filter
                petCardsContainer.innerHTML = "";
                petFilter.style.display = "none";

                if (!adoptions || adoptions.length === 0) {
                    userAdoptionsContainer.innerHTML = `
                        <div class="alert alert-info text-center">
                            You have no adoption requests.
                        </div>
                    `;
                    return;
                }

                const ul = document.createElement("ul");
                ul.className = "list-group";
                ul.style.width = "60%";
                ul.style.margin = "0 auto";

                adoptions.forEach(req => {
                    const li = document.createElement("li");
                    li.className = "list-group-item d-flex justify-content-between align-items-center";

                    li.innerHTML = `
                        <div>
                            <strong>${req.pet.name}</strong> (${req.pet.breed}, ${req.pet.age}y, ${req.pet.gender})<br>
                            at <strong>${req.pet.shelter.name}</strong> (${req.pet.shelter.address})<br>
                            <small>Status: ${req.status}</small>
                        </div>
                        <div>
                            ${req.status === 'PENDING' ? '<button class="btn btn-danger btn-sm cancel-request">Cancel</button>' : ''}
                        </div>
                    `;

                    if (req.status === 'PENDING') {
                        li.querySelector(".cancel-request").addEventListener("click", async () => {
                            try {
                                await apiCall.cancelAdoptionRequest(req.id);
                                li.remove();
                                if (!ul.hasChildNodes()) {
                                    userAdoptionsContainer.innerHTML = `
                                        <div class="alert alert-info text-center">
                                            You have no adoption requests.
                                        </div>
                                    `;
                                }
                            } catch (err) {
                                alert("Error cancelling request: " + err.message);
                            }
                        });
                    }

                    ul.appendChild(li);
                });

                userAdoptionsContainer.appendChild(ul);
            } catch (err) {
                console.error("Error fetching adoption requests:", err);
                userAdoptionsContainer.innerHTML = `
                    <div class="alert alert-danger text-center">
                        Could not load adoption requests: ${err.message}
                    </div>
                `;
            }
        });
    }

    if( userRole === 'shelter' && myAppsBtn) {
        myAppsBtn.style.display = "inline-block";
    
        myAppsBtn.addEventListener("click", async () => {
            try {
                const appointments = await apiCall.getShelterAppointments();
                userAppointmentsContainer.innerHTML = "";
    
                showSection("apps");
                // Hide pets and filter
                petCardsContainer.innerHTML = "";
                petFilter.style.display = "none";
    
                // Add back button
                myAppsBtn.style.display = "none";
    
                if (!appointments || appointments.length === 0) {
                    userAppointmentsContainer.innerHTML = `
                        <div class="alert alert-info text-center">
                            No appointments scheduled at your shelter.
                        </div>
                    `;
                    return;
                }
    
                const ul = document.createElement("ul");
                ul.className = "list-group";
                ul.style.width = "50%";
                ul.style.margin = "0 auto";
    
                appointments.forEach(app => {
                    const li = document.createElement("li");
                    li.className = "list-group-item d-flex justify-content-between align-items-center";
    
                    li.innerHTML = `
                         <div>
                            <strong>Pet:</strong> ${app.pet.name} (${app.pet.breed}, ${app.pet.age}y)<br>
                            <strong>Citizen:</strong> ${app.citizen.firstName} ${app.citizen.lastName}<br>
                        </div>
                        <div>
                            <button class="btn btn-danger btn-sm cancel-appointment">Cancel</button>
                        </div>
                    `;
    
                    li.querySelector(".cancel-appointment").addEventListener("click", async () => {
                        try {
                            await apiCall.cancelAppointment(app.id);
                            li.remove();
                            if (!ul.hasChildNodes()) {
                                userAppointmentsContainer.innerHTML = `
                                    <div class="alert alert-info text-center">
                                        No appointments scheduled at your shelter.
                                    </div>
                                `;
                            }
                        } catch (err) {
                            alert("Error cancelling appointment: " + err.message);
                        }
                    });
    
                    ul.appendChild(li);
                });
    
                userAppointmentsContainer.appendChild(ul);
    
            } catch (err) {
                console.error("Error fetching appointments:", err);
                userAppointmentsContainer.innerHTML = `
                    <div class="alert alert-danger text-center">
                        Could not load appointments: ${err.message}
                    </div>
                `;
            }

        });
    
    }

    if (userRole === 'shelter' && myRequestsBtn) {
        myRequestsBtn.style.display = "inline-block";

        myRequestsBtn.addEventListener("click", async () => {
            try {
                const requests = await apiCall.getShelterRequests();
                userAdoptionsContainer.innerHTML = "";

                showSection("requests");

                petCardsContainer.innerHTML = "";
                petFilter.style.display = "none";

                // Back button
                myRequestsBtn.style.display = "none";

                if (!requests || requests.length === 0) {
                    userAdoptionsContainer.innerHTML = `
                        <div class="alert alert-info text-center">
                            No adoption requests found.
                        </div>
                    `;
                    return;
                }

                const ul = document.createElement("ul");
                ul.className = "list-group";
                ul.style.width = "60%";
                ul.style.margin = "0 auto";

                requests.forEach(req => {
                    const li = document.createElement("li");
                    li.className = "list-group-item d-flex justify-content-between align-items-center";

                    li.innerHTML = `
                        <div>
                            <strong>Pet:</strong> ${req.pet.name} (${req.pet.breed}, ${req.pet.age}y, ${req.pet.gender})<br>
                            <strong>Citizen:</strong> ${req.citizen.firstName} ${req.citizen.lastName}<br>
                            <small>Status: <span class="badge bg-${req.status === "PENDING" ? "warning" : req.status === "APPROVED" ? "success" : "danger"}">
                                ${req.status}
                            </span></small>
                        </div>
                        <div>
                            ${req.status === "PENDING" ? `
                                <button class="btn btn-success btn-sm approve-request">Approve</button>
                                <button class="btn btn-danger btn-sm reject-request ms-2">Reject</button>
                            ` : ""}
                        </div>
                    `;

                    if (req.status === "PENDING") {
                        li.querySelector(".approve-request").addEventListener("click", async () => {
                            try {
                                await apiCall.approveRequest(req.id);
                                li.querySelector("span").className = "badge bg-success";
                                li.querySelector("span").innerText = "APPROVED";
                                li.querySelector(".approve-request").remove();
                                li.querySelector(".reject-request").remove();
                            } catch (err) {
                                alert("Error approving request: " + err.message);
                            }
                        });

                        li.querySelector(".reject-request").addEventListener("click", async () => {
                            try {
                                await apiCall.rejectRequest(req.id);
                                li.querySelector("span").className = "badge bg-danger";
                                li.querySelector("span").innerText = "REJECTED";
                                li.querySelector(".approve-request").remove();
                                li.querySelector(".reject-request").remove();
                            } catch (err) {
                                alert("Error rejecting request: " + err.message);
                            }
                        });
                    }

                    ul.appendChild(li);
                });

                userAdoptionsContainer.appendChild(ul);

            } catch (err) {
                console.error("Error fetching adoption requests:", err);
                userAdoptionsContainer.innerHTML = `
                    <div class="alert alert-danger text-center">
                        Could not load requests: ${err.message}
                    </div>
                `;
            }
        });
    }

    // Check Shelters logic for admin
    if (userRole === "admin" && checkSheltersBtn) {
        checkSheltersBtn.style.display = "inline-block";

        checkSheltersBtn.addEventListener("click", async () => {
            try {
                const shelters = await apiCall.getUnverifiedShelters();
                renderShelters(shelters);

                // Hide pets and filter
                petCardsContainer.innerHTML = "";
                petFilter.style.display = "none";

                // Add back button
                checkSheltersBtn.style.display = "none";
                const backBtn = document.createElement("button");
                backBtn.className = "btn btn-secondary ms-2";
                backBtn.id = "backToPetsBtn";
                backBtn.innerText = "Back to Pets";
                checkSheltersBtn.parentNode.appendChild(backBtn);

                backBtn.addEventListener("click", () => {
                    sheltersContainer.innerHTML = "";
                    backBtn.remove();
                    checkSheltersBtn.style.display = "inline-block";
                    petFilter.style.display = "inline-block";
                    loadPets();
                });

            } catch (err) {
                console.error("Error fetching shelters:", err);
                sheltersContainer.innerHTML = `
                    <div class="alert alert-danger text-center">
                        Could not load shelters: ${err.message}
                    </div>
                `;
            }
        });
    }

    function renderShelters(shelters) {
        sheltersContainer.innerHTML = "";

        if (!shelters || shelters.length === 0) {
            sheltersContainer.innerHTML = `
                <div class="alert alert-info text-center">
                    No shelters pending approval.
                </div>
            `;
            return;
        }

        const ul = document.createElement("ul");
        ul.className = "list-group";
        ul.style.width = "50%";
        ul.style.margin = "0 auto";

        shelters.forEach(shelter => {
            const li = document.createElement("li");
            li.className = "list-group-item d-flex justify-content-between align-items-center";
            li.style.listStyleType = "disc";
            li.style.paddingLeft = "1.2rem";

            li.innerHTML = `
                <div>
                    <strong>${shelter.name}</strong><br>
                    <small>
                        📞 ${shelter.phoneNumber} | 📍 ${shelter.address} | 👤 ${shelter.user.username} (${shelter.user.email})
                    </small>
                </div>
                <div>
                    <button class="btn btn-success btn-sm me-2 approve-shelter">
                        <i class="fa-solid fa-check"></i> Approve
                    </button>
                    <button class="btn btn-danger btn-sm reject-shelter">
                        <i class="fa-solid fa-xmark"></i> Reject
                    </button>
                </div>
            `;

            ul.appendChild(li);

            // Approve
            li.querySelector(".approve-shelter").addEventListener("click", async () => {
                try {
                    await apiCall.approveShelter(shelter.id);
                    li.remove();
                    if (!ul.hasChildNodes()) renderShelters([]); 
                } catch (err) {
                    alert("Error approving shelter: " + err.message);
                }
            });

            // Reject
            li.querySelector(".reject-shelter").addEventListener("click", async () => {
                try {
                    await apiCall.rejectShelter(shelter.id);
                    li.remove();
                    if (!ul.hasChildNodes()) renderShelters([]);
                } catch (err) {
                    alert("Error rejecting shelter: " + err.message);
                }
            });
        });

        sheltersContainer.appendChild(ul);
    }
    
    function showSection(section) {
        userAdoptionsContainer.innerHTML = "";
        userAppointmentsContainer.innerHTML = "";
        petCardsContainer.innerHTML = "";
        petFilter.style.display = "none";
        
        myAppsBtn.style.display = "none";
        myRequestsBtn.style.display = "none";
        
        let backBtn = document.getElementById("backToPetsBtn");
        if (!backBtn) {
            backBtn = document.createElement("button");
            backBtn.className = "btn btn-secondary ms-2";
            backBtn.id = "backToPetsBtn";
            backBtn.innerText = "Back to Pets";
            myAppsBtn.parentNode.appendChild(backBtn);
    
            backBtn.addEventListener("click", () => {
                userAdoptionsContainer.innerHTML = "";
                userAppointmentsContainer.innerHTML = "";
                backBtn.remove();
                myAppsBtn.style.display = "inline-block";
                myRequestsBtn.style.display = "inline-block";
                
                if (userRole === "vet" || userRole === "admin") {
                    petFilter.style.display = "inline-block";
                } else {
                    petFilter.style.display = "none";
                }
                
                loadPets();
            });
        }
    
        if (section === "apps") {
            myRequestsBtn.style.display = "inline-block";
        }
        else if (section === "requests") {
            myAppsBtn.style.display = "inline-block";
        }
    }
    
    // Initial load: pets
    loadPets();
});

