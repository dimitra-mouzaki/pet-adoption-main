import { routes } from '../routes.js';
import { apiCall } from '../apiCalls.js';

let selectedRole = null;

function selectRole(role, cardElement) {
  selectedRole = role;

  document.querySelectorAll('.role-card').forEach(card =>
    card.classList.remove('selected-role')
  );
  cardElement.classList.add('selected-role');

  // Enabling confirm button after role selection
  document.getElementById('confirmRoleBtn').disabled = false;
}

function createModal(role) {
  const overlay = document.createElement('div');
  overlay.id = 'roleModalOverlay';
  overlay.style = `
    position: fixed;
    top:0; left:0;
    width:100%; height:100%;
    background: rgba(0,0,0,0.6);
    display:flex;
    justify-content:center;
    align-items:center;
    z-index:1000;
  `;

  const modal = document.createElement('div');
  modal.style = `
    background-color: var(--card-bg-color);
    padding: 2rem;
    border-radius: 12px;
    width: 400px;
    max-width: 90%;
    color: var(--text-color);
    text-align: center;
  `;

  const title = document.createElement('h2');
  title.textContent = "Complete your profile";
  title.style.color = 'var(--primary-color)';
  modal.appendChild(title);

  const form = document.createElement('form');
  form.id = 'roleExtraForm';
  form.style.display = 'flex';
  form.style.flexDirection = 'column';
  form.style.gap = '1rem';

  if (role === 'vet') {
    form.innerHTML = `
      <input type="text" name="firstName" placeholder="First Name" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
      <input type="text" name="lastName" placeholder="Last Name" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
      <input type="text" name="licenseNumber" placeholder="License Number" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
    `;
  } else if (role === 'shelter') {
    form.innerHTML = `
      <input type="text" name="shelterName" placeholder="Shelter Name" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
      <input type="text" name="shelterAddress" placeholder="Shelter Address" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
      <input type="tel" name="shelterPhone" placeholder="Shelter Phone" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
    `;
  } else if (role === 'user') {
    form.innerHTML = `
      <input type="text" name="firstName" placeholder="First Name" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
      <input type="text" name="lastName" placeholder="Last Name" required style="padding:8px; border-radius:5px; border:1px solid var(--input-border-color); background:var(--input-bg-color); color:var(--text-color)">
    `;
  }

  const submitBtn = document.createElement('button');
  submitBtn.type = 'submit';
  submitBtn.textContent = 'Finish Registration';
  submitBtn.style = `
    padding:10px;
    background-color: var(--primary-color);
    color: var(--background-color);
    border:none;
    border-radius:5px;
    cursor:pointer;
    font-weight:bold;
  `;
  form.appendChild(submitBtn);

  modal.appendChild(form);
  overlay.appendChild(modal);
  document.body.appendChild(overlay);

  form.addEventListener('submit', async e => {
    e.preventDefault();
    const formData = new FormData(form);
    const userExtra = Object.fromEntries(formData.entries());

    const basicInfo = JSON.parse(localStorage.getItem('basicInfo'));

    try {
      let response;
      if (role === 'vet') {
        response = await apiCall.signupVet(basicInfo, userExtra);
      } else if (role === 'shelter') {
        response = await apiCall.signupShelter(basicInfo, userExtra);
      } else if (role === 'user') {
        response = await apiCall.signupCitizen(basicInfo, userExtra);
      }

      console.log('Registration successful:', response);
      // localStorage.setItem('userRole', role);
      // localStorage.setItem('userExtra', JSON.stringify(userExtra));

      // Remove modal και redirect
      document.body.removeChild(overlay);
      window.location.href = routes.homepage;

    } catch (err) {
      alert(err.message);
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.role-card').forEach(card => {
    card.addEventListener('click', () => {
      const role = card.dataset.role;
      selectRole(role, card);
    });
  });

  const confirmBtn = document.getElementById('confirmRoleBtn');
  confirmBtn.addEventListener('click', () => {
    if (selectedRole) {
      localStorage.setItem('userRole', selectedRole);

      createModal(selectedRole);
    }
  });
});
