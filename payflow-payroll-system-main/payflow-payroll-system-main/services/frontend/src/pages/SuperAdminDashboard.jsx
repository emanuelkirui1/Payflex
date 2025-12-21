import React, { useState } from 'react';
import { Link } from 'react-router-dom';

export default function SuperAdminDashboard() {
  const [companies, setCompanies] = useState([
    { id: 1, name: 'Company A', billing: 'Basic', status: 'active' },
    { id: 2, name: 'Company B', billing: 'Premium', status: 'active' },
  ]);

  const [billingPlans, setBillingPlans] = useState([
    { id: 1, name: 'Basic', price: 100 },
    { id: 2, name: 'Standard', price: 300 },
    { id: 3, name: 'Premium', price: 500 },
  ]);

  const deactivateCompany = (id) => {
    setCompanies(companies.map(c => c.id === id ? { ...c, status: 'inactive' } : c));
  };

  const addBillingPlan = () => {
    // Mock add
    alert('Add billing plan');
  };

  return (
    <div style={{ padding: '20px', backgroundColor: 'DarkGrey' }}>
      <h1>SuperAdmin Dashboard</h1>
      <p>Control everything in the system</p>
      <Link to="/payroll">View All Payroll</Link>

      <section>
        <h2>Manage Companies</h2>
        <table border="1" style={{ width: '100%' }}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Billing Plan</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {companies.map(company => (
              <tr key={company.id}>
                <td>{company.name}</td>
                <td>{company.billing}</td>
                <td>{company.status}</td>
                <td>
                  <button onClick={() => deactivateCompany(company.id)}>Deactivate</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      <section>
        <h2>Manage Billing Plans</h2>
        <ul>
          {billingPlans.map(plan => (
            <li key={plan.id}>{plan.name} - ${plan.price}</li>
          ))}
        </ul>
        <button onClick={addBillingPlan}>Add New Plan</button>
      </section>
    </div>
  );
}