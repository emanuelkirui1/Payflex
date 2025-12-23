import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Payroll from './pages/Payroll';
import CompanyRegistration from './pages/CompanyRegistration';
import SuperAdminDashboard from './pages/SuperAdminDashboard';
import AuditLogs from './pages/AuditLogs';
import LeaveRequests from './pages/LeaveRequests';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/payroll" element={<Payroll />} />
          <Route path="/register" element={<CompanyRegistration />} />
          <Route path="/superadmin" element={<SuperAdminDashboard />} />
          <Route path="/audits" element={<AuditLogs />} />
          <Route path="/leaves" element={<LeaveRequests />} />
          <Route path="/profile" element={<Profile />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;