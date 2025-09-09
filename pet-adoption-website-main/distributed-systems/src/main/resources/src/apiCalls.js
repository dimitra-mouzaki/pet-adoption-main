const API_URL = `http://localhost:8080`;

export const apiCall = {
    // Sign-in
    login: async (username, password) => {
        try {
        const response = await fetch(`${API_URL}/api/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Login failed');
        }

        return await response.json();
        } catch (error) {
        console.error('Error during login:', error);
        throw error;
        }
    },

    // Sign-up Vet
    signupVet: async (basicInfo, userExtra) => {
        try {
        const payload = { ...basicInfo, ...userExtra };
        const response = await fetch(`${API_URL}/api/auth/signup/vet`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        if (!response.ok) {
            const errorData = await parseJSONSafe(response);
            throw new Error(errorData.message || 'Vet registration failed');
        }

        return await parseJSONSafe(response);
        } catch (error) {
        console.error('Error during vet signup:', error);
        throw error;
        }
    },

    // Sign-up Shelter
    signupShelter: async (basicInfo, userExtra) => {
        try {
        const payload = { ...basicInfo, ...userExtra };
        const response = await fetch(`${API_URL}/api/auth/signup/shelter`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        if (!response.ok) {
            const errorData = await parseJSONSafe(response);
            throw new Error(errorData.message || 'Shelter registration failed');
        }

        return await parseJSONSafe(response);
        } catch (error) {
        console.error('Error during shelter signup:', error);
        throw error;
        }
    },

    // Sign-up Citizen
    signupCitizen: async (basicInfo, userExtra) => {
        try {
        const payload = { ...basicInfo, ...userExtra };
        const response = await fetch(`${API_URL}/api/auth/signup/citizen`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        if (!response.ok) {
            const errorData = await parseJSONSafe(response);
            throw new Error(errorData.message || 'Citizen registration failed');
        }

        return await parseJSONSafe(response);
        } catch (error) {
        console.error('Error during citizen signup:', error);
        throw error;
        }
    },

    // Pets
    getAvailablePets: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/pet/available`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return await parseJSONSafe(res);
    },

    getUnhealthyPets: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/pet/unhealthy`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return await parseJSONSafe(res);
    },

    getAllPets: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/pet/all`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return await parseJSONSafe(res);
    },

    getPendingPets: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/pet/pending`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return await parseJSONSafe(res);
    },

    // Pet by ID
    getPetById: async (petId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/pet/${petId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        return await parseJSONSafe(res);
    },

    // Add pet
    addPet: async (petData) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/pet`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(petData)
        });
        
        const data = await parseJSONSafe(res);
        if (!res.ok) {
            throw new Error(data.message || "Failed to add pet");
        }

        return data;
    },

    // Updating pet status
    updatePetStatus: async (petId, status) => {
        const token = localStorage.getItem("token");
        let url, method;

        if (status === "APPROVED") {
            url = `${API_URL}/pet/approve/${petId}`;
            method = "PUT";
        } else if (status === "REJECTED") {
            url = `${API_URL}/pet/reject/${petId}`;
            method = "DELETE";
        } else {
            throw new Error("Invalid status");
        }

        const res = await fetch(url, {
            method,
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) {
            throw new Error(data.message || `Failed to update pet status`);
        }

        return data;
    },

    // Validating pet health
    validatePetHealth: async (petId) => {
        const token = localStorage.getItem("token");
        const res = await fetch(`${API_URL}/pet/validate/${petId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) {
            throw new Error(data.message || "Failed to validate pet health");
        }

        return data;
    },

    // Shelters
    getUnverifiedShelters: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/shelter/unverified`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return await parseJSONSafe(res);
    },

    approveShelter: async (shelterId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/shelter/${shelterId}/approve`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || 'Failed to approve shelter');
        return data;
    },

    rejectShelter: async (shelterId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/shelter/${shelterId}/reject`, {
            method: 'DELETE',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || 'Failed to reject shelter');
        return data;
    },

    scheduleVisit: async (petId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/apt/schedule/${petId}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || "Failed to schedule appointment");
        return data;
    }, 

    adoptPet: async (petId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/requests/submit/${petId}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || "Failed to request adoption");
        return data;
    },

    getUserAppointments: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/apt/myappointments`, {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        return await parseJSONSafe(res);
    },

    getUserAdoptions: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/requests/myrequests`, {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        return await parseJSONSafe(res);
    },

    cancelAppointment: async (appointmentId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/apt/cancel/${appointmentId}`, {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || "Failed to cancel appointment");
        return data;
    },

    cancelAdoptionRequest: async (requestId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/requests/delete/${requestId}`, {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || "Failed to cancel adoption request");
        return data;
    },

    getShelterAppointments: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/apt/shelterappointments`, {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        return await parseJSONSafe(res);
    },

    getShelterRequests: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/requests/shelter`, {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        return await parseJSONSafe(res);
    },

    approveRequest: async (requestId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/requests/approve/${requestId}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || "Failed to approve request");
        return data;
    },

    rejectRequest: async (requestId) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_URL}/requests/delete/${requestId}`, {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || "Failed to reject request");
        return data;
    },

    // Fetch pets with optional filters
    getFilteredPets: async ({ type = '', gender = '', ageRange = '' } = {}) => {
        const token = localStorage.getItem('token');
        
        const params = new URLSearchParams();
        if (type) params.append('type', type.toUpperCase());
        if (gender) params.append('gender', gender.toUpperCase());
        if (ageRange) params.append('ageRange', ageRange.toUpperCase());

        const res = await fetch(`${API_URL}/pet/filter?${params.toString()}`, {
            headers: { 
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        const data = await parseJSONSafe(res);
        if (!res.ok) throw new Error(data.message || 'Failed to fetch filtered pets');
        return data;
    },

}

async function parseJSONSafe(response) {
  const text = await response.text();
  try {
    return text ? JSON.parse(text) : {};
  } catch {
    return { message: text };
  }
}
