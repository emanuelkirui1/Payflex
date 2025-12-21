
import React from 'react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  return (
    <div>
      <h1>Dashboard</h1>
      <p>Company overview & stats</p>
      <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
        <Link to="/payroll">Go to Payroll</Link>
        {/* Show audit link only to admin/finance/superadmin */}
        {(() => {
          const token = localStorage.getItem('token');
          if (!token) return null;
          try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const role = payload.role;
            if (['ADMIN', 'FINANCE', 'SUPERADMIN'].includes(role)) {
              return <Link to="/audits">View Audit Logs</Link>;
            }
          } catch (e) {
            return null;
          }
          return null;
        })()}
      </div>
    </div>
  );
}
