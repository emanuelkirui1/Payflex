import React, { useState } from 'react';

export default function CompanyRegistration() {
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    billingPlan: '',
  });

  const billingPlans = [
    { id: 1, name: 'Basic', price: 100, description: 'Up to 10 employees' },
    { id: 2, name: 'Standard', price: 300, description: 'Up to 50 employees' },
    { id: 3, name: 'Premium', price: 500, description: 'Unlimited employees' },
  ];

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const registerCompany = () => {
    // Mock registration
    alert(`Company ${form.name} registered with ${form.billingPlan} plan`);
    // In real app, send to backend
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Company Registration</h1>
      <form onSubmit={(e) => { e.preventDefault(); registerCompany(); }}>
        <input
          type="text"
          name="name"
          placeholder="Company Name"
          value={form.name}
          onChange={handleInputChange}
          required
        />
        <input
          type="email"
          name="email"
          placeholder="Admin Email"
          value={form.email}
          onChange={handleInputChange}
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleInputChange}
          required
        />
        <select name="billingPlan" value={form.billingPlan} onChange={handleInputChange} required>
          <option value="">Select Billing Plan</option>
          {billingPlans.map(plan => (
            <option key={plan.id} value={plan.name}>{plan.name} - ${plan.price}/month</option>
          ))}
        </select>
        <button type="submit">Register</button>
      </form>
    </div>
  );
}