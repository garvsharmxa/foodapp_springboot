// Login functionality
let waitingForOTP = false;

document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const otp = document.getElementById('otp').value.trim();
    
    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnLoader = document.getElementById('btnLoader');
    const errorMessage = document.getElementById('errorMessage');
    const otpSection = document.getElementById('otpSection');
    
    // Disable button
    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'block';
    errorMessage.classList.remove('show');
    
    try {
        if (!waitingForOTP) {
            // Step 1: Login to get OTP
            const response = await fetch(`${API_CONFIG.BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });
            
            const data = await response.json();
            
            if (response.ok) {
                if (data.token) {
                    // Hard-core login bypass - log straight in without OTP!
                    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, data.token);
                    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, data.refreshToken);
                    localStorage.setItem(STORAGE_KEYS.USER_INFO, JSON.stringify({ email }));
                    window.location.href = '/admin/dashboard.html';
                } else {
                    // Show OTP section
                    otpSection.style.display = 'block';
                    waitingForOTP = true;
                    btnText.textContent = 'Verify OTP';
                    showError('OTP sent! Check your email or console.', false);
                }
            } else {
                throw new Error(data.message || 'Login failed');
            }
        } else {
            // Step 2: Verify OTP
            console.log('Verifying OTP:', { email, otp });
            console.log('API URL:', `${API_CONFIG.BASE_URL}/auth/verify-login-otp`);
            
            const response = await fetch(`${API_CONFIG.BASE_URL}/auth/verify-login-otp`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, otp })
            });
            
            console.log('Response status:', response.status);
            const data = await response.json();
            console.log('Response data:', data);
            
            if (response.ok && data.token) {
                // Store tokens
                localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, data.token);
                localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, data.refreshToken);
                localStorage.setItem(STORAGE_KEYS.USER_INFO, JSON.stringify({ email }));
                
                // Redirect to dashboard
                window.location.href = '/admin/dashboard.html';
            } else {
                throw new Error(data.message || 'OTP verification failed');
            }
        }
    } catch (error) {
        showError(error.message);
    } finally {
        submitBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
});

function showError(message, isError = true) {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = message;
    errorMessage.style.backgroundColor = isError ? '#fee2e2' : '#d1fae5';
    errorMessage.style.color = isError ? '#991b1b' : '#065f46';
    errorMessage.classList.add('show');
    
    if (!isError) {
        setTimeout(() => {
            errorMessage.classList.remove('show');
        }, 5000);
    }
}

// Check if already logged in
if (localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)) {
    window.location.href = '/admin/dashboard.html';
}
