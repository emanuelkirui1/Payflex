
import React, { useState, useEffect } from 'react';

export default function Payroll() {
  const [employees, setEmployees] = useState([]);
  const [role, setRole] = useState('');
  const [companyId, setCompanyId] = useState(null);

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    kraPin: '',
    basicSalary: '',
    allowances: '',
  });

  const [payslipEmployee, setPayslipEmployee] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      setRole(payload.role);
      setCompanyId(payload.companyId);
      fetchEmployees(payload.role, payload.companyId, token);
    }
  }, []);

  const fetchEmployees = async (userRole, compId, token) => {
    const url = userRole === 'SUPERADMIN' ? 'http://localhost:8081/api/payroll/employees' : `http://localhost:8081/api/payroll/employees/${compId}`;
    try {
      const response = await fetch(url, {
        headers: { 'Authorization': `Bearer ${token}` },
      });
      if (response.ok) {
        const data = await response.json();
        setEmployees(data);
      }
    } catch (error) {
      console.error('Error fetching employees:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const addEmployee = () => {
    if (!form.firstName || !form.lastName || !form.kraPin || !form.basicSalary || !form.allowances) {
      alert('Please fill all fields');
      return;
    }
    const newEmployee = {
      id: Date.now(),
      ...form,
      basicSalary: Number(form.basicSalary),
      allowances: Number(form.allowances),
    };
    setEmployees([...employees, newEmployee]);
    setForm({
      firstName: '',
      lastName: '',
      kraPin: '',
      basicSalary: '',
      allowances: '',
    });
  };

  const calculateGross = (basic, allowances) => basic + allowances;

  const calculateTax = (gross) => (gross > 100000 ? gross * 0.2 : gross * 0.1);

  const calculateNet = (gross, tax) => gross - tax;

  const generatePayslip = (employee) => {
    setPayslipEmployee(employee);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Payroll Management</h1>
      <p>Salary, Tax & Payslip Management</p>

      {/* Employee Management Section */}
      <section style={{ marginBottom: '40px' }}>
        <h2>Add New Employee</h2>
        <form onSubmit={(e) => { e.preventDefault(); addEmployee(); }}>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px', marginBottom: '10px' }}>
            <input
              type="text"
              name="firstName"
              placeholder="First Name"
              value={form.firstName}
              onChange={handleInputChange}
              required
            />
            <input
              type="text"
              name="lastName"
              placeholder="Last Name"
              value={form.lastName}
              onChange={handleInputChange}
              required
            />
            <input
              type="text"
              name="kraPin"
              placeholder="KRA PIN"
              value={form.kraPin}
              onChange={handleInputChange}
              required
            />
            <input
              type="number"
              name="basicSalary"
              placeholder="Basic Salary"
              value={form.basicSalary}
              onChange={handleInputChange}
              required
            />
            <input
              type="number"
              name="allowances"
              placeholder="Allowances"
              value={form.allowances}
              onChange={handleInputChange}
              required
            />
          </div>
          <button type="submit">Add Employee</button>
        </form>
      </section>

      {/* Employee List and Calculations */}
      <section>
        <h2>Employee Payroll Overview</h2>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Name</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>KRA PIN</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Basic Salary</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Allowances</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Gross Pay</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Tax</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Net Pay</th>
              <th style={{ border: '1px solid #ddd', padding: '8px' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {employees.map((emp) => {
              const gross = calculateGross(emp.basicSalary, emp.allowances);
              const tax = calculateTax(gross);
              const net = calculateNet(gross, tax);
              return (
                <tr key={emp.id}>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{emp.firstName} {emp.lastName}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{emp.kraPin}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{emp.basicSalary.toLocaleString()}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{emp.allowances.toLocaleString()}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{gross.toLocaleString()}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{tax.toLocaleString()}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{net.toLocaleString()}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>
                    <button onClick={() => generatePayslip(emp)}>Generate Payslip</button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </section>

      {/* Payslip Section */}
      {payslipEmployee && (
        <section style={{ marginTop: '40px', border: '1px solid #ccc', padding: '20px' }}>
          <h2>Payslip for {payslipEmployee.firstName} {payslipEmployee.lastName}</h2>
          <p><strong>KRA PIN:</strong> {payslipEmployee.kraPin}</p>
          <p><strong>Basic Salary:</strong> {payslipEmployee.basicSalary.toLocaleString()}</p>
          <p><strong>Allowances:</strong> {payslipEmployee.allowances.toLocaleString()}</p>
          <p><strong>Gross Pay:</strong> {calculateGross(payslipEmployee.basicSalary, payslipEmployee.allowances).toLocaleString()}</p>
          <p><strong>Tax:</strong> {calculateTax(calculateGross(payslipEmployee.basicSalary, payslipEmployee.allowances)).toLocaleString()}</p>
          <p><strong>Net Pay:</strong> {calculateNet(calculateGross(payslipEmployee.basicSalary, payslipEmployee.allowances), calculateTax(calculateGross(payslipEmployee.basicSalary, payslipEmployee.allowances))).toLocaleString()}</p>
          <button onClick={() => setPayslipEmployee(null)}>Close Payslip</button>
        </section>
      )}
    </div>
  );
}
