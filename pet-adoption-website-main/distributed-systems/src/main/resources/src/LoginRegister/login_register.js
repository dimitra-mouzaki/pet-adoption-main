import { routes } from '../routes.js';
import { apiCall } from '../apiCalls.js';
  
document.getElementById('loginForm').addEventListener('submit', async e => {
  e.preventDefault();
  const username = e.target.loginUsername.value.trim();
  const password = e.target.loginPassword.value.trim();

  if (username && password) {
    // alert(`Login με username: ${username}`);

    try {
      const response = await apiCall.login(username, password);
      console.log('Login successful:', response);

      localStorage.setItem('userName', response.username);

      let role = 'admin';
      if(response.roles && response.roles.length > 0) {
        switch(response.roles[0]) {
          case 'ROLE_ADMIN':
            role = 'admin';
            break;
          case 'ROLE_VET':
            role = 'vet';
            break;
          case 'ROLE_SHELTER':
            role = 'shelter';
            break;
          case 'ROLE_BASIC':
            role = 'basic';
            break;
          default:
            role = 'admin';
        }
      }

      localStorage.setItem('userRole', role);
      localStorage.setItem('token', response.accessToken);

      e.target.reset();
      window.location.href = routes.homepage;
    }catch (err) {
      alert(err.message);
    }

  } else {
    alert('Fill all field of signin form.');
  }
});

document.getElementById('registerForm').addEventListener('submit', e => {
  e.preventDefault();
  const username = e.target.registerUsername.value.trim();
  const email = e.target.registerEmail.value.trim();
  const password = e.target.registerPassword.value.trim();

  if (username && email && password) {

    localStorage.removeItem('basicInfo');
    localStorage.removeItem('userExtra');
    
    localStorage.setItem('basicInfo', JSON.stringify({ username, email, password }));

    e.target.reset();
    window.location.href = routes.register_as;
  } else {
    alert('Fill all fields of signup form.');
  }
});
