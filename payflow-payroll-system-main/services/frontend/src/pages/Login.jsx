
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleLogin = async () => {
    try {
      const response = await fetch('http://localhost:8081/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: form.email, password: form.password }),
      });
      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('token', data.token);
        // Decode token to get role
        const payload = JSON.parse(atob(data.token.split('.')[1]));
        if (payload.role === 'SUPERADMIN') {
          navigate('/superadmin');
        } else {
          navigate('/dashboard');
        }
      } else {
        alert('Invalid credentials');
      }
    } catch (error) {
      alert('Error logging in');
    }
  };

  return (
    <div>
      <h1>Login</h1>
      <input placeholder="Email" name="email" value={form.email} onChange={handleInputChange} />
      <input type="password" placeholder="Password" name="password" value={form.password} onChange={handleInputChange} />
      <button onClick={handleLogin}>Login</button>
      <p>New company? <a href="/register">Register here</a></p>
    </div>
  );
}
