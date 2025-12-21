import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Payroll from './pages/Payroll';
import CompanyRegistration from './pages/CompanyRegistration';
import SuperAdminDashboard from './pages/SuperAdminDashboard';
import AuditLogs from './pages/AuditLogs';

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
        </Routes>
      </div>
    </Router>
  );
}

export default App;