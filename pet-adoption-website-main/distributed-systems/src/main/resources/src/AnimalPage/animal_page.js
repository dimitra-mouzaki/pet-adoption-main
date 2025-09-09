import { routes } from '../routes.js';
import { apiCall } from '../apiCalls.js';

document.addEventListener("DOMContentLoaded", async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const petId = parseInt(urlParams.get("id"));
  const userRole = localStorage.getItem('userRole');

  if (isNaN(petId)) {
    document.getElementById("animalCard").innerHTML = `
      <div class="alert alert-danger text-center">Pet not found.</div>
    `;
    return;
  }

  try {
    const animal = await apiCall.getPetById(petId);

    if (!animal || !animal.name) {
      throw new Error("Invalid pet data");
    }

    document.getElementById("animalImage").src = "../img/dog2.jpg";
    document.getElementById("animalName").textContent = animal.name;
    document.getElementById("animalBreed").textContent = `Breed: ${animal.breed}`;
    document.getElementById("animalAge").textContent = `Age: ${animal.age}`;
    document.getElementById("animalDescription").textContent = 
      `This is ${animal.name}, a ${animal.age}-year-old ${animal.breed}.`;

    const statusElement = document.getElementById("animalStatus");
    const actionsContainer = document.getElementById("animalActions");
    actionsContainer.innerHTML = "";
    const scheduleBtn = document.getElementById("scheduleBtn");
    const adoptBtn = document.getElementById("adoptBtn");

    scheduleBtn.style.display = "none";
    adoptBtn.style.display = "none";

    // Status badge
    if (animal.status === "PENDING_APPROVAL" && userRole === 'admin') {
      statusElement.textContent = "Pending Approval";
      statusElement.className = "status-badge status-pending px-4 py-2 rounded-pill fw-bold";

      // Admin buttons: Approve / Reject
      const approveBtn = document.createElement("button");
      approveBtn.className = "btn btn-success me-2";
      approveBtn.textContent = "Approve";
      approveBtn.addEventListener("click", async () => {
        try {
          await apiCall.updatePetStatus(petId, "APPROVED");
          alert(`${animal.name} approved successfully`);
          window.location.reload();
        } catch (err) {
          alert("Error approving pet: " + err.message);
        }
      });

      const rejectBtn = document.createElement("button");
      rejectBtn.className = "btn btn-danger";
      rejectBtn.textContent = "Reject";
      rejectBtn.addEventListener("click", async () => {
        try {
          await apiCall.updatePetStatus(petId, "REJECTED");
          alert(`${animal.name} rejected`);
          window.location.reload();
        } catch (err) {
          alert("Error rejecting pet: " + err.message);
        }
      });

      actionsContainer.appendChild(approveBtn);
      actionsContainer.appendChild(rejectBtn);

    } else if (animal.status === "APPROVED" && animal.healthVerified) {
      statusElement.textContent = "Healthy & Verified";
      statusElement.className = "status-badge status-healthy px-4 py-2 rounded-pill fw-bold";

      if(userRole === 'basic') {
        scheduleBtn.style.display = "inline-block";
        adoptBtn.style.display = "inline-block";

        scheduleBtn.addEventListener("click", async () => {
          try {
            await apiCall.scheduleVisit(petId);
            alert("Appointment scheduled successfully!");
          } catch (err) {
            alert("Could not schedule appointment: " + err.message);
          }
        });

        adoptBtn.addEventListener("click", async () => {
          try {
            await apiCall.adoptPet(petId);
            alert("Adoption request submitted!");
            window.location.reload();
          } catch (err) {
            alert("Could not submit adoption request: " + err.message);
          }
        });
      }

    } else {
      // Unhealthy / unverified animals -> Vet can validate health
      statusElement.textContent = !animal.healthVerified 
        ? "Unhealthy - Needs Vet Care" 
        : "Unverified";
      statusElement.className = !animal.healthVerified
        ? "status-badge status-unhealthy px-4 py-2 rounded-pill fw-bold"
        : "status-badge status-unverified px-4 py-2 rounded-pill fw-bold";

      if (!animal.healthVerified && userRole === 'vet') {
        const validateBtn = document.createElement("button");
        validateBtn.className = "btn btn-success shadow-sm";
        validateBtn.innerHTML = `<i class="fa-solid fa-check"></i> Validate Health`;
        validateBtn.addEventListener("click", async () => {
            try {
                await apiCall.validatePetHealth(petId);
                alert(`${animal.name} health validated successfully!`);
                window.location.reload();
            } catch (err) {
                alert("Error validating pet health: " + err.message);
            }
        });
        actionsContainer.appendChild(validateBtn);
      } 
    } 

  } catch (err) {
    console.error("Error loading pet:", err);
    document.getElementById("animalCard").innerHTML = `
      <div class="alert alert-danger text-center">Could not load pet information.</div>
    `;
  }
});
